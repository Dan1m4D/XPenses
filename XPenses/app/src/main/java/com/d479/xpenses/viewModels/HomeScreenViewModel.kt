package com.d479.xpenses.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d479.xpenses.models.User
import com.d479.xpenses.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        viewModelScope.launch {
            val fetchedUser = userRepository.getUser()
            _user.emit(fetchedUser)
        }
    }

    suspend fun resetState() {
        _user.value = null
        userRepository.resetCurrentUser()
    }

    fun getName(): String {
        return user.value?.name?.split(" ")?.get(0).toString()
    }
}