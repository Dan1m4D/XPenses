package com.d479.xpenses.dao

import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.d479.xpenses.entities.Invoice

interface InvoiceDao {

    @Upsert
    suspend fun upsertInvoice(invoice: Invoice)

    @Delete
    suspend fun deleteInvoice(invoice: Invoice)

    @Query("SELECT * FROM Invoice")
    suspend fun getAllInvoices(): List<Invoice>

    @Query("SELECT * FROM Invoice WHERE id = :id")
    suspend fun getInvoiceById(id: String): Invoice?


}