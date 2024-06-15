package com.d479.xpenses.repositories

import androidx.compose.runtime.saveable.rememberSaveable
import com.d479.xpenses.XpensesApp
import com.d479.xpenses.models.User
import com.d479.xpenses.signIn.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.find
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

            if (existingUser != null) {
                // If a user with the same uid exists, set currentUser to that user
                currentUser = existingUser
            } else {
                // If no user with the same uid exists, insert the new user
                val userIn = User().apply {
                    uid = user.uid
                    name = user.name
                    photoUrl = user.photoUrl
                    invoices = realmListOf()
                }
                copyToRealm(userIn, updatePolicy = UpdatePolicy.ALL)

                // Set currentUser to the new user
                currentUser = userIn
            }
        }
        println("Current user -> " + currentUser.name + " " + currentUser.uid)
    }

    suspend fun getUser(): User? {
        println("User -> " + currentUser.name + " " + currentUser.photoUrl)
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


}