package com.d479.xpenses.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d479.xpenses.XpensesApp
import com.d479.xpenses.models.Invoice
import com.d479.xpenses.models.Item
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class ScanViewModel : ViewModel() {
    private val realm = XpensesApp.realm

    fun createInvoice(itemList: List<Item>, totalValue: Double) {
        viewModelScope.launch {
            realm.write {
                val invoice = Invoice().apply {
                    date = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())
                    local = "Local Name"
                    total = totalValue
                    items.addAll(itemList)
                    user = null
                }
                copyToRealm(invoice)
                Log.d("ScanViewModel", "Invoice created successfully")
                Log.d("ScanViewModel", "Invoice date: ${invoice.date}, total: ${invoice.total}")
            }
        }
    }
}