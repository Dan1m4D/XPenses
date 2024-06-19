package com.d479.xpenses.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d479.xpenses.models.Invoice
import com.d479.xpenses.models.User
import com.d479.xpenses.models.Category
import com.d479.xpenses.repositories.CategoryRepository
import com.d479.xpenses.repositories.UserRepository
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.SortedMap

class ExpensesViewModel : ViewModel() {

    // number of invoices considered latest
    companion object {
        private const val RECENT_LIMIT = 5
    }

    private val categoryRepository = CategoryRepository()
    private val userRepository = UserRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices

    private val _filteredInvoices = MutableStateFlow(emptyList<Invoice>())
    val filteredInvoices: StateFlow<List<Invoice>> = _filteredInvoices

    private val _categoryList = MutableStateFlow(emptyList<Category>())
    val categoryList: StateFlow<List<Category>> = _categoryList

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

    fun formatDate(input: RealmInstant): String {
        val date = Date(input.epochSeconds * 1000)

        date.let {
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
    }

    fun getGroupedInvoices(invoices: List<Invoice>): SortedMap<RealmInstant, List<Invoice>> {

        Log.d("ExpensesViewModel", "getGroupedInvoices called")
        return invoices.groupBy { it.date }.toSortedMap(reverseOrder())
    }


    fun getCategoryById(id: ObjectId): Category {
        return userRepository.getCategoryById(id)
    }
}