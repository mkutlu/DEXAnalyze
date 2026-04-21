package com.aarw.dexanalyze.data.model

import com.google.gson.annotations.SerializedName

data class ScanListResponse(
    val scans: List<ScanResult>? = null,
    val results: List<ScanResult>? = null,
    val pagination: Pagination? = null,
    val count: Int? = null,
    @SerializedName("sort_order") val sortOrder: String? = null
) {
    fun scanList(): List<ScanResult> = (scans ?: results).orEmpty()
}

data class Pagination(
    val page: Int,
    @SerializedName("page_size") val pageSize: Int,
    val results: Int,
    @SerializedName("has_more") val hasMore: Boolean
)

data class Location(
    val name: String? = null,
    val city: String? = null,
    val state: String? = null
) {
    override fun toString() = name ?: city ?: ""
}

data class ScanResult(
    @SerializedName("result_id") val resultId: String,
    @SerializedName(value = "start_time", alternate = ["scan_date"]) val scanDate: String,
    val location: Location? = null,
    val composition: Composition? = null,
    @SerializedName("bone_density") val boneDensity: BoneDensity? = null,
    @SerializedName("visceral_fat") val visceralFat: VisceralFat? = null,
    val percentiles: Percentiles? = null,
    val rmr: Rmr? = null
)

data class Composition(
    val total: RegionComposition? = null,
    val regions: Map<String, RegionComposition>? = null,
    @SerializedName("android_gynoid_ratio") val androidGynoidRatio: Double? = null
) {
    val android: RegionComposition? get() = regions?.get("android")
    val gynoid: RegionComposition? get() = regions?.get("gynoid")
    val trunk: RegionComposition? get() = regions?.get("trunk")
    val arms: RegionComposition? get() = regions?.get("arms")
    val legs: RegionComposition? get() = regions?.get("legs")
    val limbs: RegionComposition? get() = regions?.get("limbs")
    val lArm: RegionComposition? get() = regions?.get("left_arm")
    val rArm: RegionComposition? get() = regions?.get("right_arm")
    val lLeg: RegionComposition? get() = regions?.get("left_leg")
    val rLeg: RegionComposition? get() = regions?.get("right_leg")
}

data class RegionComposition(
    @SerializedName("fat_mass_kg") val fatMassKg: Double = 0.0,
    @SerializedName("lean_mass_kg") val leanMassKg: Double = 0.0,
    @SerializedName("bone_mass_kg") val boneMassKg: Double = 0.0,
    @SerializedName("total_mass_kg") val totalMassKg: Double = 0.0,
    @SerializedName("tissue_fat_pct") val tissueFatPct: Double = 0.0,
    @SerializedName("region_fat_pct") val regionFatPct: Double = 0.0
)

data class BoneDensity(
    val total: BoneRegion? = null,
    val regions: Map<String, BoneRegion>? = null
) {
    val lArm: BoneRegion? get() = regions?.get("left_arm")
    val rArm: BoneRegion? get() = regions?.get("right_arm")
    val lLeg: BoneRegion? get() = regions?.get("left_leg")
    val rLeg: BoneRegion? get() = regions?.get("right_leg")
    val trunk: BoneRegion? get() = regions?.get("trunk")
}

data class BoneRegion(
    @SerializedName("bone_mineral_density") val bmdGCm2: Double = 0.0,
    @SerializedName("bone_area_cm2") val areaCm2: Double = 0.0,
    @SerializedName("bone_mineral_content_g") val bmcG: Double = 0.0
)

data class VisceralFat(
    @SerializedName("vat_mass_kg") val vatMassKg: Double = 0.0,
    @SerializedName("vat_volume_cm3") val vatVolumeCm3: Double = 0.0
)

data class Percentiles(
    val params: PercentileParams? = null,
    val metrics: PercentileMetrics? = null
)

data class PercentileParams(
    val gender: String? = null,
    @SerializedName("reference_age_range") val referenceAgeRange: AgeRange? = null,
    @SerializedName("reference_dataset_size") val referenceDatasetSize: Int = 0
)

data class AgeRange(
    @SerializedName("min_years") val minYears: Int = 0,
    @SerializedName("max_years") val maxYears: Int = 0
)

data class PercentileMetrics(
    @SerializedName("total_body_fat_pct") val totalBodyFatPct: PercentileValue? = null,
    @SerializedName("vat_mass_kg") val vatMassKg: PercentileValue? = null,
    @SerializedName("total_lmi_kg_m2") val totalLmiKgM2: PercentileValue? = null,
    @SerializedName("limb_lmi_kg_m2") val limbLmiKgM2: PercentileValue? = null,
    @SerializedName("bone_density_g_cm2") val boneDensityGCm2: PercentileValue? = null
)

data class PercentileValue(
    val value: Double = 0.0,
    val percentile: Int = 0
)

data class ScanInfo(
    @SerializedName("result_id") val resultId: String? = null,
    @SerializedName("scanner_model") val scannerModel: String? = null,
    @SerializedName("acquire_time") val acquireTime: String? = null,
    @SerializedName("patient_intake") val patientIntake: PatientIntake? = null
)

data class PatientIntake(
    @SerializedName("age_years") val ageYears: Int? = null,
    @SerializedName("height_cm") val heightCm: Double? = null,
    @SerializedName("weight_kg") val weightKg: Double? = null
)

data class Rmr(
    @SerializedName("result_id") val resultId: String? = null,
    val estimates: List<RmrEstimate>? = null
)

data class RmrEstimate(
    val formula: String? = null,
    @SerializedName("kcal_per_day") val kcalPerDay: Double? = null
)
