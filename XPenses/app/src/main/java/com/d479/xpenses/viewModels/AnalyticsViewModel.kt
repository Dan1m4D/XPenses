package com.d479.xpenses.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d479.xpenses.models.Category
import com.d479.xpenses.models.Invoice
import com.d479.xpenses.models.User
import com.d479.xpenses.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.text.SimpleDateFormat
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

    private val _filteredInvoices = MutableStateFlow(emptyList<Invoice>())
    val filteredInvoices: StateFlow<List<Invoice>> = _filteredInvoices

    init {
        viewModelScope.launch {
            val fetchedUser = userRepository.getUser()
            _user.emit(fetchedUser)


            val fetchedInvoices = userRepository.getUserInvoices()
            _invoices.emitAll(fetchedInvoices)

            val fetchedFilteredInvoices = userRepository.getFilteredInvoices(_selectedFilter)
            _filteredInvoices.emitAll(fetchedFilteredInvoices)



        }
    }

    fun invoicesToDoubleList(invoices: List<Invoice>): List<Double> {
        val groupedInvoices = invoices.groupBy { it.date }
        return groupedInvoices.map { (date, invoices) ->
            invoices.sumOf { it.total }
        }
    }


    fun onOptionSelected(option: String) {
        _selectedFilter.value = option
        viewModelScope.launch {
            val fetchedFilteredInvoices = userRepository.getFilteredInvoices(selectedFilter)
            _filteredInvoices.emitAll(fetchedFilteredInvoices)
        }
    }

    fun onDialogOpen() {
        _isDialogOpen.value = true
    }

    fun onDialogClose() {
        _isDialogOpen.value = false
    }

    private fun dateToEpochTime(date: String): Long? {
        val inputFormat = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
        val formatedDate = inputFormat.parse(date)
        return formatedDate?.time
    }

    fun invoicesByCategory(filteredInvoices: List<Invoice>): Map<Category, Double> {
        val groupedInvoices = filteredInvoices.groupBy { invoice ->
            getCategoryById(invoice.categoryId)
        }.mapValues { (_, invoices) -> invoices.sumOf { it.total } }
        return groupedInvoices
    }
    fun getCategoryById(id: ObjectId): Category {
        return userRepository.getCategoryById(id)
    }


}


