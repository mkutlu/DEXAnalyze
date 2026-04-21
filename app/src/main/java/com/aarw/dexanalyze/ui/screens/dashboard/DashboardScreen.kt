package com.aarw.dexanalyze.ui.screens.dashboard

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
import com.aarw.dexanalyze.data.model.ScanResult
import com.aarw.dexanalyze.ui.LocalUseMetric
import com.aarw.dexanalyze.ui.Tooltips
import com.aarw.dexanalyze.ui.components.MetricCard
import com.aarw.dexanalyze.ui.components.SectionHeader
import com.aarw.dexanalyze.ui.massUnit
import com.aarw.dexanalyze.ui.toDisplayMass
import com.aarw.dexanalyze.ui.theme.Bone
import kotlin.math.abs

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
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
            state.scan != null -> DashboardContent(state)
        }
    }
}

@Composable
private fun DashboardContent(state: DashboardUiState) {
    val scan = state.scan ?: return
    val previous = state.previousScan
    val history = state.history
    val useMetric = LocalUseMetric.current
    val mUnit = massUnit(useMetric)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { ScanHeader(scan) }

        if (scan.composition != null) {
            item { SectionHeader("Body Composition") }

            item {
                val prevTotal = previous?.composition?.total
                val totalKg = scan.composition.total?.totalMassKg ?: 0.0
                val prevKg = prevTotal?.totalMassKg
                val totalWeightHistory = history.mapNotNull { it.composition?.total?.totalMassKg?.toDisplayMass(useMetric)?.toFloat() }.reversed()
                
                MetricCard(
                    label = "Total Weight",
                    value = "%.1f".format(totalKg.toDisplayMass(useMetric)),
                    unit = mUnit,
                    delta = prevKg?.let { "%.1f $mUnit".format(abs((totalKg - it).toDisplayMass(useMetric))) },
                    deltaIsIncrease = prevKg?.let { totalKg > it },
                    deltaPositive = prevKg?.let { totalKg < it },
                    tooltip = Tooltips.TOTAL_WEIGHT,
                    trendData = if (totalWeightHistory.size > 1) totalWeightHistory else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                val fatPct = scan.composition.total?.regionFatPct ?: 0.0
                val prevFatPct = previous?.composition?.total?.regionFatPct
                val fatPctHistory = history.mapNotNull { it.composition?.total?.regionFatPct?.toFloat() }.reversed()
                val fatPercentile = scan.percentiles?.metrics?.totalBodyFatPct
                
                MetricCard(
                    label = "Body Fat",
                    value = "%.1f".format(fatPct),
                    unit = "%",
                    delta = prevFatPct?.let { "%.1f%%".format(abs(fatPct - it)) },
                    deltaIsIncrease = prevFatPct?.let { fatPct > it },
                    deltaPositive = prevFatPct?.let { fatPct < it },
                    accent = MaterialTheme.colorScheme.secondary,
                    tooltip = Tooltips.BODY_FAT_PCT,
                    trendData = if (fatPctHistory.size > 1) fatPctHistory else null,
                    percentile = fatPercentile?.percentile,
                    percentileValue = fatPercentile?.let { "%.1f%%".format(it.value) },
                    percentileText = fatPercentile?.percentile?.let { getPercentileText("body_fat", it, false) },
                    percentileReverse = false, // Green (L) -> Red (R)
                    percentileStops = listOf(0.45f, 0.75f),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                val leanKg = scan.composition.total?.leanMassKg ?: 0.0
                val prevLeanKg = previous?.composition?.total?.leanMassKg
                val leanHistory = history.mapNotNull { it.composition?.total?.leanMassKg?.toDisplayMass(useMetric)?.toFloat() }.reversed()
                val lmiPercentile = scan.percentiles?.metrics?.totalLmiKgM2

                MetricCard(
                    label = "Lean Mass",
                    value = "%.1f".format(leanKg.toDisplayMass(useMetric)),
                    unit = mUnit,
                    delta = prevLeanKg?.let { "%.1f $mUnit".format(abs((leanKg - it).toDisplayMass(useMetric))) },
                    deltaIsIncrease = prevLeanKg?.let { leanKg > it },
                    deltaPositive = prevLeanKg?.let { leanKg > it },
                    accent = MaterialTheme.colorScheme.tertiary,
                    tooltip = Tooltips.LEAN_MASS,
                    trendData = if (leanHistory.size > 1) leanHistory else null,
                    percentile = lmiPercentile?.percentile,
                    percentileValue = lmiPercentile?.let { "%.2f kg/m²".format(it.value) },
                    percentileText = lmiPercentile?.percentile?.let { getPercentileText("lean_mass", it, true) },
                    percentileReverse = true, // Red (L) -> Green (R)
                    percentileStops = listOf(0.25f, 0.55f),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                val vatKg = scan.visceralFat?.vatMassKg ?: 0.0
                val prevVatKg = previous?.visceralFat?.vatMassKg
                val vatHistory = history.mapNotNull { it.visceralFat?.vatMassKg?.toDisplayMass(useMetric)?.toFloat() }.reversed()
                val vatPercentile = scan.percentiles?.metrics?.vatMassKg

                MetricCard(
                    label = "VAT Mass",
                    value = "%.2f".format(vatKg.toDisplayMass(useMetric)),
                    unit = mUnit,
                    delta = prevVatKg?.let { "%.2f $mUnit".format(abs((vatKg - it).toDisplayMass(useMetric))) },
                    deltaIsIncrease = prevVatKg?.let { vatKg > it },
                    deltaPositive = prevVatKg?.let { vatKg < it },
                    accent = MaterialTheme.colorScheme.error,
                    tooltip = Tooltips.VAT_MASS,
                    trendData = if (vatHistory.size > 1) vatHistory else null,
                    percentile = vatPercentile?.percentile,
                    percentileValue = vatPercentile?.let { "%.2f $mUnit".format(it.value) },
                    percentileText = vatPercentile?.percentile?.let { getPercentileText("vat", it, false) },
                    percentileReverse = false, // Green (L) -> Red (R)
                    percentileStops = listOf(0.45f, 0.75f),
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }

        if (scan.boneDensity != null) {
            item { SectionHeader("Bone Density", tooltip = Tooltips.TOTAL_BMD, modifier = Modifier.padding(top = 4.dp)) }
            item {
                val bmd = scan.boneDensity.total?.bmdGCm2 ?: 0.0
                val bmdHistory = history.mapNotNull { it.boneDensity?.total?.bmdGCm2?.toFloat() }.reversed()
                val bmdPercentile = scan.percentiles?.metrics?.boneDensityGCm2

                MetricCard(
                    label = "Total BMD",
                    value = "%.3f".format(bmd),
                    unit = "g/cm²",
                    accent = Bone,
                    tooltip = Tooltips.TOTAL_BMD,
                    trendData = if (bmdHistory.size > 1) bmdHistory else null,
                    percentile = bmdPercentile?.percentile,
                    percentileValue = bmdPercentile?.let { "%.3f".format(it.value) },
                    percentileText = bmdPercentile?.percentile?.let { getPercentileText("bone_density", it, true) },
                    percentileReverse = true, // Red (L) -> Green (R)
                    percentileStops = listOf(0.20f, 0.55f),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                val limbLmiPercentile = scan.percentiles?.metrics?.limbLmiKgM2
                val limbLmiKg = limbLmiPercentile?.value ?: 0.0
                val prevLimbLmiKg = previous?.percentiles?.metrics?.limbLmiKgM2?.value
                val limbLeanHistory = history.mapNotNull { it.percentiles?.metrics?.limbLmiKgM2?.value?.toFloat() }.reversed()

                MetricCard(
                    label = "Limb Lean Mass",
                    value = "%.2f".format(limbLmiKg),
                    unit = "kg/m²",
                    delta = prevLimbLmiKg?.let { "%.2f".format(abs(limbLmiKg - it)) },
                    deltaIsIncrease = prevLimbLmiKg?.let { limbLmiKg > it },
                    deltaPositive = prevLimbLmiKg?.let { limbLmiKg > it },
                    accent = MaterialTheme.colorScheme.tertiary,
                    tooltip = Tooltips.PERCENTILE_LIMB_LMI,
                    trendData = if (limbLeanHistory.size > 1) limbLeanHistory else null,
                    percentile = limbLmiPercentile?.percentile,
                    percentileValue = limbLmiPercentile?.let { "LMI: %.2f kg/m²".format(it.value) },
                    percentileText = limbLmiPercentile?.percentile?.let { getPercentileText("limb_lmi", it, true) },
                    percentileReverse = true, // Red (L) -> Green (R)
                    percentileStops = listOf(0.25f, 0.55f),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (scan.rmr?.estimates?.isNotEmpty() == true) {
            item { SectionHeader("Metabolism", modifier = Modifier.padding(top = 4.dp)) }
            item {
                val estimate = scan.rmr.estimates?.first()
                val kcal = estimate?.kcalPerDay ?: 0.0
                MetricCard(
                    label = "Resting Metabolic Rate",
                    value = "%.0f".format(kcal),
                    unit = "kcal/day",
                    accent = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (scan.composition != null) {
            item {
                val androidFat = scan.composition.android?.regionFatPct ?: 0.0
                val gynoidFat = scan.composition.gynoid?.regionFatPct ?: 0.0
                val ratio = if (gynoidFat > 0) androidFat / gynoidFat else 0.0
                SectionHeader("Android / Gynoid", modifier = Modifier.padding(top = 4.dp))
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard("Android Fat", "%.1f".format(androidFat), "%", accent = MaterialTheme.colorScheme.secondary, tooltip = Tooltips.ANDROID_FAT, modifier = Modifier.weight(1f))
                    MetricCard("Gynoid Fat", "%.1f".format(gynoidFat), "%", accent = MaterialTheme.colorScheme.primary, tooltip = Tooltips.GYNOID_FAT, modifier = Modifier.weight(1f))
                    MetricCard("A/G Ratio", "%.2f".format(ratio), accent = if (ratio > 1.0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary, tooltip = Tooltips.AG_RATIO, modifier = Modifier.weight(1f))
                }
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun ScanHeader(scan: ScanResult) {
    val useMetric = LocalUseMetric.current
    val mUnit = massUnit(useMetric)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Latest Scan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(scan.scanDate.take(10), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(scan.location?.toString() ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        val total = scan.composition?.total
        if (total != null) {
            Column(horizontalAlignment = Alignment.End) {
                Text("Fat Mass", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("%.1f $mUnit".format(total.fatMassKg.toDisplayMass(useMetric)), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary)
                Text("Lean Mass", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("%.1f $mUnit".format(total.leanMassKg.toDisplayMass(useMetric)), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}

private fun getPercentileText(metric: String, percentile: Int, isHigherBetter: Boolean): String {
    val worse = 100 - percentile
    return when (metric) {
        "body_fat" -> "$worse% of people have higher body fat % than you"
        "vat" -> "$worse% of people have higher visceral fat than you"
        "lean_mass" -> "You have higher lean mass than $worse% of people"
        "bone_density" -> "You have higher bone density than $worse% of people"
        "limb_lmi" -> "You have higher limb lean mass than $worse% of people"
        else -> ""
    }
}
