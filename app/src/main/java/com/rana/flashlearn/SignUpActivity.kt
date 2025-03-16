package com.rana.flashlearn

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.rana.flashlearn.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()

            viewModel.registerUser(email, password, confirmPassword, username)
        }

        viewModel.signUpResult.observe(this) { state ->
            when (state) {
                is SignUpState.Loading -> {
                    binding.progressBar.visibility = android.view.View.VISIBLE
                    binding.btnSignUp.isEnabled = false
                }
                is SignUpState.Success -> {
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.btnSignUp.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is SignUpState.Error -> {
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.btnSignUp.isEnabled = true
                    Toast.makeText(this, state.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}