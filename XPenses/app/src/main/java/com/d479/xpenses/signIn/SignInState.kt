package com.d479.xpenses.signIn

data class SignInState (
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)