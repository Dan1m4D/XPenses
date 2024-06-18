package com.d479.xpenses.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d479.xpenses.XpensesApp
import com.d479.xpenses.models.Category
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.launch

class ScanScreenViewModel : ViewModel() {

    private val realm = XpensesApp.realm

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val categories = realm.query<Category>().find()
            _categories.value = realm.copyFromRealm(categories) // Use copyFromRealm to avoid accessing RealmObject outside of the Realm instance
        }
    }



}