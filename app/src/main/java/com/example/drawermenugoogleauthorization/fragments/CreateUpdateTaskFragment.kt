package com.example.drawermenugoogleauthorization.fragments

import android.annotation.SuppressLint
import android.net.Uri
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
import androidx.core.net.toUri
import androidx.core.view.marginTop
import com.example.drawermenugoogleauthorization.MainActivity
import com.example.drawermenugoogleauthorization.R
import com.example.drawermenugoogleauthorization.databinding.FragmentCreateUpdateTaskBinding
import com.example.drawermenugoogleauthorization.task.Task
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

class CreateUpdateTaskFragment(val act: MainActivity, val task: Task?) : Fragment() {
    private var yearInt = 0
    private var monthInt: Int = 0
    private var dayOfMonthInt: Int = 0
    private var hour : Int = 12
    private var minute: Int = 15
    private lateinit var mStorage: FirebaseStorage
    private lateinit var tvUri: TextView
    private lateinit var mAuth : FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding : FragmentCreateUpdateTaskBinding
    private lateinit var etName: TextView
    private lateinit var etDesc: TextView
    private lateinit var date: CalendarView
    private lateinit var time: TextView
    private lateinit var selectFile: Button
    private lateinit var notification: CheckBox
    private var isImageDeleteClicked = false
    private var fileUri: Uri? = null
    private var fileDownUri: String?= null
    private var isDone: Boolean = false
    private var isDeleted: Boolean = false
    private lateinit var picker: MaterialTimePicker
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateUpdateTaskBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Tasks/${mAuth.currentUser?.uid}")

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



        // Inflate the layout for this fragment
        return binding.root
    }

    private fun updateClassTask(){
        if(task != null){
            etName = binding.etTaskName
            etDesc = binding.etTaskDesc
            date = binding.calendarView
            time = binding.selectedTimeHour
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
            val tempCal = Calendar.getInstance()
            tempCal.set(yearInt, monthInt, dayOfMonthInt)
            date.setDate(tempCal.timeInMillis, true, true)
            date.setOnDateChangeListener { calendarView, i, i2, i3 ->
                val calendar = Calendar.getInstance()
                calendar.set(i, i2, i3)
                calendarView.setDate(calendar.timeInMillis, true, true)
                Log.d("MyTag", "Selected Date: $i3/${i2+1}/$i")
            }
            time.text = "$hour : $minute"
            time.setOnClickListener {
                showTimePicker()
            }
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
        date = binding.calendarView
        date.setOnDateChangeListener { calView: CalendarView, year: Int, month: Int, dayOfMonth: Int ->
            val calender: Calendar = Calendar.getInstance()
            calender.set(year, month, dayOfMonth)
            calView.setDate(calender.timeInMillis, true, true)
            Log.d("MyTag", "Selected Date: $dayOfMonth/${month+1}/$year")
        }
        time = binding.selectedTimeHour
        time.text = "$hour : $minute"
        time.setOnClickListener {
            showTimePicker()
        }
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

    private fun updateTask(){
        if(etName.text.isNotEmpty()){
            val taskid: String? = task?.taskId
            val dateMillis: Long = binding.calendarView.date
            val date: Date = Date(dateMillis)
            yearInt = (DateFormat.format("yyyy", date) as String).toInt()
            monthInt = (DateFormat.format("MM", date) as String).toInt()
            dayOfMonthInt = (DateFormat.format("dd", date) as String).toInt()
            if(isImageDeleteClicked){
                val pictureRef = mStorage.getReferenceFromUrl(task?.file!!)
                pictureRef.delete()
            }
            val task = Task(taskid, etName.text.toString(), etDesc.text.toString(), yearInt, monthInt, dayOfMonthInt, hour, minute, notification.isChecked, fileDownUri)
            database.child(taskid.toString()).setValue(task).addOnSuccessListener {
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

    private fun onFirstSaveClicked(){
        if(etName.text.isNotEmpty()){
            val taskid: String? = database.push().key
            getCalendarDate()
            if(isImageDeleteClicked){
                val pictureRef = mStorage.getReferenceFromUrl(fileDownUri!!)
                pictureRef.delete()
            }
            val task = Task(taskid, etName.text.toString(), etDesc.text.toString(), yearInt, monthInt, dayOfMonthInt, hour, minute, notification.isChecked, fileDownUri)
            database.child(taskid.toString()).setValue(task).addOnSuccessListener {
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

    private fun getCalendarDate(){
        val dateMillis: Long = binding.calendarView.date
        val date: Date = Date(dateMillis)
        yearInt = (DateFormat.format("yyyy", date) as String).toInt()
        monthInt = (DateFormat.format("MM", date) as String).toInt()
        dayOfMonthInt = (DateFormat.format("dd", date) as String).toInt()
    }



    @SuppressLint("SetTextI18n")
    private fun showTimePicker(){
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText("Select task time")
            .build()
        picker.show(act.supportFragmentManager, "nurlytan")
        picker.addOnPositiveButtonClickListener {
            hour = picker.hour
            minute = picker.minute
            time.text = "$hour : $minute"
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

}