package com.d479.xpenses.signIn

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d479.xpenses.XpensesApp.Companion.realm
import com.d479.xpenses.models.Category
import com.d479.xpenses.models.Invoice
import com.d479.xpenses.models.Item
import com.d479.xpenses.models.User
import com.d479.xpenses.repositories.UserRepository
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    private val userRepository = UserRepository()
    val state = _state.asStateFlow()

    // Update the state with the result of the sign in operation
    suspend fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage
            )
        }
    }

    // Reset the state to the initial state
    fun resetState() {
        _state.update { SignInState() }
    }

    // Register the user in the repository
    suspend fun loginUser(userData: UserData) {

//        // Because the compa
//        if(userData.userId?.isBlank() == true) {
//            Log.e("SignInViewModel", "User id cannot be blank")
//            return
//        }
        userRepository.registerUser(
            User().apply {
                uid = userData.userId ?: ""
                name = userData.username ?: ""
                photoUrl = userData.profilePictureURL ?: ""
            }
        )
        createSampleEntries()
    }

    private fun createSampleEntries() {
        viewModelScope.launch {
            realm.write {

                val userIn = this
                    .query<User>()
                    .sort("timestamp", Sort.DESCENDING)
                    .first()
                    .find()

                Log.d("SampleCreation", "User: ${userIn?.name}")

                if (userIn?.uid?.isNotBlank()!!) {
                    Log.d("SampleCreation", "User already exists -> ${userIn.name}")


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

                    val invoice1 = Invoice().apply {
                        date = RealmInstant.from(1718650408L, 0)
                        local = "Local Name"
                        total = 100.0
                        user = userIn
                    }
                    val invoice2 = Invoice().apply {
                        date = RealmInstant.now()
                        local = "Local Name"
                        total = 23.0
                        user = userIn
                    }

                    if (userIn.name == "Daniel Madureira") {
                        Log.d("SampleCreation", "User is Daniel Madureira -> Adding more invoices...")

                        val invoice3 = Invoice().apply {
                            date = RealmInstant.from(1715650408L, 0)
                            local = "Local Name"
                            total = 64.0
                            user = userIn
                        }

                        invoice3.category = cat3
                        invoice3.items.addAll(itemList)

                        copyToRealm(invoice3, updatePolicy = UpdatePolicy.ALL)


                    }

                    userIn.invoices.add(invoice1)
                    userIn.invoices.add(invoice2)


                    invoice1.category = cat1
                    invoice2.category = cat2

                    invoice1.items.addAll(itemList)
                    invoice2.items.addAll(itemList)


                    Log.d("SampleCreation", "Copying to Realm... -> ${invoice1.user?.name}")

                    copyToRealm(invoice1, updatePolicy = UpdatePolicy.ALL)
                    copyToRealm(invoice2, updatePolicy = UpdatePolicy.ALL)
                    copyToRealm(
                        cat1,
                        updatePolicy = UpdatePolicy.ALL
                    ) // Adiciona o objeto Category ao Realm
                    copyToRealm(cat2, updatePolicy = UpdatePolicy.ALL)
                } else {
                    Log.d("CatViewModel", "User does not exist")
                }
            }
        }
    }
}