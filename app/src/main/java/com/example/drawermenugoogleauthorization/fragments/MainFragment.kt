package com.example.drawermenugoogleauthorization.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drawermenugoogleauthorization.MainActivity
import com.example.drawermenugoogleauthorization.R
import com.example.drawermenugoogleauthorization.databinding.FragmentMainBinding
import com.example.drawermenugoogleauthorization.task.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class MainFragment(val act: MainActivity) : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentMainBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var rcView :RecyclerView
    private lateinit var taskArrayList: ArrayList<Task>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.addActionButton.setOnClickListener{
            val ft = act.supportFragmentManager.beginTransaction()
            ft.replace(R.id.main_frame_container,
                CreateUpdateTaskFragment(act)).commit()
            ft.addToBackStack(null)
        }
        mAuth = FirebaseAuth.getInstance()
        rcView = binding.rcViewTasks
        rcView.layoutManager = LinearLayoutManager(act)
        taskArrayList = arrayListOf<Task>()
        getUserData()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getUserData() {
        dbref = FirebaseDatabase.getInstance().getReference("Tasks/${mAuth.currentUser?.uid}")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (taskSnapshot in snapshot.children){
                        val task = taskSnapshot.getValue(Task::class.java)
                        taskArrayList.add(task!!)
                    }
                    rcView.adapter = FbRcAdapter(taskArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}