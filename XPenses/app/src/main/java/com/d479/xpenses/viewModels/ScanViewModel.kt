package com.d479.xpenses.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d479.xpenses.XpensesApp
import com.d479.xpenses.managers.LocationManager
import com.d479.xpenses.models.Category
import com.d479.xpenses.models.Invoice
import com.d479.xpenses.models.Item
import com.d479.xpenses.repositories.CategoryRepository
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class ScanViewModel() : ViewModel() {
    private val realm = XpensesApp.realm
    private val catRepo = CategoryRepository()


    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory

    val isDialogOpen = MutableStateFlow(false)
    init {
        viewModelScope.launch {
            val fetchedCats = catRepo.getAllCategories().first()//apagar o first
            _categories.value = fetchedCats
            Log.d("CATEGORIES",categories.value.size.toString())
            if (fetchedCats.isNotEmpty()) {
                _selectedCategory.value = fetchedCats[0]
            }
        }

    }

    fun getCategories() {
        viewModelScope.launch {
            val categories = realm.query<Category>().find()
            _categories.value = realm.copyFromRealm(categories) // Use copyFromRealm to avoid accessing RealmObject outside of the Realm instance
        }
    }

    fun getSelectedCategory(): Category? {
        return _selectedCategory.value
    }


    fun onDialogDismiss() {
        isDialogOpen.value = false
        Log.d("CLOSE", isDialogOpen.toString())
    }

    fun onDialogShow() {
        isDialogOpen.value = true
        Log.d("OPEN", isDialogOpen.toString())
    }




    fun createInvoice(itemList: List<Item>, totalValue: Double, titleInvoice: String) {

        viewModelScope.launch {
            val location = LocationManager.getLastLocation()
            Log.d("ScanViewModel", "Location: ${location?.latitude}, ${location?.longitude}")
            val invoice = Invoice().apply {
                title = titleInvoice
                date = RealmInstant.now()
                latitude = location?.latitude ?: 40.638076
                longitude = location?.longitude ?: -8.653603
                total = totalValue
                items.addAll(itemList)
                user = null
                categoryId = selectedCategory.value?._id ?: ObjectId()
            }

            realm.write {
                copyToRealm(invoice)
                Log.d("ScanViewModel", "Invoice created successfully")
                Log.d("ScanViewModel", "Invoice date: ${invoice.title}")

            }
        }
    }

    fun onSelectedCategoryChanged(category: Category) {
        _selectedCategory.value = category
    }


}