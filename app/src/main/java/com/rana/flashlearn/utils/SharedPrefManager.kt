package com.rana.flashlearn

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SharedPrefManager private constructor(context: Context) {

    private val masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val PREF_NAME = "FlashLearnPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
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

    private inline fun editPreferences(commit: Boolean = false, action: SharedPreferences.Editor.() -> Unit) {
        preferences.edit().apply {
            action()
            if (commit) commit() else apply()
        }
    }

    // ✅ Fix: Improved session management (Persistent login)
    fun setLoggedIn(isLoggedIn: Boolean) {
        editPreferences { putBoolean(KEY_IS_LOGGED_IN, isLoggedIn) }
    }

    fun isLoggedIn(): Boolean = preferences.getBoolean(KEY_IS_LOGGED_IN, false)

    fun saveUserEmail(email: String) {
        editPreferences { putString(KEY_USER_EMAIL, email) }
    }

    fun getUserEmail(): String? = preferences.getString(KEY_USER_EMAIL, null)

    fun saveUserId(userId: String) {
        editPreferences { putString(KEY_USER_ID, userId) }
    }

    fun getUserId(): String? = preferences.getString(KEY_USER_ID, null)

    // ✅ Fix: Ensure session clearing is immediately committed
    fun clearSession() {
        editPreferences(commit = true) { clear() }
    }

    fun isFirstLaunch(): Boolean = preferences.getBoolean(KEY_IS_FIRST_LAUNCH, true)

    fun setFirstLaunch(isFirst: Boolean) {
        editPreferences { putBoolean(KEY_IS_FIRST_LAUNCH, isFirst) }
    }
}
