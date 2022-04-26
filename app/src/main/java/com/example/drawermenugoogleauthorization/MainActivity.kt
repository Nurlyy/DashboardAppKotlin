package com.example.drawermenugoogleauthorization

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.drawermenugoogleauthorization.databinding.ActivityMainBinding
import com.example.drawermenugoogleauthorization.fragments.*
import com.example.drawermenugoogleauthorization.notification.CHANNEL_ID
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var mAuth : FirebaseAuth
    private lateinit var binding : ActivityMainBinding
    private lateinit var tvAccount : TextView
    private lateinit var ivAccount: ImageView
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()
        supportFragmentManager.beginTransaction().replace(R.id.main_frame_container, MainFragment(this)).commit()
        mAuth = FirebaseAuth.getInstance()
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

    fun setTitle(title: String){
        binding.mainToolbar.toolbar.title = title
    }

    fun updateUi(imageBitmap: Bitmap){
        ivAccount.setImageBitmap(imageBitmap)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.all_tasks -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame_container, MainFragment(this)).commit()
            }

            R.id.today_tasks -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame_container, TodayFragment(this)).commit()
            }

            R.id.done_tasks -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame_container, DoneFragment(this)).commit()
            }

            R.id.deleted_tasks -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame_container, DelededFragment(this)).commit()
            }

            R.id.profile -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame_container, FragmentProfile(this)).commit()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        val name = "Notification Channel"
        val desc = "Notification Description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = desc
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}