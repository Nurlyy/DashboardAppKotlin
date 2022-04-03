package com.example.drawermenugoogleauthorization.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.drawermenugoogleauthorization.R
import com.example.drawermenugoogleauthorization.databinding.FragmentCreateUpdateTaskBinding

class CreateUpdateTaskFragment : Fragment() {
    private lateinit var binding : FragmentCreateUpdateTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateUpdateTaskBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

}