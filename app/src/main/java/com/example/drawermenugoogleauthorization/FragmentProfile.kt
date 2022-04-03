package com.example.drawermenugoogleauthorization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import com.example.drawermenugoogleauthorization.databinding.FragmentProfile2Binding
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlin.math.log10

class FragmentProfile(val act : MainActivity) : Fragment() {
    private lateinit var tvUsername : TextView
    private lateinit var tvEmail : TextView
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
        if(mAuth.currentUser?.photoUrl!= null){
            Picasso.get().load(mAuth.currentUser!!.photoUrl).into(binding.imageProfile)
        }else {
            Picasso.get().load("https://pic.onlinewebfonts.com/svg/img_458488.png")
                .into(binding.imageProfile)
        }

        tvUsername = binding.etUsername
        tvEmail = binding.etEmail

        tvEmail.text = mAuth.currentUser?.email
        tvUsername.text = mAuth.currentUser?.displayName.toString()

        tvUsername.addTextChangedListener{
            binding.btnSaveChanges.visibility = View.VISIBLE
        }

        binding.imageProfile.setOnClickListener {
            act.pickImageFromGallery()
        }

        binding.btnSaveChanges.setOnClickListener {
            Log.d("MyTag", "onCreateView: ${act.isImageChanged}")
            if(act.isImageChanged){
                act.saveChangesToFirebase(tvUsername.text.toString(), mAuth.currentUser, act.imageUri)
            }
            else{
                act.saveChangesToFirebase(tvUsername.text.toString(), mAuth.currentUser)
            }

        }

        return binding.root
    }

    private fun logOutFromAccount(){
        accHelper.logOutFromGoogle()
    }
}