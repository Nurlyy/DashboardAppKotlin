package com.example.drawermenugoogleauthorization

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AccountHelper(val act: AppCompatActivity) {
    private val mAuth = FirebaseAuth.getInstance()

    fun getSignInClient(): GoogleSignInClient {
        val temp = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(act, temp)
    }

    fun logOutFromGoogle(){
        val signInClient = getSignInClient()
        mAuth.signOut()
        signInClient.signOut().addOnCompleteListener{
            Log.d("MyTag", "logOutFromAccount: Signed Out")
            val intent = Intent(act, EmptyActivity::class.java)
            act.startActivity(intent)
            act.finish()
        }
    }


}