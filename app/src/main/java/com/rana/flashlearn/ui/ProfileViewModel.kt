package com.rana.flashlearn.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Context
import com.rana.flashlearn.AuthRepository

class ProfileViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() {



    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    fun loadUserProfile(context: Context) {
        // Get user data from SharedPreferences
        val sharedPrefs = context.getSharedPreferences("FlashLearnPrefs", Context.MODE_PRIVATE)
        _username.value = sharedPrefs.getString("username", "User") ?: "User"
        _email.value = sharedPrefs.getString("email", "email@example.com") ?: "email@example.com"


    }
}
