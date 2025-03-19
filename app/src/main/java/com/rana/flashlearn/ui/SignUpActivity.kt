package com.rana.flashlearn.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.rana.flashlearn.SignUpState
import com.rana.flashlearn.SignUpViewModel
import com.rana.flashlearn.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]

        binding.etEmail.addTextChangedListener { validateAndEnableButton() }
        binding.etUsername.addTextChangedListener { validateAndEnableButton() }
        binding.etPassword.addTextChangedListener { validateAndEnableButton() }
        binding.etConfirmPassword.addTextChangedListener { validateAndEnableButton() }

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
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSignUp.isEnabled = false
                }
                is SignUpState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSignUp.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is SignUpState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSignUp.isEnabled = true
                    Toast.makeText(this, state.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateAndEnableButton() {
        val isValid = binding.etEmail.text.toString().trim().isNotEmpty() &&
                binding.etUsername.text.toString().trim().isNotEmpty() &&
                binding.etPassword.text.toString().trim().isNotEmpty() &&
                binding.etConfirmPassword.text.toString().trim().isNotEmpty() &&
                binding.etPassword.text.toString().trim() == binding.etConfirmPassword.text.toString().trim()

        binding.btnSignUp.isEnabled = isValid
    }
}
