package com.example.drawermenugoogleauthorization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.drawermenugoogleauthorization.databinding.ActivitySignInBinding
import com.example.drawermenugoogleauthorization.databinding.FragmentSignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class SignInActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivitySignInBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var launcher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            val task  = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if(account != null){
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            }catch (e: ApiException){
                Log.d("MyTag", "onCreate: ${e}")
            }
        }
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.signUpInFragmentContainer, SignInFragment(this)).commit()
    }

    fun signInWithEmail(email: String, password: String){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this.baseContext, MainActivity::class.java))
                this.finish()
            }
            else{
                Toast.makeText(this, "Can not to sign in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signUpWithEmail(email: String, password: String){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            if(it.isSuccessful){
                sendEmailVerification(it.result.user!!)
            }
            else{
                Toast.makeText(this, "Error signing up", Toast.LENGTH_SHORT).show()
                Log.d("MyTag", "signUpWithEmail: ${it.exception}")
            }
        }
    }
    
    fun sendRestEmail(email: String){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(this, "Password reset email message was sent", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Can not send password reset message to email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser){
        user.sendEmailVerification().addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(this, "Verification message sent to your email", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this.baseContext, MainActivity::class.java))
                this.finish()
            }
            else{
                Toast.makeText(this, "Error sending verification message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getSignInClient(): GoogleSignInClient {
        val temp = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        return GoogleSignIn.getClient(this, temp)
    }

    fun signInWithGoogle(){
        googleSignInClient = getSignInClient()
        launcher.launch(googleSignInClient.signInIntent)
    }

    fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(this, "Sign In Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this.baseContext, MainActivity::class.java))
                this.finish()
            }
            else{
                Toast.makeText(this, "Error Signing In", Toast.LENGTH_SHORT).show()
            }
        }
    }
}