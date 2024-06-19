package com.d479.xpenses.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d479.xpenses.models.Category
import com.d479.xpenses.models.Invoice
import com.d479.xpenses.models.User
import com.d479.xpenses.repositories.CategoryRepository
import com.d479.xpenses.repositories.UserRepository
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

class AnalyticsViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val categoryRepository = CategoryRepository()
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

    private val _categoryList = MutableStateFlow(emptyList<Category>())
    val categoryList: StateFlow<List<Category>> = _categoryList

    init {
        viewModelScope.launch {
            val fetchedUser = userRepository.getUser()
            _user.emit(fetchedUser)


            val fetchedInvoices = userRepository.getUserInvoices()
            _invoices.emitAll(fetchedInvoices)

            val fetchedFilteredInvoices = userRepository.getFilteredInvoices(_selectedFilter)
            _filteredInvoices.emitAll(fetchedFilteredInvoices)

            val fetchedCategories = categoryRepository.getAllCategories()
            _categoryList.emitAll(fetchedCategories)
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

    private fun dateToEpochTime(date: RealmInstant): Long {
        val input = date.epochSeconds * 1000
        val formatedDate = Date(input)

        return formatedDate.time
    }

    fun getInvoicesByCategory(filteredInvoices: List<Invoice>): Map<Category, Double> {
        // group invoices by category
        val invoicesPerCategory = filteredInvoices.groupBy { it.categoryId }

        val totalPerCategory =
            invoicesPerCategory.mapValues { (_, invoices) -> invoices.sumOf { it.total } }
        val totalPerCategorySorted = totalPerCategory.toSortedMap().map {
            val category = userRepository.getCategoryById(it.key)
            Pair(category, it.value)
        }.toMap()
        viewModelScope.launch {
            val fetchedFilteredInvoices = userRepository.getFilteredInvoices(selectedFilter)
            _filteredInvoices.emitAll(fetchedFilteredInvoices)
        }
        return totalPerCategorySorted
    }

    fun getAnalyticsData(): List<Double> {
        // group invoices by day
        val invoicesPerDay = filteredInvoices.value.groupBy { invoice ->
            val date = dateToEpochTime(invoice.date)
            Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate().dayOfYear

        }
        val totalPerDay = invoicesPerDay.mapValues { (_, invoices) -> invoices.sumOf { it.total } }
        val totalPerDaySorted = totalPerDay.toSortedMap()

        return totalPerDaySorted.values.toList()
    }


}


