package com.example.drawermenugoogleauthorization

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.drawermenugoogleauthorization.databinding.FragmentProfile2Binding
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log10

class FragmentProfile(val act : AppCompatActivity) : Fragment() {
    private lateinit var binding: FragmentProfile2Binding
    private lateinit var accHelper : AccountHelper
    val mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfile2Binding.inflate(inflater, container, false)
        accHelper = AccountHelper(act)
        binding.btnLogOut.setOnClickListener {
            logOutFromAccount()
        }
        return binding.root
    }

    private fun logOutFromAccount(){
        accHelper.logOutFromGoogle()
    }
}