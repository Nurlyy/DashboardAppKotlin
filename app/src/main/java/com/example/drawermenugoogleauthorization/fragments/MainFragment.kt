package com.example.drawermenugoogleauthorization.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.drawermenugoogleauthorization.MainActivity
import com.example.drawermenugoogleauthorization.R
import com.example.drawermenugoogleauthorization.databinding.FragmentMainBinding

class MainFragment(val act: MainActivity) : Fragment() {
    private lateinit var binding: FragmentMainBinding

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
        // Inflate the layout for this fragment
        return binding.root
    }

}