package com.d479.xpenses.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d479.xpenses.models.Invoice
import com.d479.xpenses.models.User
import com.d479.xpenses.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    private val _invoices = MutableStateFlow(emptyList<Invoice>())
    val invoices: StateFlow<List<Invoice>> = _invoices

    init {
        viewModelScope.launch {
            val fetchedUser = userRepository.getUser()
            _user.emit(fetchedUser)


            val fetchedInvoices = userRepository.getUserInvoices()
            _invoices.emitAll(fetchedInvoices)
        }
    }

    /*fun getFilteredInvoices(): List<Invoice> {
        return invoices.value.filter { invoice ->
            when (selectedFilter.value) {
                "Last week" -> {
                    dateToEpochTime(invoice.date)!! > System.currentTimeMillis() - 604800000
                }
                "Last month" -> {
                    dateToEpochTime(invoice.date)!! > System.currentTimeMillis() - 2629746000
                }
                "Last year" -> {
                    dateToEpochTime(invoice.date)!! > System.currentTimeMillis() - 31556952000
                }
                else -> {
                    true
                }
            }
        }
    }*/



    fun onOptionSelected(option: String) {
        _selectedFilter.value = option
    }

    fun onDialogOpen() {
        _isDialogOpen.value = true
    }

    fun onDialogClose() {
        _isDialogOpen.value = false
    }

    private fun dateToEpochTime (date:String): Long? {
        val inputFormat = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
        val formatedDate = inputFormat.parse(date)
        return formatedDate?.time
    }


}


