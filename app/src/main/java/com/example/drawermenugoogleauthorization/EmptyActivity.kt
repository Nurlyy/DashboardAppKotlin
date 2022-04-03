package com.example.drawermenugoogleauthorization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.drawermenugoogleauthorization.SignUpIn.SignInActivity
import com.google.firebase.auth.FirebaseAuth

class EmptyActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        Handler().postDelayed({
            if(mAuth.currentUser != null){
                startActivity(Intent(this, MainActivity::class.java))
                this.finish()
            }
            else{
                startActivity(Intent(this, SignInActivity::class.java))
                this.finish()
            }
        }, 2000)

        setContentView(R.layout.activity_empty)
    }
}