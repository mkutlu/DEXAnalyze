package com.aarw.dexanalyze.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aarw.dexanalyze.ui.theme.ChartGrid
import com.aarw.dexanalyze.ui.theme.OnSurfaceVariant

data class ChartEntry(val label: String, val value: Float)
data class HBarItem(val label: String, val value: Float, val color: Color)
data class StackedBarEntry(val label: String, val fat: Float, val lean: Float)

@Composable
fun LineChart(
    data: List<ChartEntry>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    unit: String = ""
) {
    val density = LocalDensity.current
    val labelColor = OnSurfaceVariant.toArgb()
    val gridColor = ChartGrid

    val textPaint = remember(density) {
        android.graphics.Paint().apply {
            color = labelColor
            textSize = with(density) { 10.sp.toPx() }
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.RIGHT
        }
    }
    val xTextPaint = remember(density) {
        android.graphics.Paint().apply {
            color = labelColor
            textSize = with(density) { 10.sp.toPx() }
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }

    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas

        val padLeft = 60f
        val padRight = 16f
        val padTop = 16f
        val padBottom = 28f

        val chartW = size.width - padLeft - padRight
        val chartH = size.height - padTop - padBottom

        val minVal = data.minOf { it.value }
        val maxVal = data.maxOf { it.value }
        val range = (maxVal - minVal).coerceAtLeast(0.01f)
        val yMin = minVal - range * 0.1f
        val yMax = maxVal + range * 0.1f
        val yRange = yMax - yMin

        val gridCount = 4
        repeat(gridCount + 1) { i ->
            val fraction = i.toFloat() / gridCount
            val y = padTop + chartH * (1f - fraction)
            val value = yMin + yRange * fraction
            drawLine(gridColor, Offset(padLeft, y), Offset(padLeft + chartW, y), 1f)
            val labelText = when {
                range < 0.1f -> "%.3f".format(value) + unit
                range < 1f -> "%.2f".format(value) + unit
                else -> "%.1f".format(value) + unit
            }
            drawContext.canvas.nativeCanvas.drawText(
                labelText,
                padLeft - 4f, y + 4f, textPaint
            )
        }

        val points = data.mapIndexed { i, entry ->
            val x = padLeft + chartW * (i.toFloat() / (data.size - 1))
            val y = padTop + chartH * (1f - (entry.value - yMin) / yRange)
            Offset(x, y)
        }

        val fillPath = Path().apply {
            moveTo(points.first().x, padTop + chartH)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, padTop + chartH)
            close()
        }
        drawPath(
            fillPath,
            Brush.verticalGradient(
                listOf(lineColor.copy(alpha = 0.35f), Color.Transparent),
                startY = padTop, endY = padTop + chartH
            )
        )

        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(linePath, lineColor, style = Stroke(3f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        points.forEach { p ->
            drawCircle(lineColor, 5f, p)
            drawCircle(Color(0xFF131829), 2.5f, p)
        }

        data.forEachIndexed { i, entry ->
            val x = padLeft + chartW * (i.toFloat() / (data.size - 1))
            when (i) {
                0 -> {
                    xTextPaint.textAlign = android.graphics.Paint.Align.LEFT
                    drawContext.canvas.nativeCanvas.drawText(entry.label, x + 8f, size.height - 4f, xTextPaint)
                    xTextPaint.textAlign = android.graphics.Paint.Align.CENTER
                }
                data.size - 1 -> {
                    xTextPaint.textAlign = android.graphics.Paint.Align.RIGHT
                    drawContext.canvas.nativeCanvas.drawText(entry.label, x - 8f, size.height - 4f, xTextPaint)
                    xTextPaint.textAlign = android.graphics.Paint.Align.CENTER
                }
                else -> {
                    drawContext.canvas.nativeCanvas.drawText(entry.label, x, size.height - 4f, xTextPaint)
                }
            }
        }
    }
}

@Composable
fun HorizontalBarChart(
    items: List<HBarItem>,
    maxValue: Float = 50f,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(56.dp)
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth((item.value / maxValue).coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(4.dp))
                            .background(item.color)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "%.1f%%".format(item.value),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(42.dp)
                )
            }
        }
    }
}

@Composable
fun StackedBarChart(
    data: List<StackedBarEntry>,
    modifier: Modifier = Modifier,
    fatColor: Color = MaterialTheme.colorScheme.secondary,
    leanColor: Color = MaterialTheme.colorScheme.tertiary
) {
    val maxVal = data.maxOf { it.fat + it.lean }.coerceAtLeast(0.01f)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        data.forEach { entry ->
            val total = entry.fat + entry.lean
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                val barHeight = 140.dp
                if (total > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(barHeight * (total / maxVal))
                    ) {
                        Column(Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(entry.fat / total)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(fatColor)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(entry.lean / total)
                                    .clip(
                                        if (entry.fat == 0f)
                                            RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                        else RoundedCornerShape(0.dp)
                                    )
                                    .background(leanColor)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    entry.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PercentileBar(
    label: String,
    value: String,
    percentile: Int,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    tooltip: String? = null,
    reverseColors: Boolean = false,
    sectionStops: List<Float> = listOf(0.40f, 0.75f) // Default: Green 40%, Yellow 35%, Red 25%
) {
    Column(modifier = modifier) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (tooltip != null) InfoIconTooltip(text = tooltip, title = label)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(value, style = MaterialTheme.typography.titleSmall, color = barColor)
                Text(
                    "${percentile}th",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        val colors = listOf(
            Color(0xFF8DC34A), // Muted Green
            Color(0xFFFDD835), // Muted Yellow
            Color(0xFFE57373)  // Muted Red
        )
        val finalColors = if (reverseColors) colors.reversed() else colors
        
        // Create gradient stops based on 2 section stops (3 sections)
        // [0.0 to stops[0]] = Color[0], [stops[0] to stops[1]] = Color[1], [stops[1] to 1.0] = Color[2]
        val gradientStops = remember(finalColors, sectionStops, reverseColors) {
            val s1 = sectionStops[0]
            val s2 = sectionStops[1]
            arrayOf(
                0.0f to finalColors[0],
                s1 to finalColors[0],
                s1 to finalColors[1],
                s2 to finalColors[1],
                s2 to finalColors[2],
                1.0f to finalColors[2]
            )
        }

        BoxWithConstraints(
            Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(brush = Brush.horizontalGradient(colorStops = gradientStops))
        ) {
            // Center the 3 dp marker on the exact percentile position
            val markerOffset = (this.maxWidth * (percentile / 100f)) - 1.5.dp
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(3.dp)
                    .offset(x = markerOffset)
                    .background(Color.White.copy(alpha = 0.95f))
            )
        }
    }
}
