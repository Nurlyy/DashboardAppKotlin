package com.example.drawermenugoogleauthorization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.example.drawermenugoogleauthorization.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var mAuth : FirebaseAuth
    private lateinit var binding : ActivityMainBinding
    private lateinit var tvAccount : TextView
    private lateinit var ivAccount: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.mainToolbar.toolbar, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        if(mAuth.currentUser != null){
            tvAccount = binding.navView.getHeaderView(0).findViewById(R.id.tvEmail)
            tvAccount.text = mAuth.currentUser!!.email.toString()
            ivAccount = binding.navView.getHeaderView(0).findViewById(R.id.imageView)
            Picasso.get().load(mAuth.currentUser!!.photoUrl).into(ivAccount)
        }
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
}