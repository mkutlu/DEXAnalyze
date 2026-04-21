package com.aarw.dexanalyze.ui

import androidx.compose.runtime.compositionLocalOf

/** True = metric (kg), false = imperial (lb). Provided at the app root. */
val LocalUseMetric = compositionLocalOf { true }

const val LB_PER_KG = 2.20462

fun Double.toDisplayMass(useMetric: Boolean): Double = if (useMetric) this else this * LB_PER_KG
fun massUnit(useMetric: Boolean): String = if (useMetric) "kg" else "lb"
