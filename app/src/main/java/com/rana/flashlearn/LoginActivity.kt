package com.rana.flashlearn

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.rana.flashlearn.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch
import android.util.Log
import com.rana.flashlearn.ui.MainActivity
import com.rana.flashlearn.ui.SignUpActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authRepository: AuthRepository
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authRepository = AuthRepository(FirebaseAuth.getInstance())
        sharedPrefManager = SharedPrefManager.getInstance(this)

        if (authRepository.isUserLoggedIn()) {
            navigateToMainActivity()
            return
        }

        setupGoogleSignIn()
        setupClickListeners()
        setupGoogleSignInLauncher()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupGoogleSignInLauncher() {
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.e("LoginActivity", "Google sign in failed", e)
                    showToast(getString(R.string.google_sign_in_failed))
                }
            } else {
                Log.e("LoginActivity", "Google sign in cancelled or failed")
                showToast(getString(R.string.google_sign_in_failed))
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast(getString(R.string.email_password_required))
                return@setOnClickListener
            }

            loginWithEmailPassword(email, password)
        }

        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        binding.tvForgotPassword.setOnClickListener {
            // TODO: Implement forgot password functionality
            showToast("Forgot Password functionality to be implemented.")
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun loginWithEmailPassword(email: String, password: String) {
        showProgressBar()
        lifecycleScope.launch {
            val result = authRepository.signInWithEmailPassword(email, password)
            hideProgressBar()

            result.fold(
                onSuccess = { userId ->
                    saveUserSession(userId, email)
                    navigateToMainActivity()
                },
                onFailure = { exception ->
                    Log.e("LoginActivity", "Email/Password login failed", exception)
                    showToast(getString(R.string.login_failed, exception.localizedMessage))
                }
            )
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        showProgressBar()
        lifecycleScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            hideProgressBar()

            result.fold(
                onSuccess = { userId ->
                    saveUserSession(userId, authRepository.getCurrentUserEmail())
                    navigateToMainActivity()
                },
                onFailure = { exception ->
                    Log.e("LoginActivity", "Firebase Google Auth failed", exception)
                    showToast(getString(R.string.login_failed, exception.localizedMessage))
                }
            )
        }
    }

    private fun saveUserSession(userId: String?, email: String?) {
        userId?.let { sharedPrefManager.saveUserId(it) }
        email?.let { sharedPrefManager.saveUserEmail(it) }
        sharedPrefManager.setLoggedIn(true)
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
        binding.btnGoogleSignIn.isEnabled = false
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        binding.btnLogin.isEnabled = true
        binding.btnGoogleSignIn.isEnabled = true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}