package com.d479.xpenses.repositories

import androidx.compose.runtime.saveable.rememberSaveable
import com.d479.xpenses.XpensesApp
import com.d479.xpenses.models.Category
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

class CategoryRepository {
    private val realm = XpensesApp.realm

    suspend fun getAllCategories(): Flow<List<Category>> {
        return realm
            .query<Category>()
            .asFlow()
            .map { results ->
                results.list.toList()
            }
    }



}