package com.aarw.dexanalyze.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aarw.dexanalyze.data.model.Composition

// Professional heat-map: cool (low fat) → warm (high fat)
private val FatCyan        = Color(red = 0.00f, green = 0.80f, blue = 0.95f)
private val FatGreen       = Color(red = 0.20f, green = 0.75f, blue = 0.30f)
private val FatYellow      = Color(red = 0.95f, green = 0.75f, blue = 0.10f)
private val FatOrange      = Color(red = 0.95f, green = 0.50f, blue = 0.15f)
private val FatRed         = Color(red = 0.85f, green = 0.15f, blue = 0.20f)

private val FatColorStops = listOf(
    0.00f to FatCyan,
    0.25f to FatGreen,
    0.50f to FatYellow,
    0.75f to FatOrange,
    1.00f to FatRed
)

private val LegendStops = listOf(
    "≤15%" to FatCyan,
    "20%" to FatGreen,
    "32%" to FatYellow,
    "40%" to FatOrange,
    "50%+" to FatRed
)

internal fun fatColor(pct: Double): Color {
    val t = ((pct.coerceIn(10.0, 50.0) - 10.0) / 40.0).toFloat()
    val lo = FatColorStops.last { it.first <= t }
    val hi = FatColorStops.first { it.first >= t }
    if (lo.first == hi.first) return lo.second
    return lerp(lo.second, hi.second, (t - lo.first) / (hi.first - lo.first))
}

@Composable
fun BodyCompositionMapCard(composition: Composition) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Body Fat Distribution",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        BodyFatSilhouette(
            composition = composition,
            neutralColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
            modifier = Modifier
                .width(180.dp)
                .height(360.dp)
        )

        FatColorLegend()
    }
}

@Composable
private fun FatColorLegend() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Body Fat %",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendStops.forEach { (label, color) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BodyFatSilhouette(
    composition: Composition,
    neutralColor: Color,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        val sx = size.width / 200f
        val sy = size.height / 400f

        fun x(v: Float) = v * sx
        fun y(v: Float) = v * sy
        fun cr(r: Float) = CornerRadius(r * sx, r * sy)

        val labelPaint = Paint().apply {
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            color = android.graphics.Color.WHITE
            setShadowLayer(2f, 0f, 1f, android.graphics.Color.BLACK)
            with(density) { textSize = 8.5.sp.toPx() }
        }

        fun region(
            left: Float, top: Float, rw: Float, rh: Float,
            pct: Double?, corner: Float = 14f, stroke: Boolean = true
        ) {
            val col = if (pct != null) fatColor(pct) else neutralColor
            drawRoundRect(
                color = col,
                topLeft = Offset(x(left), y(top)),
                size = Size(x(rw), y(rh)),
                cornerRadius = cr(corner)
            )
            if (stroke && pct != null) {
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.3f),
                    topLeft = Offset(x(left), y(top)),
                    size = Size(x(rw), y(rh)),
                    cornerRadius = cr(corner),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
                )
            }
            if (pct != null && pct > 0) {
                drawContext.canvas.nativeCanvas.drawText(
                    "%.1f%%".format(pct),
                    x(left + rw / 2f),
                    y(top + rh / 2f) + labelPaint.textSize / 3f,
                    labelPaint
                )
            }
        }

        drawOval(
            color = neutralColor,
            topLeft = Offset(x(76f), y(2f)),
            size = Size(x(48f), y(46f))
        )
        drawRoundRect(
            color = neutralColor,
            topLeft = Offset(x(88f), y(47f)),
            size = Size(x(24f), y(15f)),
            cornerRadius = cr(5f)
        )

        region(left = 10f, top = 62f, rw = 38f, rh = 130f, pct = composition.lArm?.regionFatPct, corner = 19f)
        region(left = 152f, top = 62f, rw = 38f, rh = 130f, pct = composition.rArm?.regionFatPct, corner = 19f)
        region(left = 48f, top = 62f, rw = 104f, rh = 82f, pct = composition.android?.regionFatPct, corner = 12f)
        region(left = 42f, top = 142f, rw = 116f, rh = 58f, pct = composition.gynoid?.regionFatPct, corner = 12f)
        region(left = 45f, top = 202f, rw = 52f, rh = 166f, pct = composition.lLeg?.regionFatPct, corner = 16f)
        region(left = 103f, top = 202f, rw = 52f, rh = 166f, pct = composition.rLeg?.regionFatPct, corner = 16f)

        drawRoundRect(
            color = neutralColor,
            topLeft = Offset(x(36f), y(368f)),
            size = Size(x(58f), y(24f)),
            cornerRadius = cr(10f)
        )
        drawRoundRect(
            color = neutralColor,
            topLeft = Offset(x(106f), y(368f)),
            size = Size(x(58f), y(24f)),
            cornerRadius = cr(10f)
        )
    }
}
