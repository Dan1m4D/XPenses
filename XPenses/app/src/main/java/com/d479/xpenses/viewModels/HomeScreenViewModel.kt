package com.d479.xpenses.viewModels

import android.util.Log
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
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeScreenViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    
    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices

    init {
        viewModelScope.launch {
            val fetchedUser = userRepository.getUser()
            _user.emit(fetchedUser)

            val fetchedInvoices = userRepository.getUserInvoices()
            _invoices.emitAll(fetchedInvoices)
        }
    }

    suspend fun resetState() {
        _user.value = null
        userRepository.resetCurrentUser()
    }

    fun getName(): String {
        return user.value?.name?.split(" ")?.get(0).toString()
    }


    fun formatDate(input: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
        val date = inputFormat.parse(input)

        date?.let {
            val today = Calendar.getInstance()
            val inputDate = Calendar.getInstance().apply { time = it }

            return when {
                today[Calendar.YEAR] == inputDate[Calendar.YEAR] -> {
                    val outputFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())
                    when {
                        today[Calendar.DAY_OF_YEAR] == inputDate[Calendar.DAY_OF_YEAR] -> "Today"
                        today[Calendar.DAY_OF_YEAR] - inputDate[Calendar.DAY_OF_YEAR] == 1 -> "Yesterday"
                        else -> outputFormat.format(it)
                    }
                }
                else -> {
                    val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                    outputFormat.format(it)
                }
            }
        }
        return "Invalid date"
    }

    fun getAnalyticsData(): List<Double> {
        val invoicesPerDay = invoices.value.groupBy { it.date.substring(0, 8) }
        val totalPerDay = invoicesPerDay.mapValues { (_, invoices) -> invoices.sumOf { it.total } }
        val totalPerDaySorted = totalPerDay.toSortedMap()

        return totalPerDaySorted.values.toList()
    }
}