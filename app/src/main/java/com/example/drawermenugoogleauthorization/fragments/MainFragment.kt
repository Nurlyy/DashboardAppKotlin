package com.example.drawermenugoogleauthorization.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drawermenugoogleauthorization.MainActivity
import com.example.drawermenugoogleauthorization.R
import com.example.drawermenugoogleauthorization.databinding.FragmentMainBinding
import com.example.drawermenugoogleauthorization.task.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(taskArrayList.isEmpty()){
            binding.tvThereIsNoTasks.visibility = View.VISIBLE
        }else{
            binding.tvThereIsNoTasks.visibility = View.GONE
        }
    }

    private fun getUserData() {
        dbref = FirebaseDatabase.getInstance().getReference("Tasks/${mAuth.currentUser?.uid}")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    taskArrayList.clear()
                    for (taskSnapshot in snapshot.children){
                        val task = taskSnapshot.getValue(Task::class.java)
                        if(task?.isDeleted == false && !task.isDone) {
                            taskArrayList.add(task!!)
                        }
                    }
                    rcView.adapter = FbRcAdapter(taskArrayList, this@MainFragment)
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



}