package com.d479.xpenses.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d479.xpenses.XpensesApp
import com.d479.xpenses.models.Category
import kotlinx.coroutines.launch
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import io.realm.kotlin.*


class CatViewModel : ViewModel() {
    private val realm = XpensesApp.realm

    init {
        //createSampleEntries()
    }
    private fun createSampleEntries() {
        viewModelScope.launch {
            realm.write {
                val existingCategories = query<Category>().find()
                if (existingCategories.isEmpty()) {
                    val cat1 = Category().apply {
                        name = "Saude"
                        color = "#FF5733"
                    }
                    val cat2 = Category().apply {
                        name = "Geral"
                        color = "#DAF7A6"
                    }
                    val cat3 = Category().apply {
                        name = "Transporte"
                        color = "#581845"
                    }
                    copyToRealm(
                        cat1,
                        updatePolicy = UpdatePolicy.ALL
                    ) // Adiciona o objeto Category ao Realm
                    copyToRealm(cat2, updatePolicy = UpdatePolicy.ALL)
                    Log.d("CatViewModel", "Category added: ${cat1._id.toString()}")
                }
            }
        }
    }
}