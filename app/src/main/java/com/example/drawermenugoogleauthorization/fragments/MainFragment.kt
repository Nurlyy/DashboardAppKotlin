package com.example.drawermenugoogleauthorization.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drawermenugoogleauthorization.MainActivity
import com.example.drawermenugoogleauthorization.R
import com.example.drawermenugoogleauthorization.databinding.FragmentMainBinding
import com.example.drawermenugoogleauthorization.notification.NOTIFICATION_ID
import com.example.drawermenugoogleauthorization.notification.NotificationHelper
import com.example.drawermenugoogleauthorization.notification.messageExtra
import com.example.drawermenugoogleauthorization.notification.titleExtra
import com.example.drawermenugoogleauthorization.task.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainFragment(val act: MainActivity) : Fragment(), FbRcAdapter.OnItemClickListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentMainBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var rcView :RecyclerView
    private lateinit var taskArrayList: ArrayList<Task>
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        act.setTitle("All Tasks")
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.addActionButton.setOnClickListener{
            val ft = act.supportFragmentManager.beginTransaction()
            ft.replace(R.id.main_frame_container,
                CreateUpdateTaskFragment(act, null)).commit()
            ft.addToBackStack(null)
        }
        mAuth = FirebaseAuth.getInstance()
        rcView = binding.rcViewTasks
        database = FirebaseDatabase.getInstance().getReference("Tasks/${mAuth.currentUser?.uid}")
        rcView.layoutManager = LinearLayoutManager(act)
        taskArrayList = arrayListOf<Task>()
        getUserData()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(taskArrayList.isEmpty()){
            binding.tvThereIsNoTasks.visibility = View.VISIBLE
        }else{
            binding.tvThereIsNoTasks.visibility = View.GONE
        }
//        taskArrayList.forEach{
//            createNotificationForTask(it)
//        }
    }

    private fun getUserData() {
        val todayDate: Int = SimpleDateFormat("dd").format(Date()).toInt()
        val monthDate: Int = SimpleDateFormat("MM").format(Date()).toInt()
        val yearDate:Int = SimpleDateFormat("yyyy").format(Date()).toInt()
        val todayHour = SimpleDateFormat("HH").format(Date()).toInt()
        val todayMinute = SimpleDateFormat("mm").format(Date()).toInt()

        dbref = FirebaseDatabase.getInstance().getReference("Tasks/${mAuth.currentUser?.uid}")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    taskArrayList.clear()
                    for (taskSnapshot in snapshot.children){
                        val task = taskSnapshot.getValue(Task::class.java)
                        if(task?.isDeleted == false && !task.isDone) {
                            if(task.dayOfMonth!! >= todayDate && task.month!! >= monthDate && task.year!! >= yearDate) {
                                if(todayHour <= task.hour!! && todayMinute <= task.minute!!)
                                    taskArrayList.add(task)
                                else{
                                    task.isDone = true
                                    rcView.adapter?.notifyDataSetChanged()
                                    database.child(task.taskId!!).setValue(task).addOnSuccessListener {
                                        Toast.makeText(act, "One Task Is Done", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                    rcView.adapter = FbRcAdapter(taskArrayList, this@MainFragment)
//                    taskArrayList.forEach{
//                        createNotificationForTask(it)
//                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onItemClick(position: Int) {
        act.supportFragmentManager.beginTransaction().replace(R.id.main_frame_container, CreateUpdateTaskFragment(act, taskArrayList[position])).addToBackStack(null).commit()
//        Toast.makeText(act, "clicked: ${taskArrayList[position].taskId}", Toast.LENGTH_SHORT).show()
    }

    override fun onBtnDeleteClick(position: Int) {
        val tempTask = taskArrayList[position]
        tempTask.isDeleted = true
        updateTask(tempTask, position)
    }

    private fun updateTask(tempTask:Task, position: Int){
        taskArrayList.removeAt(position)
        rcView.adapter?.notifyDataSetChanged()
        database.child(tempTask.taskId!!).setValue(tempTask).addOnSuccessListener {
            Toast.makeText(act, "Successfully removed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBtnFinishClicked(position: Int) {
        val tempTask = taskArrayList[position]
        tempTask.isDone = true
        updateTask(tempTask, position)
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
//
//
//    private fun getTimeInMillis(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long{
//        val calendar = Calendar.getInstance()
//        calendar.set(year, month, day, hour, minute)
//        return calendar.timeInMillis
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun scheduleNotification(title2: String, message: String, time: Long){
//        val intent = Intent(context, NotificationHelper::class.java)
//        intent.putExtra(titleExtra, title2)
//        intent.putExtra(messageExtra, message)
//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            NOTIFICATION_ID,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            time,
//            pendingIntent
//        )
//    }



}