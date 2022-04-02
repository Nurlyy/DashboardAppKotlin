package com.example.drawermenugoogleauthorization

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.drawermenugoogleauthorization.databinding.FragmentSignInBinding

class SignInFragment(val activity: SignInActivity) : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        binding.btnSignIn.setOnClickListener { 
            if(binding.etUsername.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()){
                activity.signInWithEmail(binding.etUsername.text.toString(), binding.etPassword.text.toString())
            }else {
                binding.etPassword.text.clear()
                binding.etUsername.text.clear()
                binding.etUsername.error = "Please enter username"
                binding.etPassword.error = "Please enter password"
            }
        }

        binding.forgetpasswordTextView.setOnClickListener {
            if(binding.etUsername.text.isNotEmpty()) {
                activity.sendRestEmail(binding.etUsername.text.toString())
            }else{
                binding.etUsername.error = "Please enter username"
            }
        }

        binding.btnSignInWithGoogle.setOnClickListener {
            activity.signInWithGoogle()
        }

        binding.tvRegistration.setOnClickListener {
            val ft = activity.supportFragmentManager.beginTransaction()
            ft.replace(R.id.signUpInFragmentContainer, SignUpFragment(activity)).commit()
            ft.addToBackStack(null)
        }
        return binding.root
    }
}