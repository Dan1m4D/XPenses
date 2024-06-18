package com.d479.xpenses.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d479.xpenses.models.User
import com.d479.xpenses.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnalyticsViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _filterOptions = MutableStateFlow(listOf("Last week", "Last month", "Last year"))
    val filterOptions: StateFlow<List<String>> = _filterOptions

    private val _selectedFilter = MutableStateFlow("Last week")
    val selectedFilter: StateFlow<String> = _selectedFilter

    private val _isDialogOpen = MutableStateFlow(false)
    val isDialogOpen: StateFlow<Boolean> = _isDialogOpen


    init {
        viewModelScope.launch {
            val fetchedUser = userRepository.getUser()
            _user.emit(fetchedUser)
        }
    }

    fun onOptionSelected(option: String) {
        _selectedFilter.value = option
    }

    fun onDialogOpen() {
        _isDialogOpen.value = true
    }

    fun onDialogClose() {
        _isDialogOpen.value = false
    }
}