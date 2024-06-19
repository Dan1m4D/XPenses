package com.d479.xpenses.ui.components

import android.graphics.Color.parseColor
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.d479.xpenses.models.Category
import kotlin.math.roundToInt

@Composable
fun PieGraph(
    modifier: Modifier = Modifier,
    data: Map<Category, Double>) {
    val total = data.values.sum()
    var startAngle = 0f

    Log.d("PieGraph", "Data: $data")

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .padding(16.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = (canvasWidth.coerceAtMost(canvasHeight) / 2) - 2.dp.toPx()
        val center = Offset(x = canvasWidth / 2, y = canvasHeight / 2)

        // transform color string to Color

        data.entries.forEach { item ->
            val (category, amount) = item
            val sweep = (amount / total) * 360f
            val parsedColor = Color(parseColor(
                if (category.color.isEmpty()) "#000000" else category.color
            ))

            drawArc(
                color = parsedColor,
                startAngle = startAngle,
                sweepAngle = sweep.toFloat(),
                useCenter = true,
                topLeft = Offset(
                    x = center.x - radius,
                    y = center.y - radius
                ),
                size = Size(
                    width = radius * 2,
                    height = radius * 2
                )
            )
            startAngle += sweep.toFloat()
        }
    }

    /// Draw the legend
    Column(modifier = Modifier.padding(top = 16.dp)) {
        data.entries.toList().forEach { entry ->
            val (category, amount) = entry
            val percentage = (amount / total) * 100

            // transform color string to Color
            val parsedColor = Color(parseColor(
                if (category.color.isEmpty()) "#000000" else category.color
            ))

            LegendItem(
                color = parsedColor,
                title = category.name ?: "Others",
                amount = amount,
                percentage = percentage.roundToInt()
            )
        }
    }
}