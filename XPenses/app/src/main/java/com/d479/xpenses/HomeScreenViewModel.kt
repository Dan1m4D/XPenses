package com.d479.xpenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d479.xpenses.models.User
import com.d479.xpenses.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val _user = MutableStateFlow<User?>(null)
    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val user: StateFlow<User?> = _user
    val allUsers: StateFlow<List<User>> = _allUsers

    init {
        viewModelScope.launch {
            val fetchedUser = userRepository.getUser()
            _user.emit(fetchedUser)

            val allUsers = userRepository.getAllUsers()
            _allUsers.emitAll(allUsers)
            allUsers.collect {
                println("All users -> $it")
            }
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