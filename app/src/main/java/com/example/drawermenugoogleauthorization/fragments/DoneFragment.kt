package com.example.drawermenugoogleauthorization.fragments

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drawermenugoogleauthorization.MainActivity
import com.example.drawermenugoogleauthorization.R
import com.example.drawermenugoogleauthorization.databinding.FragmentDoneBinding
import com.example.drawermenugoogleauthorization.databinding.FragmentMainBinding
import com.example.drawermenugoogleauthorization.databinding.FragmentTodayBinding
import com.example.drawermenugoogleauthorization.task.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DoneFragment(val act: MainActivity) : Fragment(), FbDoneRcAdapter.OnItemClickListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentDoneBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var rcView : RecyclerView
    private lateinit var taskArrayList: ArrayList<Task>
    private lateinit var database: DatabaseReference
    private var currentDay :Int? = null
    private var currentMonth:Int? = null
    private var currentYear:Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        act.setTitle("Done Tasks")
        binding = FragmentDoneBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()
        rcView = binding.rcViewTasks
        database = FirebaseDatabase.getInstance().getReference("Tasks/${mAuth.currentUser?.uid}")
        rcView.layoutManager = LinearLayoutManager(act)
        taskArrayList = arrayListOf<Task>()
        getUserData()
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(taskArrayList.isEmpty()){
            binding.tvThereIsNoTasks.visibility = View.VISIBLE
        }else{
            binding.tvThereIsNoTasks.visibility = View.GONE
        }
        currentDay = SimpleDateFormat("dd").format(Date()).toInt()
        currentMonth = SimpleDateFormat("MM").format(Date()).toInt()
        currentYear = SimpleDateFormat("yyyy").format(Date()).toInt()
    }

    @SuppressLint("SimpleDateFormat")
    private fun getUserData() {
        dbref = FirebaseDatabase.getInstance().getReference("Tasks/${mAuth.currentUser?.uid}")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    taskArrayList.clear()
                    for (taskSnapshot in snapshot.children){
                        val task = taskSnapshot.getValue(Task::class.java)
                        if(task?.isDone!!) {
                            taskArrayList.add(task)
                        }
                    }
                    rcView.adapter = FbDoneRcAdapter(taskArrayList, this@DoneFragment)
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

    private fun updateTask(tempTask: Task, position: Int){
        taskArrayList.removeAt(position)
        rcView.adapter?.notifyDataSetChanged()
        database.child(tempTask.taskId!!).setValue(tempTask).addOnSuccessListener {
            Toast.makeText(act, "Successfully removed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBtnActivateClicked(position: Int) {
        val tempTask = taskArrayList[position]
        tempTask.isDone = false
        tempTask.isDeleted = false
        tempTask.dayOfMonth = currentDay?.plus(1)
        tempTask.month = currentMonth
        tempTask.year = currentYear
        updateTask(tempTask, position)
    }
}