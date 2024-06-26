package com.d479.xpenses.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d479.xpenses.models.Invoice
import com.d479.xpenses.models.User
import com.d479.xpenses.models.Category
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

class HomeScreenViewModel : ViewModel() {

    // number of invoices considered latest
    companion object {
        private const val RECENT_LIMIT = 5
    }

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

    suspend fun resetState(delete: Boolean = false) {
        _user.value = null
        userRepository.resetCurrentUser()
        if (delete) {
            userRepository.deleteUser(user.value!!)
        }
    }

    fun getName(): String {
        return user.value?.name?.split(" ")?.get(0).toString()
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

    fun getGroupedInvoices(): SortedMap<RealmInstant, List<Invoice>> {
        viewModelScope.launch {
            val fetchedInvoices = userRepository.getUserInvoices()
            _invoices.emitAll(fetchedInvoices)
        }
        return invoices.value.groupBy { it.date }.toSortedMap(reverseOrder())
    }

    fun getRecentGroupedInvoices(): SortedMap<RealmInstant, List<Invoice>> {
        viewModelScope.launch {
            val fetchedInvoices = userRepository.getUserInvoices()
            _invoices.emitAll(fetchedInvoices)
        }

        val latestInvoices = invoices.value.takeLast(Companion.RECENT_LIMIT)
        return latestInvoices.groupBy { it.date }.toSortedMap(reverseOrder())
    }

    fun getAnalyticsData(): List<Double> {
        viewModelScope.launch {
            val fetchedInvoices = userRepository.getUserInvoices()
            _invoices.emitAll(fetchedInvoices)
        }
        // group invoices by day
        val invoicesPerDay = invoices.value.groupBy { it.date }
        val totalPerDay = invoicesPerDay.mapValues { (_, invoices) -> invoices.sumOf { it.total } }
        val totalPerDaySorted = totalPerDay.toSortedMap()

        return totalPerDaySorted.values.toList()
    }

    fun getCategoryById(id: ObjectId): Category {
        return userRepository.getCategoryById(id)
    }
}