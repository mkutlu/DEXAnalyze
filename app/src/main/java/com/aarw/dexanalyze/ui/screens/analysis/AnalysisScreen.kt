package com.aarw.dexanalyze.ui.screens.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aarw.dexanalyze.data.model.ScanResult
import com.aarw.dexanalyze.ui.LocalUseMetric
import com.aarw.dexanalyze.ui.Tooltips
import com.aarw.dexanalyze.ui.components.BodyCompositionMapCard
import com.aarw.dexanalyze.ui.massUnit
import com.aarw.dexanalyze.ui.toDisplayMass
import com.aarw.dexanalyze.ui.components.HBarItem
import com.aarw.dexanalyze.ui.components.HorizontalBarChart
import com.aarw.dexanalyze.ui.components.SectionHeader
import com.aarw.dexanalyze.ui.theme.Bone

@Composable
fun AnalysisScreen(viewModel: AnalysisViewModel) {
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
            state.selectedScan != null -> AnalysisContent(
                state = state,
                onSelectScan = viewModel::selectScan
            )
        }
    }
}

@Composable
private fun AnalysisContent(state: AnalysisUiState, onSelectScan: (Int) -> Unit) {
    val scan = state.selectedScan ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Analysis",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (state.scans.size > 1) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(state.scans) { index, s ->
                        FilterChip(
                            selected = index == state.selectedIndex,
                            onClick = { onSelectScan(index) },
                            label = { Text(s.scanDate.take(10)) }
                        )
                    }
                }
            }
        }

        if (scan.composition != null) {
            item { SectionHeader("Body Composition Map", tooltip = Tooltips.BODY_MAP) }
            item { BodyCompositionMapCard(scan.composition) }
        }

        item { SectionHeader("Regional Fat %", tooltip = Tooltips.REGIONAL_FAT) }
        item { RegionalCompositionCard(scan) }

        item { SectionHeader("Left / Right Symmetry", tooltip = Tooltips.SYMMETRY) }
        item { SymmetryCard(scan) }

        item { SectionHeader("Bone Density", tooltip = Tooltips.BONE_DENSITY_SECTION) }
        item { BoneDensityCard(scan) }

        item { SectionHeader("Body Mass Breakdown", tooltip = Tooltips.MASS_BREAKDOWN) }
        item { MassBreakdownCard(scan) }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun RegionalCompositionCard(scan: ScanResult) {
    val c = scan.composition ?: return
    val fatColor = MaterialTheme.colorScheme.secondary
    val items = listOfNotNull(
        c.total?.let { HBarItem("Total", it.regionFatPct.toFloat(), fatColor) },
        c.trunk?.let { HBarItem("Trunk", it.regionFatPct.toFloat(), fatColor) },
        c.android?.let { HBarItem("Android", it.regionFatPct.toFloat(), MaterialTheme.colorScheme.error) },
        c.gynoid?.let { HBarItem("Gynoid", it.regionFatPct.toFloat(), fatColor) },
        c.arms?.let { HBarItem("Arms", it.regionFatPct.toFloat(), fatColor) },
        c.legs?.let { HBarItem("Legs", it.regionFatPct.toFloat(), fatColor) },
        c.limbs?.let { HBarItem("Limbs", it.regionFatPct.toFloat(), fatColor) }
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        HorizontalBarChart(items = items, maxValue = 50f, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun SymmetryCard(scan: ScanResult) {
    val c = scan.composition ?: return
    val useMetric = LocalUseMetric.current
    val mUnit = massUnit(useMetric)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SymmetryRow("Arm Lean", (c.lArm?.leanMassKg ?: 0.0).toDisplayMass(useMetric), (c.rArm?.leanMassKg ?: 0.0).toDisplayMass(useMetric), mUnit)
        SymmetryRow("Arm Fat",  (c.lArm?.fatMassKg  ?: 0.0).toDisplayMass(useMetric), (c.rArm?.fatMassKg  ?: 0.0).toDisplayMass(useMetric), mUnit)
        SymmetryRow("Leg Lean", (c.lLeg?.leanMassKg ?: 0.0).toDisplayMass(useMetric), (c.rLeg?.leanMassKg ?: 0.0).toDisplayMass(useMetric), mUnit)
        SymmetryRow("Leg Fat",  (c.lLeg?.fatMassKg  ?: 0.0).toDisplayMass(useMetric), (c.rLeg?.fatMassKg  ?: 0.0).toDisplayMass(useMetric), mUnit)
    }
}

@Composable
private fun SymmetryRow(label: String, left: Double, right: Double, unit: String) {
    val diff = ((left - right) / ((left + right) / 2) * 100).let { "%.1f".format(it) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "L  %.2f $unit".format(left),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$diff%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            "%.2f $unit  R".format(right),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun BoneDensityCard(scan: ScanResult) {
    val bd = scan.boneDensity ?: return
    val rows = listOfNotNull(
        bd.total?.let { Triple("Total", it.bmdGCm2, it.bmcG) },
        bd.trunk?.let { Triple("Trunk", it.bmdGCm2, it.bmcG) },
        bd.lArm?.let { Triple("L Arm", it.bmdGCm2, it.bmcG) },
        bd.rArm?.let { Triple("R Arm", it.bmdGCm2, it.bmcG) },
        bd.lLeg?.let { Triple("L Leg", it.bmdGCm2, it.bmcG) },
        bd.rLeg?.let { Triple("R Leg", it.bmdGCm2, it.bmcG) }
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Region", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("BMD g/cm²", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("BMC g", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        rows.forEach { (region, bmd, bmc) ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(region, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("%.3f".format(bmd), style = MaterialTheme.typography.bodyMedium, color = Bone)
                Text("%.0f".format(bmc), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun MassBreakdownCard(scan: ScanResult) {
    val t = scan.composition?.total ?: return
    val useMetric = LocalUseMetric.current
    val mUnit = massUnit(useMetric)
    val items = listOf(
        HBarItem("Fat",  t.fatMassKg.toDisplayMass(useMetric).toFloat(),  MaterialTheme.colorScheme.secondary),
        HBarItem("Lean", t.leanMassKg.toDisplayMass(useMetric).toFloat(), MaterialTheme.colorScheme.tertiary),
        HBarItem("Bone", t.boneMassKg.toDisplayMass(useMetric).toFloat(), Bone)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        HorizontalBarChart(
            items = items,
            maxValue = t.totalMassKg.toDisplayMass(useMetric).toFloat().coerceAtLeast(1f),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Total: ${"%.1f".format(t.totalMassKg.toDisplayMass(useMetric))} $mUnit",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
