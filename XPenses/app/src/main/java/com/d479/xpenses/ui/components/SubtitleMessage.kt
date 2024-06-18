package com.d479.xpenses.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun SubtitleMessage(
    modifier: Modifier = Modifier,
    text1: String = "",
    text2: String = "",
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Row {
        Text(
            text = text1,
            color = color,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = text2,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
        )
    }

}