package com.aarw.dexanalyze.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aarw.dexanalyze.ui.LocalUseMetric
import com.aarw.dexanalyze.ui.components.*
import com.aarw.dexanalyze.ui.massUnit
import com.aarw.dexanalyze.ui.theme.Bone
import com.aarw.dexanalyze.ui.toDisplayMass

@Composable
fun ProgressScreen(viewModel: ProgressViewModel) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            state.error != null -> Text(
                "Error: ${state.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center).padding(24.dp)
            )
            state.scans.isNotEmpty() -> ProgressContent(state)
        }
    }
}

@Composable
private fun ProgressContent(state: ProgressUiState) {
    val useMetric = LocalUseMetric.current
    val mUnit = massUnit(useMetric)

    // Recompute weight-based entries based on unit preference
    val weightEntries = androidx.compose.runtime.remember(state.scans, useMetric) {
        state.scans.map { s ->
            ChartEntry(s.scanDate.take(10).takeLast(5), (s.composition?.total?.totalMassKg ?: 0.0).toDisplayMass(useMetric).toFloat())
        }
    }
    val vatEntries = androidx.compose.runtime.remember(state.scans, useMetric) {
        state.scans.map { s ->
            ChartEntry(s.scanDate.take(10).takeLast(5), (s.visceralFat?.vatMassKg ?: 0.0).toDisplayMass(useMetric).toFloat())
        }
    }
    val stackedEntries = androidx.compose.runtime.remember(state.scans, useMetric) {
        state.scans.map { s ->
            StackedBarEntry(
                s.scanDate.take(10).substring(5),
                (s.composition?.total?.fatMassKg ?: 0.0).toDisplayMass(useMetric).toFloat(),
                (s.composition?.total?.leanMassKg ?: 0.0).toDisplayMass(useMetric).toFloat()
            )
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Progress",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "${state.scans.size} scans · ${state.scans.firstOrNull()?.scanDate?.take(10) ?: ""} – ${state.scans.lastOrNull()?.scanDate?.take(10) ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            ChartCard(title = "Body Weight ($mUnit)") {
                LineChart(
                    data = weightEntries,
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    lineColor = MaterialTheme.colorScheme.primary,
                    unit = ""
                )
            }
        }

        item {
            ChartCard(title = "Body Fat %") {
                LineChart(
                    data = state.fatPctEntries,
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    lineColor = MaterialTheme.colorScheme.secondary,
                    unit = "%"
                )
            }
        }

        item {
            ChartCard(title = "Fat vs Lean Mass ($mUnit)") {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        LegendDot("Fat Mass", MaterialTheme.colorScheme.secondary)
                        LegendDot("Lean Mass", MaterialTheme.colorScheme.tertiary)
                    }
                    StackedBarChart(
                        data = stackedEntries,
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        fatColor = MaterialTheme.colorScheme.secondary,
                        leanColor = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        item {
            ChartCard(title = "Visceral Fat Mass ($mUnit)") {
                LineChart(
                    data = vatEntries,
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    lineColor = MaterialTheme.colorScheme.error,
                    unit = ""
                )
            }
        }

        item {
            ChartCard(title = "Bone Density g/cm²") {
                val boneEntries = state.scans.map { scan ->
                    ChartEntry(scan.scanDate.take(10).takeLast(5), (scan.boneDensity?.total?.bmdGCm2 ?: 0.0).toFloat())
                }
                LineChart(
                    data = boneEntries,
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    lineColor = Bone,
                    unit = ""
                )
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun ChartCard(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun LegendDot(label: String, color: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(color)
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
