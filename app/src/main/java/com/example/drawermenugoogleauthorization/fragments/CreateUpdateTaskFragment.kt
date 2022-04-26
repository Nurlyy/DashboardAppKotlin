package com.example.drawermenugoogleauthorization.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.text.format.DateFormat
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.core.view.doOnAttach
import androidx.core.view.marginTop
import com.example.drawermenugoogleauthorization.MainActivity
import com.example.drawermenugoogleauthorization.R
import com.example.drawermenugoogleauthorization.databinding.FragmentCreateUpdateTaskBinding
import com.example.drawermenugoogleauthorization.notification.NOTIFICATION_ID
import com.example.drawermenugoogleauthorization.notification.NotificationHelper
import com.example.drawermenugoogleauthorization.notification.messageExtra
import com.example.drawermenugoogleauthorization.notification.titleExtra
import com.example.drawermenugoogleauthorization.task.Task
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*
import kotlin.math.min

class CreateUpdateTaskFragment(val act: MainActivity, val task: Task?) : Fragment() {
    private var notificationid = 0
    private var yearInt = 0
    private var monthInt: Int = 0
    private var dayOfMonthInt: Int = 0
    private var hour : Int = 0
    private var minute : Int = 0
    private lateinit var mStorage: FirebaseStorage
    private lateinit var tvUri: TextView
    private lateinit var mAuth : FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding : FragmentCreateUpdateTaskBinding
    private lateinit var etName: TextView
    private lateinit var etDesc: TextView
    private lateinit var date: DatePicker
    private lateinit var time: TimePicker
    private lateinit var selectFile: Button
    private lateinit var notification: CheckBox
    private var isImageDeleteClicked = false
    private var fileUri: Uri? = null
    private var fileDownUri: String?= null
    private val pickFile  = registerForActivityResult(
        ActivityResultContracts.GetContent(), ActivityResultCallback {
            if(it!=null) {
                fileUri = it
                uploadFile(fileUri!!)
                //upload
                Log.d("MyTag", "file URI : $fileUri ")
            }
        }
    )

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        act.setTitle("Edit Task")
        binding = FragmentCreateUpdateTaskBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Tasks/${mAuth.currentUser?.uid}")

        if(mAuth.currentUser?.isEmailVerified == true){
            if(task == null){
                init()
                binding.btnSaveTask.setOnClickListener {
                    onFirstSaveClicked()
                }
                binding.ivDelete.setOnClickListener {
                    onImageDeleteClicked()
                }
            }else{
                updateClassTask()
            }
        }else{
            init()
             binding.btnPickFile.setOnClickListener {
                 Toast.makeText(act, "You must verify your email", Toast.LENGTH_SHORT).show()
             }
            binding.btnSaveTask.setOnClickListener{
                Toast.makeText(act, "You must verify your email", Toast.LENGTH_SHORT).show()
            }
        }




        // Inflate the layout for this fragment
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateClassTask(){
        if(task != null){
            etName = binding.etTaskName
            etDesc = binding.etTaskDesc
            date = binding.datePicker
            time = binding.timePicker
            if(task.file != null){
                fileDownUri = task.file
                binding.linearLayoutUri.visibility = View.VISIBLE
                tvUri = binding.tvFileUri
                tvUri.text = fileDownUri
            }
            hour = task!!.hour!!
            minute = task!!.minute!!
            yearInt = task!!.year!!
            monthInt = task!!.month!!-1
            dayOfMonthInt = task!!.dayOfMonth!!
            etName.text = task!!.title
            etDesc.text = task!!.description
            date.init(yearInt, monthInt+1, dayOfMonthInt, DatePicker.OnDateChangedListener { datePicker, i, i2, i3 ->})
            time.hour = hour
            time.minute = minute
            selectFile = binding.btnPickFile
            selectFile.setOnClickListener {
                if(task.file == null){
                    chooseFile()
                }
            }
            notification = binding.checkBoxNotification
            notification.isChecked = task!!.notification
            binding.ivDelete.setOnClickListener {
                onImageDeleteClicked()
            }
            binding.btnSaveTask.setOnClickListener {
                updateTask()
            }

        }
    }

    private fun init(){
        etName = binding.etTaskName
        etDesc = binding.etTaskDesc
        date = binding.datePicker
        time = binding.timePicker
        selectFile = binding.btnPickFile
        selectFile.setOnClickListener {
            if(fileDownUri == null){
                chooseFile()
            }
        }
        notification = binding.checkBoxNotification
    }

    private fun onImageDeleteClicked(){
        if(fileDownUri != null) {
            isImageDeleteClicked = true
            binding.linearLayoutUri.visibility = View.GONE
            binding.tvFileUri.text = ""
            fileDownUri = null
            fileUri = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateTask(){
        if(etName.text.isNotEmpty()){
            val taskid: String? = task?.taskId
            yearInt = binding.datePicker.year
            monthInt = binding.datePicker.month
            dayOfMonthInt = binding.datePicker.dayOfMonth
            hour = time.hour
            minute = time.minute
            if(isImageDeleteClicked){
                val pictureRef = mStorage.getReferenceFromUrl(task?.file!!)
                pictureRef.delete()
            }
            notificationid = task?.notificationID!!
            val tempcal = Calendar.getInstance()
            tempcal.set(yearInt, monthInt, dayOfMonthInt, hour, minute)
            if(notification.isChecked){
                scheduleNotification(etName.text.toString(), etDesc.text.toString(), tempcal.timeInMillis, notificationid)
            }else{
                cancelNotification(notificationid)
            }
            val taskUpdated = Task(taskid, etName.text.toString(), etDesc.text.toString(), yearInt, monthInt, dayOfMonthInt, hour, minute, notification.isChecked, notificationid, fileDownUri)
            database.child(taskid.toString()).setValue(taskUpdated).addOnSuccessListener {
                Toast.makeText(act, "Task Saved Successfully", Toast.LENGTH_SHORT).show()
                Log.d("MyTag", "onCreateView: Selected date: $dayOfMonthInt/$monthInt/$yearInt")
                act.supportFragmentManager.beginTransaction().replace(R.id.main_frame_container, MainFragment(act)).commit()
            }.addOnFailureListener{
                Toast.makeText(act, "Something went wrong", Toast.LENGTH_SHORT).show()
                act.supportFragmentManager.beginTransaction().replace(R.id.main_frame_container, MainFragment(act)).commit()
                Log.d("MyTag", "onCreateView: $it")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun onFirstSaveClicked(){
        if(etName.text.isNotEmpty()){
            val taskid: String? = database.push().key
            yearInt = binding.datePicker.year
            monthInt = binding.datePicker.month+1
            dayOfMonthInt = binding.datePicker.dayOfMonth
            hour = time.hour
            minute = time.minute
            val tempcal = Calendar.getInstance()
            tempcal.set(yearInt, monthInt-1, dayOfMonthInt, hour, minute)
            if(isImageDeleteClicked){
                val pictureRef = mStorage.getReferenceFromUrl(fileDownUri!!)
                pictureRef.delete()
            }
            notificationid = createNotificationID()
            if(notification.isChecked){
                scheduleNotification(etName.text.toString(), etDesc.text.toString(), tempcal.timeInMillis, notificationid)
            }
            val task = Task(taskid, etName.text.toString(), etDesc.text.toString(), yearInt, monthInt, dayOfMonthInt, hour, minute, notification.isChecked, notificationid, fileDownUri)
            database.child(taskid.toString()).setValue(task).addOnSuccessListener {
                Toast.makeText(act, "Task Saved Successfully", Toast.LENGTH_SHORT).show()
                Log.d("MyTag", "onCreateView: Selected date: $dayOfMonthInt/$monthInt/$yearInt $hour:$minute")
                act.supportFragmentManager.beginTransaction().replace(R.id.main_frame_container, MainFragment(act)).commit()
            }.addOnFailureListener{
                Toast.makeText(act, "Something went wrong", Toast.LENGTH_SHORT).show()
                act.supportFragmentManager.beginTransaction().replace(R.id.main_frame_container, MainFragment(act)).commit()
                Log.d("MyTag", "onCreateView: $it")
            }
        }
    }

    private fun chooseFile(){
        pickFile.launch("*/*")
    }

    private fun uploadFile(uri: Uri){
        val storage = mStorage.getReference("taskFiles/${mAuth.currentUser?.uid}")
            .child("${uri.lastPathSegment}")
        val upload = storage.putFile(uri)
        binding.btnSaveTask.isClickable = false
        upload.addOnSuccessListener {taskSnapshot: UploadTask.TaskSnapshot? ->
            storage.downloadUrl.addOnSuccessListener { uri->
                binding.btnSaveTask.isClickable = true
                binding.linearLayoutUri.visibility = View.VISIBLE
                tvUri = binding.tvFileUri
                isImageDeleteClicked = false
                tvUri.text = uri.toString()
                fileDownUri = uri.toString()
                Log.d("MyTag", "uploadFile: Success; ${fileDownUri}")
            }

            Toast.makeText(act, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            Log.d("MyTag", "uploadFile: $it")
        }
    }

    private fun createNotificationID():Int{
        return java.util.Random().nextInt(1000000)
    }

//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun createNotificationForTask(task: Task){
//        if(task.notification) {
//            val tempTime = getTimeInMillis(
//                task.year!!,
//                task.month!!,
//                task.dayOfMonth!!,
//                task.hour!!,
//                task.minute!!
//            )
//            scheduleNotification(task.title!!, task.description!!, tempTime)
//        }
//    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification(title2: String, message: String, time: Long, notificationID:Int){
        val intent = Intent(context, NotificationHelper::class.java)
        intent.putExtra(titleExtra, title2)
        intent.putExtra(messageExtra, message)
        intent.putExtra(NOTIFICATION_ID, notificationID)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        Log.d("MyTag", "scheduleNotification: NOTIFICATION SCHEDULED")
    }
    private fun cancelNotification(notificationID: Int){
        val intent = Intent(context, NotificationHelper::class.java)
        intent.putExtra(NOTIFICATION_ID, notificationID.toString())
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationID,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

}