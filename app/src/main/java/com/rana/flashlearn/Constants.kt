package com.rana.flashlearn

/**
 * Contains all constants used throughout the application
 */
object Constants {

    const val PREF_NAME = "FlashLearnPrefs"
    const val PREF_IS_FIRST_LAUNCH = "isFirstLaunch"
    const val PREF_USER_LOGGED_IN = "userLoggedIn"

    // Firebase Nodes
    const val FIREBASE_USERS = "users"
    const val FIREBASE_FLASHCARDS = "flashcards"
    const val FIREBASE_COLLECTIONS = "collections"

    // Request Codes
    const val RC_SIGN_IN = 9001

    // Intent Extra Keys
    const val EXTRA_USER_ID = "extra_user_id"
}