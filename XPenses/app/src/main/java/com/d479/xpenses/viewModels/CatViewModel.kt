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
import com.d479.xpenses.models.Invoice
import com.d479.xpenses.models.Item
import com.d479.xpenses.models.User
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import io.realm.kotlin.*
import java.text.SimpleDateFormat
import java.util.Date


class CatViewModel : ViewModel() {
    private val realm = XpensesApp.realm

    val userIn = realm.query<User>().first().find()

    val itemList = realmListOf(
        Item().apply {
            name = "Item 1"
            price = 10.0
        },
        Item().apply {
            name = "Item 2"
            price = 20.0
        },
        Item().apply {
            name = "Item 3"
            price = 30.0
        }
    )

    init {
        createSampleEntries()
    }
    private fun createSampleEntries() {
        viewModelScope.launch {
            realm.write {

                val userIn = this.query<User>().first().find()

                val itemList = realmListOf(
                    Item().apply {
                        name = "Item 1"
                        price = 10.0
                    },
                    Item().apply {
                        name = "Item 2"
                        price = 20.0
                    },
                    Item().apply {
                        name = "Item 3"
                        price = 30.0
                    }
                )

                val cat1 = Category().apply {
                    name = "Saude"
                    color = "#00FF00"
                }
                val cat2 = Category().apply {
                    name = "Geral"
                    color = "#FF0000"
                }

                val invoice1 = Invoice().apply {
                    date = SimpleDateFormat("yyyyMMdd_HHmm").format(Date(1718650408000))
                    local = "Local Name"
                    total = 100.0
                    user = userIn
                }
                val invoice2 = Invoice().apply {
                    date = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())
                    local = "Local Name"
                    total = 23.0
                    user = userIn
                }

                invoice1.categories = cat1
                invoice2.categories = cat2

                invoice1.items.addAll(itemList)
                invoice2.items.addAll(itemList)


                copyToRealm(invoice1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(invoice2, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(cat1, updatePolicy = UpdatePolicy.ALL) // Adiciona o objeto Category ao Realm
                copyToRealm(cat2, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }
}
