package com.d479.xpenses.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("DefaultLocale")
@Composable
fun AnalyticsCard(
    modifier: Modifier = Modifier,
    data: List<Double> = emptyList(),
    barTitle: String
) {

    val localConfiguration = LocalConfiguration.current
    val screenHeight = localConfiguration.screenHeightDp.dp
    val cardHeight = screenHeight * 0.3f


    val totalSpent = data.sum()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .height(cardHeight)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "You've spent", style = MaterialTheme.typography.titleSmall)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = String.format("%.2f", totalSpent),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(text = "€", style = MaterialTheme.typography.titleMedium)
                }
            }

            if (data.isNotEmpty())  {
                // if data length is less than 7, pad the data with 0s before feed to the barGraph
                val paddedData = if (data.size < 7) {
                    data + List(7 - data.size) { 0.0 }
                } else if (data.size > 7) {
                    // if data length is more than 7, take the last 7 elements
                    data.takeLast(7)
                } else {
                    data
                }
                BarGraph(data = paddedData, title = barTitle)


            } else {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                    Text(text = "No transactions yet")
                }
            }

        }
    }

}

