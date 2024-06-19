package com.d479.xpenses.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale")
@Composable
fun BarGraph(data: List<Double>, title: String = "") {
    var max by rememberSaveable { mutableDoubleStateOf(0.0) }
    max = data.maxOrNull() ?: 0.0

    val configuration = LocalConfiguration.current
    val width = configuration.screenWidthDp.dp

    val barWidth = width / 10

    Column {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { value ->
                // Calculate the percentage of the value compared to the max value
                val percentage = (value / max) * 100
                // Calculate the height of the bar based on the percentage and a max height of 100
                val barHeight = (percentage / 100) * 80

                val barLabel = if (value <= 0.0) "" else String.format("%.2f", value)

                Bar(
                    height = barHeight,
                    width = barWidth,
                    label = barLabel,
                    maxHeight = 110.0

                )
            }
        }
    }

}

@Composable
fun Bar(height: Double, maxHeight: Double, width: Dp, label: String, modifier: Modifier = Modifier) {


    Column(
        modifier = modifier
            .width(width)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {

        Box(
            modifier = Modifier
                .width(width)
                .height(height.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 4.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 4.dp
                    )
                )
        )
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }


}
