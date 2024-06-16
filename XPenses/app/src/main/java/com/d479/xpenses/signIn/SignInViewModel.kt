package com.d479.xpenses.signIn

import androidx.lifecycle.ViewModel
import com.d479.xpenses.models.User
import com.d479.xpenses.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel: ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    private val userRepository = UserRepository()
    val state = _state.asStateFlow()

    // Update the state with the result of the sign in operation
    suspend fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }

    // Reset the state to the initial state
    fun resetState() {
        _state.update { SignInState() }
    }

    // Register the user in the repository
    suspend fun loginUser(userData: UserData) {
        userRepository.registerUser(
            User().apply {
                uid = userData.userId ?: ""
                name = userData.username ?: ""
                photoUrl = userData.profilePictureURL ?: ""
            }
        )
    }
}