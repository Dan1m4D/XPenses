package com.d479.xpenses.repositories

import android.util.Log
import com.d479.xpenses.XpensesApp
import com.d479.xpenses.models.Invoice
import com.d479.xpenses.models.User
import com.d479.xpenses.models.Category
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import java.util.Date

class UserRepository {
    private val realm = XpensesApp.realm

    companion object {
        var currentUser = User()
    }

    suspend fun registerUser(user: User) {
        realm.write {

            // Check if a user with the same uid already exists
            val existingUser = realm
                .query<User>("uid == $0", user.uid)
                .first()
                .find()

            if (user.uid.isBlank()) {
                return@write
            }

            if (existingUser != null) {
                // If a user with the same uid exists, set currentUser to that user
                currentUser = existingUser
                Log.d(
                    "UserRepository",
                    "User ${currentUser.name} already exists -> ${
                        Date(
                            currentUser.timestamp.epochSeconds * 1000
                        )
                    }"
                )
                copyToRealm(user.apply {
                    timestamp = RealmInstant.now()
                }, updatePolicy = UpdatePolicy.ALL)
            } else {
                // If no user with the same uid exists, insert the new user
                val userIn = User().apply {
                    uid = user.uid
                    name = user.name
                    photoUrl = user.photoUrl
                    invoices = realmListOf()
                    timestamp = RealmInstant.now()
                }
                copyToRealm(userIn, updatePolicy = UpdatePolicy.ALL)

                // Set currentUser to the new user
                currentUser = userIn
            }
        }
    }

    suspend fun getUser(): User? {
        return realm
            .query<User>("uid == $0", currentUser.uid)
            .first()
            .find()
    }

    suspend fun updateUser(user: User) {
        realm.write {
            copyToRealm(user, updatePolicy = UpdatePolicy.ALL)
        }
    }

    suspend fun deleteUser(user: User) {
        realm.write {
            val latest = findLatest(user)
            if (latest != null) {
                delete(latest)
            }
        }
    }

    suspend fun getAllUsers(): Flow<List<User>> {
        return realm
            .query<User>()
            .asFlow()
            .map { results ->
                results.list.toList()
            }
    }

    fun resetCurrentUser() {
        currentUser = User()
    }

    suspend fun getUserInvoices(): Flow<List<Invoice>> {
        Log.d("UserRepository", "Fetching invoices in getUserInvoices()...")

        val managedUser = getUser()

        return realm
            .query<Invoice>("user == $0", managedUser)
            .asFlow()
            .map { results ->
                Log.d("UserRepository", "Fetched ${results.list.toList().size} invoices")
                results.list.toList()
            }

    }

    suspend fun getFilteredInvoices(selectedFilter: StateFlow<String>): Flow<List<Invoice>> {
        val invoices = getUserInvoices()

        val time: Long = when (selectedFilter.value) {
            "Last week" -> {
                604800
            }

            "Last month" -> {
                2629746
            }

            "Last year" -> {
                31556952
            }

            else -> {
                0L
            }
        }
        val comparisonTime = RealmInstant.from(RealmInstant.now().epochSeconds - time, 1000)


        return realm
            .query<Invoice>("date > $0", comparisonTime)
            .distinct(
                "categoryId",
                "date"
            )
            .asFlow()
            .map { results ->
                Log.d("UserRepository", "Fetched ${results.list.toList().size} invoices")
                results.list.toList()
            }

    }

    fun getCategoryById(id: ObjectId): Category {
        return realm
            .query<Category>("_id == $0", id)
            .first()
            .find() ?: Category()
    }



}
