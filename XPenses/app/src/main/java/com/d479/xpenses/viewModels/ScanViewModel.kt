package com.d479.xpenses.viewModels

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d479.xpenses.XpensesApp
import com.d479.xpenses.models.Category
import com.d479.xpenses.models.Invoice
import com.d479.xpenses.models.Item
import com.d479.xpenses.repositories.CategoryRepository
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId
import java.text.SimpleDateFormat
import java.util.Date
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.d479.xpenses.managers.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

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
            val fetchedCats = catRepo.getAllCategories()
            _categories.emitAll(fetchedCats)
        }
    }

    fun getSelectedCategory(): Category? {
//        viewModelScope.launch {
//            val fetchedCats = catRepo.getAllCategories()
//            _selectedCategory.value = fetchedCats.first().get(0)
//        }
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
//                local = location ?: Location("DEFAULT_LOCATION").apply {
//                    latitude = 40.638076
//                    longitude = -8.653603
//                }
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