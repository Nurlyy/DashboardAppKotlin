package com.example.drawermenugoogleauthorization

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.drawermenugoogleauthorization.databinding.ActivityMainBinding
import com.example.drawermenugoogleauthorization.fragments.DoneFragment
import com.example.drawermenugoogleauthorization.fragments.FragmentProfile
import com.example.drawermenugoogleauthorization.fragments.MainFragment
import com.example.drawermenugoogleauthorization.fragments.TodayFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var mAuth : FirebaseAuth
    private lateinit var binding : ActivityMainBinding
    private lateinit var tvAccount : TextView
    private lateinit var ivAccount: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

            R.id.profile -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame_container, FragmentProfile(this)).commit()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}