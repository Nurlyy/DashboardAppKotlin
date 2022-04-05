package com.example.drawermenugoogleauthorization.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import com.example.drawermenugoogleauthorization.AccountHelper
import com.example.drawermenugoogleauthorization.MainActivity
import com.example.drawermenugoogleauthorization.databinding.FragmentProfile2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class FragmentProfile(val act : MainActivity) : Fragment() {
    private lateinit var tvUsername : TextView
    private lateinit var tvEmail : TextView
    private lateinit var imageUri: Uri
    private val DEFAULT_IMAGE_URL = "https://pic.onlinewebfonts.com/svg/img_458488.png"
    @SuppressLint("NewApi")
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            if(it != null){
                val source = ImageDecoder.createSource(act.contentResolver, it)
                val bitmap = ImageDecoder.decodeBitmap(source)
                binding.btnSaveChanges.visibility = View.VISIBLE
                uploadImageAndSaveUri(bitmap)
            }
        }
    )
    private lateinit var binding: FragmentProfile2Binding
    private lateinit var accHelper : AccountHelper
    val mAuth = FirebaseAuth.getInstance()


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
            Picasso.get().load(DEFAULT_IMAGE_URL)
                .into(binding.imageProfile)
        }

        tvUsername = binding.etUsername
        tvEmail = binding.etEmail

        tvEmail.text = mAuth.currentUser?.email
        tvUsername.text = mAuth.currentUser?.displayName.toString()

        tvUsername.addTextChangedListener{
            binding.btnSaveChanges.visibility = View.VISIBLE
        }

        binding.imageProfile.setOnClickListener{
            pickImage.launch("image/*")
        }

        binding.btnSaveChanges.setOnClickListener {
            val photo = when{
                ::imageUri.isInitialized -> imageUri
                mAuth.currentUser?.photoUrl == null -> Uri.parse(DEFAULT_IMAGE_URL)
                else->mAuth.currentUser?.photoUrl
            }
            val name = binding.etUsername.text.toString()

            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photo)
                .build()
            mAuth.currentUser?.updateProfile(updates)?.addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(act, "User updated", Toast.LENGTH_SHORT).show()
                }
                else{
                    Log.d("MyTag", "onCreateView: ${it.exception}")
                }
            }
        }
        return binding.root
    }

    private fun logOutFromAccount(){
        accHelper.logOutFromGoogle()
    }

    private fun uploadImageAndSaveUri(imageBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("pics/${mAuth.currentUser?.uid}")
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val image = baos.toByteArray()
        val upload = storageRef.putBytes(image)
        binding.progressBar.visibility = View.VISIBLE
        upload.addOnCompleteListener{uploadTask->
            binding.progressBar.visibility = View.GONE
            if(uploadTask.isSuccessful){
                storageRef.downloadUrl.addOnCompleteListener{urlTask->
                    urlTask.result?.let {
                        imageUri = it
                        binding.imageProfile.setImageBitmap(imageBitmap)
                        act.updateUi(imageBitmap)
                    }
                }
            }
            else{
                Log.d("MyTag", "uploadImageAndSaveUri: ${uploadTask.exception}")
            }
        }
    }

}