package com.aarw.dexanalyze.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun MetricCard(
    label: String,
    value: String,
    unit: String = "",
    delta: String? = null,
    deltaIsIncrease: Boolean? = null,
    deltaPositive: Boolean? = null,
    accent: Color = MaterialTheme.colorScheme.primary,
    tooltip: String? = null,
    trendData: List<Float>? = null,
    percentile: Int? = null,
    percentileValue: String? = null,
    percentileText: String? = null,
    percentileReverse: Boolean = false,
    percentileStops: List<Float>? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Left Side: Label, Value, Delta
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (tooltip != null) {
                        InfoIconTooltip(text = tooltip, title = label)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineMedium,
                        color = accent
                    )
                    if (unit.isNotEmpty()) {
                        Text(
                            text = unit,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
                if (delta != null && deltaPositive != null) {
                    val deltaColor = if (deltaPositive) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.error
                    val arrow = when (deltaIsIncrease) {
                        true -> "▲"
                        false -> "▼"
                        null -> if (deltaPositive) "▲" else "▼"
                    }
                    Text(
                        text = "$arrow $delta",
                        style = MaterialTheme.typography.labelSmall,
                        color = deltaColor
                    )
                }
            }

            // Right Side: Sparkline Trend
            if (trendData != null && trendData.size > 1) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(40.dp)
                        .padding(top = 8.dp)
                ) {
                    Sparkline(data = trendData, color = accent)
                }
            }
        }

        // Bottom Side: Percentile Bar
        if (percentile != null && percentileValue != null) {
            Spacer(Modifier.height(16.dp))
            val stops = percentileStops ?: if (percentileReverse) listOf(0.25f, 0.75f) else listOf(0.25f, 0.75f)
            PercentileBar(
                label = "Percentile Ranking",
                value = percentileValue,
                percentile = percentile,
                barColor = accent,
                reverseColors = percentileReverse,
                sectionStops = stops
            )
            if (percentileText != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = percentileText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun Sparkline(
    data: List<Float>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        if (data.size < 2) return@Canvas

        val w = size.width
        val h = size.height
        val vPad = h * 0.12f

        val min = data.min()
        val max = data.max()
        val range = (max - min).coerceAtLeast(0.01f)

        val pts = data.mapIndexed { i, v ->
            Offset(
                x = i.toFloat() / (data.size - 1) * w,
                y = h - vPad - ((v - min) / range) * (h - vPad * 2f)
            )
        }

        // Smooth cubic bezier: horizontal control points keep tangent smooth
        val linePath = Path()
        linePath.moveTo(pts[0].x, pts[0].y)
        for (i in 1 until pts.size) {
            val dx = pts[i].x - pts[i - 1].x
            linePath.cubicTo(
                pts[i - 1].x + dx * 0.5f, pts[i - 1].y,
                pts[i].x - dx * 0.5f, pts[i].y,
                pts[i].x, pts[i].y
            )
        }

        // Gradient fill under line
        val fillPath = Path()
        fillPath.addPath(linePath)
        fillPath.lineTo(pts.last().x, h)
        fillPath.lineTo(pts[0].x, h)
        fillPath.close()
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(color.copy(alpha = 0.28f), Color.Transparent),
                startY = 0f, endY = h
            )
        )

        // Line
        drawPath(
            path = linePath,
            color = color,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Dot at the latest (rightmost) data point
        drawCircle(color = color, radius = 3.dp.toPx(), center = pts.last())
        drawCircle(color = Color.White.copy(alpha = 0.75f), radius = 1.5f.dp.toPx(), center = pts.last())
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    tooltip: String? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (tooltip != null) {
            InfoIconTooltip(text = tooltip)
        }
    }
}
