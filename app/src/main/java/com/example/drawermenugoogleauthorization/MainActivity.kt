package com.example.drawermenugoogleauthorization

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.example.drawermenugoogleauthorization.databinding.ActivityMainBinding
import com.example.drawermenugoogleauthorization.databinding.FragmentProfile2Binding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var launcher: ActivityResultLauncher<Intent>
    var isImageChanged = false
    lateinit var imageUri: String
    lateinit var mAuth : FirebaseAuth
    private lateinit var binding : ActivityMainBinding
    private lateinit var tvAccount : TextView
    private lateinit var ivAccount: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
//                Log.d("MyTag", "onCreate: ${it.data}")
            }
        }
        Log.d("MyTag", "onCreate: ${mAuth.currentUser?.photoUrl}")
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.mainToolbar.toolbar, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        if(mAuth.currentUser != null){
            tvAccount = binding.navView.getHeaderView(0).findViewById(R.id.tvEmail)
            tvAccount.text = mAuth.currentUser!!.email.toString()
            ivAccount = binding.navView.getHeaderView(0).findViewById(R.id.imageView)
            if(mAuth.currentUser!!.photoUrl!= null){
                Picasso.get().load(mAuth.currentUser!!.photoUrl).into(ivAccount)
            }else{
                Picasso.get().load("https://pic.onlinewebfonts.com/svg/img_458488.png").into(ivAccount)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK) {
            val imageProfile: ImageView = findViewById(R.id.imageProfile)
            imageProfile.setImageURI(data?.data)
            isImageChanged = true
            imageUri = data?.data!!.toString()
            Log.d("MyTag", "onActivityResult: ${data.data}")
            Log.d("MyTag", "onActivityResult: ${data.dataString}")
        }
    }

    fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        val saveBtn = findViewById<Button>(R.id.btnSaveChanges)
        saveBtn.visibility = View.VISIBLE
        intent.type = "image/*"
        launcher.launch(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.all_tasks -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame_container, MainFragment()).commit()
            }
            R.id.profile -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame_container, FragmentProfile(this)).commit()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun saveChangesToFirebase(username: String, user: FirebaseUser?, profImageUri: String? = mAuth.currentUser?.photoUrl.toString()){
        val profileUpdates = userProfileChangeRequest {
            displayName = username
            photoUri = Uri.parse(profImageUri)
        }
        Log.d("MyTag", "saveChangesToFirebase: ${profImageUri}")

        user!!.updateProfile(profileUpdates).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show()
            }
            else{
                Log.d("MyTag", "${it.exception}")
            }
        }
    }
}