package com.d479.xpenses.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.d479.xpenses.XpensesApp
import com.d479.xpenses.models.Category
import com.d479.xpenses.models.Item
import com.d479.xpenses.XpensesApp.Companion.CHANNEL_ID
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.d479.xpenses.R

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowSelectedWordsDialog(
    selectedWordIndices: List<Int>,
    recognizedText: String,
    categories: List<Category>,
    selectedCategory: Category?,
    onClose: () -> Unit,
    onCategorySelected: (Category) -> Unit,
    createInvoice: (List<Item>, Double,String) -> Unit,
)
{
    Log.d("BEMVINDO", "ShowSelectedWordsDialog")
    val words = recognizedText.split(" ")
    val selectedWords = words.filterIndexed { index, _ -> selectedWordIndices.contains(index) }
        .map { it.replace("\n", "") }

    val organizedWords = mutableListOf<String>()
    var tempItem = ""
    val itemList = mutableListOf<Item>()

    var invoiceTitle by remember { mutableStateOf("") }


    for (word in selectedWords) {
        if (word.matches(Regex(".*\\d{2,}.*"))) { // Se a palavra contém dígitos (presumivelmente, um preço)
            if (tempItem.isNotEmpty()) {
                organizedWords.add("$tempItem $word") // Adiciona a linha completa à lista organizada
                tempItem = "" // Limpa a variável temporária
            } else {
                organizedWords.add(word) // Adiciona apenas o preço, pois não há item anterior
            }
        } else {
            tempItem = if (tempItem.isEmpty()) word else "$tempItem $word" // Adiciona a palavra ao item temporário
        }
    }

    organizedWords.forEach {line ->
        val parts = line.split(" ")
        Log.d("PARTS", parts.toString())
        var cleanedValue = parts.last().replace("[a-zA-Z€]".toRegex(), "").trim()
        if (cleanedValue.contains(',') && !cleanedValue.contains('.')) {
            cleanedValue = cleanedValue.replace(',', '.')}

        val price = cleanedValue.toDoubleOrNull() ?: return@forEach

        val name = parts.dropLast(1).joinToString(" ")
        val item = Item().apply {
            this.name = name
            this.price = price
            qty = 1
        }
        itemList.add(item)
    }

    var totalValue = 0.0
    itemList.forEach { item ->
        totalValue += item.price
    }
    val context = LocalContext.current
    AlertDialog(

        onDismissRequest = onClose,
        title = { Text("Selected Words") },

        text = {

            Column {
                TextField(
                    value = invoiceTitle,
                    onValueChange = { invoiceTitle = it },
                    label = { Text("Invoice Title") }
                )

                Text("Number of Items: ${itemList.size}") // Exibe o número de itens

                itemList.forEach { item ->
                    Text(" ${item.name} --> ${item.price}")
                }

                Text("Total: $totalValue")

                if (selectedCategory != null) {
                    ExampleDropdownMenu(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = onCategorySelected
                    )
                }
            }

        },

        confirmButton = {
            Button(onClick = onClose) {
                Text("Close")
            }
        },
        dismissButton = {
            Button(onClick = {
                createInvoice(itemList, totalValue, invoiceTitle)
                var builder = NotificationCompat.Builder( context,CHANNEL_ID)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("New Invoice")
                    .setContentText("The invoice $invoiceTitle has been added.")
                    .setPriority(NotificationCompat.PRIORITY_LOW)

                with(NotificationManagerCompat.from(context)) {
                    val notificationId = System.currentTimeMillis().toInt()
                    notify(notificationId, builder.build())
                }
                onClose()
            }) {
                Text("Create Invoice")
            }

        }

    )
}

