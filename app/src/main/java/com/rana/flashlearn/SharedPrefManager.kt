package com.rana.flashlearn

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SharedPrefManager private constructor(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val PREF_NAME = "FlashLearnPrefs"
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_EMAIL = "userEmail"
        private const val KEY_USER_ID = "userId"
        private const val KEY_IS_FIRST_LAUNCH = "isFirstLaunch"

        @Volatile
        private var INSTANCE: SharedPrefManager? = null

        fun getInstance(context: Context): SharedPrefManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPrefManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean = preferences.getBoolean(KEY_IS_LOGGED_IN, false)

    fun saveUserEmail(email: String) {
        preferences.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? = preferences.getString(KEY_USER_EMAIL, null)

    fun saveUserId(userId: String) {
        preferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? = preferences.getString(KEY_USER_ID, null)

    fun clearSession() {
        preferences.edit().clear().apply()
    }

    fun isFirstLaunch(): Boolean = preferences.getBoolean(KEY_IS_FIRST_LAUNCH, true)

    fun setFirstLaunch(isFirst: Boolean) {
        preferences.edit().putBoolean(KEY_IS_FIRST_LAUNCH, isFirst).apply()
    }
}
