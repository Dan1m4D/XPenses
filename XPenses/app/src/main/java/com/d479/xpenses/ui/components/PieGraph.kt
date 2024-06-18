package com.d479.xpenses.ui.components

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
import kotlin.math.roundToInt

@Composable
fun PieGraph(
    modifier: Modifier = Modifier,
    data: Map<String, Double>,
    colors: List<String>
) {
    val total = data.values.sum()
    var startAngle = 0f
    val colorsList = colors.map { Color(android.graphics.Color.parseColor(it)) }

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

        data.values.forEachIndexed { index, item ->
            val sweep = (item / total) * 360f
            drawArc(
                color = colorsList[index % colors.size],
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
        data.entries.toList().forEachIndexed { index, entry ->
            val (category, amount) = entry
            val percentage = (amount / total) * 100

            LegendItem(
                color = colorsList[index],
                title = category,
                amount = amount,
                percentage = percentage.roundToInt()
            )
        }
    }
}