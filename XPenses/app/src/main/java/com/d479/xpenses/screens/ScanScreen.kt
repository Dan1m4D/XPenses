package com.d479.xpenses.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.d479.xpenses.BuildConfig
import com.d479.xpenses.XpensesApp
import com.d479.xpenses.analyzer.TextRecognitionAnalyzer
import com.d479.xpenses.models.Category
import com.d479.xpenses.models.Invoice
import com.d479.xpenses.models.Item
import com.d479.xpenses.viewModels.ScanViewModel
import com.google.mlkit.vision.common.InputImage
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import io.realm.kotlin.query.find


@Composable
fun ScanScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current
    val file = createImageFile(context)
    val uri = FileProvider.getUriForFile(
        context,
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var recognizedText by remember { mutableStateOf("") }
    val selectedWords = remember { mutableStateListOf<String>() }
    val selectedWordIndices  = remember { mutableStateListOf<Int>() }

    var showSelectedWordsDialog by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            capturedImageUri = uri
            recognizeTextFromImage(context, uri) { text ->
                recognizedText = text
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
    //var showSelectedWordsDialog by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            if (capturedImageUri != null) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = rememberImagePainter(capturedImageUri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                ) {
                    val annotatedText = buildAnnotatedString {
                        recognizedText.split(" ").forEachIndexed { index, word ->
                            if (index > 0) append(" ")
                            pushStringAnnotation(tag = "WORD", annotation = "$index:$word")
                            val spanStyle = if (selectedWordIndices.contains(index)) {
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    background = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                )
                            } else {
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    background = Color.Transparent
                                )
                            }
                            withStyle(style = spanStyle) {
                                append(word)
                            }
                            pop()
                        }
                    }

                    ClickableText(
                        text = annotatedText,
                        modifier = Modifier.fillMaxWidth(),
                        style = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        onClick = { offset ->
                            annotatedText.getStringAnnotations(tag = "WORD", start = offset, end = offset)
                                .firstOrNull()?.let { annotation ->
                                    val (index, word) = annotation.item.split(":")
                                    val wordIndex = index.toInt()
                                    if (selectedWordIndices.contains(wordIndex)) {
                                        selectedWordIndices.remove(wordIndex)
                                    } else {
                                        selectedWordIndices.add(wordIndex)
                                        selectedWords.add(word)
                                    }
                                }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                cameraLauncher.launch(uri)
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }) {
            Text(text = "Capture Image From Camera")
        }

        Button(onClick = { showSelectedWordsDialog = true }) {
            Text(text = "Show Selected Words")
        }
        if (showSelectedWordsDialog) {
            val scanViewModel = ScanViewModel()
            ShowSelectedWordsDialog(scanViewModel,selectedWordIndices, recognizedText) {
                showSelectedWordsDialog = false
            }
        }
    }
}


fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        context.externalCacheDir
    )
}

fun recognizeTextFromImage(context: Context, imageUri: Uri, onTextRecognized: (String) -> Unit) {
    val recognizer = TextRecognitionAnalyzer(
        onTextDetected = { text ->
            onTextRecognized(text)
        },
        onTextDetailsDetected = { details ->
            // Process detailed text data if needed
        }
    )

    val image: InputImage

    try {
        image = InputImage.fromFilePath(context, imageUri)
        recognizer.processImage(image)
    } catch (e: Exception) {
        e.printStackTrace()
        onTextRecognized("Failed to process image: ${e.message}")
    }
}



@Composable
fun ShowSelectedWordsDialog(viewModel: ScanViewModel, selectedWordIndices: List<Int>, recognizedText: String, onClose: () -> Unit) {
    val words = recognizedText.split(" ") // Divide o texto reconhecido em palavras
    val selectedWords = words.filterIndexed { index, _ -> selectedWordIndices.contains(index) }
        .map { it.replace("\n", "") } // Remove quebras de linha das palavras selecionadas

    val organizedWords = mutableListOf<String>() // Lista para armazenar as linhas organizadas
    var tempItem = "" // Variável temporária para armazenar parcialmente uma linha
    val itemList = mutableListOf<Item>()


    for (word in selectedWords) {
        if (word.matches(Regex(".*\\d.*"))) { // Se a palavra contém dígitos (presumivelmente, um preço)
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
        val price = parts.last().replace("€", "").toDoubleOrNull() ?: return@forEach

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

    val realm = XpensesApp.realm
    val categories = realm.query<Category>().find()


    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Selected Words") },
        text = {
            Column {
                Text("Number of Items: ${organizedWords.size}") // Exibe o número de itens
                for (i in organizedWords.indices) {
                    val item = organizedWords[i]
                    Text("Item $i: $item") // Imprime cada objeto com o índice
                }
                Text("Total: $totalValue")

                Text("Categories:")

                for (category in categories) {
                    Text(category.name)
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
                // Aqui você chama o método createInvoice do viewModel
                viewModel.createInvoice(itemList, totalValue)
                onClose()
            }) {
                Text("Create Invoice")
            }
        }

    )
}


