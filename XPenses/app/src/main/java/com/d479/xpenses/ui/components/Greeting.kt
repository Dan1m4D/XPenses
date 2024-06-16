package com.d479.xpenses.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Greeting(modifier: Modifier = Modifier, name: String) {
    Row {
        Text(
            text = "Hello ",
            color = MaterialTheme.colorScheme.outline,
            fontSize = 45.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = name,
            fontSize = 45.sp,
            fontWeight = FontWeight.Bold,
        )
    }

}