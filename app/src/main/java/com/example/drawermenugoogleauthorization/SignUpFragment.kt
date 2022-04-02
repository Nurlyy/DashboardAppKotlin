package com.example.drawermenugoogleauthorization

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.drawermenugoogleauthorization.databinding.FragmentSignUpBinding

class SignUpFragment(val activity: SignInActivity) : Fragment() {
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.btnSignUp.setOnClickListener {
            if(binding.etUsername.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()){
                if(binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()){
                    binding.etPassword.text.clear()
                    binding.etUsername.text.clear()
                    binding.etConfirmPassword.text.clear()
                    binding.etConfirmPassword.error = "Passwords does not match"
                    binding.etPassword.error = "Passwords does not match"
                }else{
                    activity.signUpWithEmail(binding.etUsername.text.toString(), binding.etPassword.text.toString())
                }
            }
            else{
                binding.etPassword.text.clear()
                binding.etUsername.text.clear()
                binding.etConfirmPassword.text.clear()
                binding.etUsername.error = "Please enter a valid username"
                binding.etPassword.error = "Please enter a valid password"
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }
}