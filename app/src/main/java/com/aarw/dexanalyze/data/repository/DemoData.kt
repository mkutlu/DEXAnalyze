package com.aarw.dexanalyze.data.repository

import com.aarw.dexanalyze.data.model.*
import com.aarw.dexanalyze.data.model.Location

object DemoData {
    val scans: List<ScanResult> = listOf(
        ScanResult(
            resultId = "77402c920a07400a8d8ebeea5bdcd5ed",
            scanDate = "2026-04-14",
            location = Location(name = "Preston Ridge"),
            composition = Composition(
                total = RegionComposition(fatMassKg = 23.5, leanMassKg = 56.3, boneMassKg = 2.9, totalMassKg = 82.7, tissueFatPct = 29.5, regionFatPct = 28.4),
                regions = mapOf(
                    "android"   to RegionComposition(fatMassKg = 2.0,  leanMassKg = 3.7,  boneMassKg = 0.0, totalMassKg = 5.8,  tissueFatPct = 34.4, regionFatPct = 34.2),
                    "gynoid"    to RegionComposition(fatMassKg = 3.6,  leanMassKg = 9.3,  boneMassKg = 0.3, totalMassKg = 13.3, tissueFatPct = 28.1, regionFatPct = 27.4),
                    "trunk"     to RegionComposition(fatMassKg = 12.1, leanMassKg = 25.5, boneMassKg = 0.8, totalMassKg = 38.4, tissueFatPct = 32.3, regionFatPct = 31.6),
                    "arms"      to RegionComposition(fatMassKg = 2.5,  leanMassKg = 6.3,  boneMassKg = 0.3, totalMassKg = 9.1,  tissueFatPct = 28.0, regionFatPct = 27.0),
                    "legs"      to RegionComposition(fatMassKg = 7.9,  leanMassKg = 20.9, boneMassKg = 1.2, totalMassKg = 30.0, tissueFatPct = 27.4, regionFatPct = 26.3),
                    "limbs"     to RegionComposition(fatMassKg = 10.3, leanMassKg = 27.2, boneMassKg = 1.5, totalMassKg = 39.1, tissueFatPct = 27.5, regionFatPct = 26.4),
                    "left_arm"  to RegionComposition(fatMassKg = 1.1,  leanMassKg = 3.0,  boneMassKg = 0.2, totalMassKg = 4.3,  tissueFatPct = 27.1, regionFatPct = 26.1),
                    "right_arm" to RegionComposition(fatMassKg = 1.3,  leanMassKg = 3.3,  boneMassKg = 0.2, totalMassKg = 4.8,  tissueFatPct = 28.9, regionFatPct = 27.8),
                    "left_leg"  to RegionComposition(fatMassKg = 3.9,  leanMassKg = 10.3, boneMassKg = 0.6, totalMassKg = 14.8, tissueFatPct = 27.2, regionFatPct = 26.1),
                    "right_leg" to RegionComposition(fatMassKg = 4.0,  leanMassKg = 10.5, boneMassKg = 0.6, totalMassKg = 15.2, tissueFatPct = 27.5, regionFatPct = 26.4)
                ),
                androidGynoidRatio = 34.2 / 27.4
            ),
            boneDensity = BoneDensity(
                total = BoneRegion(bmdGCm2 = 1.323, areaCm2 = 2175.61, bmcG = 2877.42),
                regions = mapOf(
                    "left_arm"  to BoneRegion(bmdGCm2 = 0.970, areaCm2 = 162.47, bmcG = 157.54),
                    "left_leg"  to BoneRegion(bmdGCm2 = 1.485, areaCm2 = 402.59, bmcG = 597.95),
                    "right_arm" to BoneRegion(bmdGCm2 = 1.067, areaCm2 = 166.45, bmcG = 177.52),
                    "right_leg" to BoneRegion(bmdGCm2 = 1.484, areaCm2 = 409.03, bmcG = 606.94),
                    "trunk"     to BoneRegion(bmdGCm2 = 1.016, areaCm2 = 790.93, bmcG = 803.75)
                )
            ),
            visceralFat = VisceralFat(vatMassKg = 0.8, vatVolumeCm3 = 863.58),
            percentiles = Percentiles(
                params = PercentileParams("male", AgeRange(30, 34), 391000),
                metrics = PercentileMetrics(
                    totalBodyFatPct = PercentileValue(28.4, 74),
                    vatMassKg = PercentileValue(0.81, 75),
                    totalLmiKgM2 = PercentileValue(17.31, 23),
                    limbLmiKgM2 = PercentileValue(8.36, 22),
                    boneDensityGCm2 = PercentileValue(1.323, 43)
                )
            )
        ),
        ScanResult(
            resultId = "07d49de765774723a93b7acc0bf339ff",
            scanDate = "2026-03-10",
            location = Location(name = "Preston Ridge"),
            composition = Composition(
                total = RegionComposition(fatMassKg = 28.8, leanMassKg = 59.5, boneMassKg = 2.8, totalMassKg = 91.1, tissueFatPct = 32.6, regionFatPct = 31.5),
                regions = mapOf(
                    "android"   to RegionComposition(fatMassKg = 2.6,  leanMassKg = 4.1,  boneMassKg = 0.0, totalMassKg = 6.8,  tissueFatPct = 39.1, regionFatPct = 38.8),
                    "gynoid"    to RegionComposition(fatMassKg = 4.8,  leanMassKg = 10.1, boneMassKg = 0.3, totalMassKg = 15.1, tissueFatPct = 32.1, regionFatPct = 31.5),
                    "trunk"     to RegionComposition(fatMassKg = 15.3, leanMassKg = 27.1, boneMassKg = 0.8, totalMassKg = 43.2, tissueFatPct = 36.0, regionFatPct = 35.3),
                    "arms"      to RegionComposition(fatMassKg = 3.3,  leanMassKg = 7.5,  boneMassKg = 0.4, totalMassKg = 11.1, tissueFatPct = 30.6, regionFatPct = 29.6),
                    "legs"      to RegionComposition(fatMassKg = 9.2,  leanMassKg = 21.6, boneMassKg = 1.2, totalMassKg = 32.0, tissueFatPct = 29.9, regionFatPct = 28.8),
                    "limbs"     to RegionComposition(fatMassKg = 12.5, leanMassKg = 29.1, boneMassKg = 1.6, totalMassKg = 43.2, tissueFatPct = 30.1, regionFatPct = 29.0),
                    "left_arm"  to RegionComposition(fatMassKg = 1.6,  leanMassKg = 3.7,  boneMassKg = 0.2, totalMassKg = 5.6,  tissueFatPct = 30.6, regionFatPct = 29.6),
                    "right_arm" to RegionComposition(fatMassKg = 1.6,  leanMassKg = 3.7,  boneMassKg = 0.2, totalMassKg = 5.6,  tissueFatPct = 30.6, regionFatPct = 29.6),
                    "left_leg"  to RegionComposition(fatMassKg = 4.7,  leanMassKg = 10.1, boneMassKg = 0.6, totalMassKg = 15.4, tissueFatPct = 31.5, regionFatPct = 30.3),
                    "right_leg" to RegionComposition(fatMassKg = 4.6,  leanMassKg = 11.5, boneMassKg = 0.6, totalMassKg = 16.6, tissueFatPct = 28.4, regionFatPct = 27.4)
                ),
                androidGynoidRatio = 38.8 / 31.5
            ),
            boneDensity = BoneDensity(
                total = BoneRegion(bmdGCm2 = 1.287, areaCm2 = 2210.88, bmcG = 2846.25),
                regions = mapOf(
                    "left_arm"  to BoneRegion(bmdGCm2 = 1.049, areaCm2 = 177.7,  bmcG = 186.38),
                    "left_leg"  to BoneRegion(bmdGCm2 = 1.401, areaCm2 = 428.85, bmcG = 600.84),
                    "right_arm" to BoneRegion(bmdGCm2 = 1.049, areaCm2 = 177.7,  bmcG = 186.38),
                    "right_leg" to BoneRegion(bmdGCm2 = 1.394, areaCm2 = 418.19, bmcG = 583.05),
                    "trunk"     to BoneRegion(bmdGCm2 = 1.018, areaCm2 = 797.5,  bmcG = 812.13)
                )
            ),
            visceralFat = VisceralFat(vatMassKg = 1.1, vatVolumeCm3 = 1199.03),
            percentiles = Percentiles(
                params = PercentileParams("male", AgeRange(30, 34), 391000),
                metrics = PercentileMetrics(
                    totalBodyFatPct = PercentileValue(31.5, 85),
                    vatMassKg = PercentileValue(1.13, 86),
                    totalLmiKgM2 = PercentileValue(18.31, 40),
                    limbLmiKgM2 = PercentileValue(8.94, 40),
                    boneDensityGCm2 = PercentileValue(1.287, 32)
                )
            )
        ),
        ScanResult(
            resultId = "f287cb7294144436a2ab35ef20536a9c",
            scanDate = "2026-02-10",
            location = Location(name = "Preston Ridge"),
            composition = Composition(
                total = RegionComposition(fatMassKg = 33.1, leanMassKg = 61.0, boneMassKg = 2.9, totalMassKg = 97.0, tissueFatPct = 35.2, regionFatPct = 34.1),
                regions = mapOf(
                    "android"   to RegionComposition(fatMassKg = 3.3,  leanMassKg = 4.4,  boneMassKg = 0.1, totalMassKg = 7.8,  tissueFatPct = 42.7, regionFatPct = 42.4),
                    "gynoid"    to RegionComposition(fatMassKg = 5.7,  leanMassKg = 10.3, boneMassKg = 0.3, totalMassKg = 16.3, tissueFatPct = 35.8, regionFatPct = 35.2),
                    "trunk"     to RegionComposition(fatMassKg = 18.2, leanMassKg = 28.4, boneMassKg = 0.8, totalMassKg = 47.5, tissueFatPct = 39.1, regionFatPct = 38.4),
                    "arms"      to RegionComposition(fatMassKg = 3.6,  leanMassKg = 7.1,  boneMassKg = 0.3, totalMassKg = 11.0, tissueFatPct = 33.7, regionFatPct = 32.7),
                    "legs"      to RegionComposition(fatMassKg = 10.3, leanMassKg = 22.2, boneMassKg = 1.2, totalMassKg = 33.6, tissueFatPct = 31.6, regionFatPct = 30.5),
                    "limbs"     to RegionComposition(fatMassKg = 13.9, leanMassKg = 29.3, boneMassKg = 1.5, totalMassKg = 44.7, tissueFatPct = 32.1, regionFatPct = 31.0),
                    "left_arm"  to RegionComposition(fatMassKg = 1.7,  leanMassKg = 3.3,  boneMassKg = 0.2, totalMassKg = 5.2,  tissueFatPct = 33.6, regionFatPct = 32.6),
                    "right_arm" to RegionComposition(fatMassKg = 1.9,  leanMassKg = 3.8,  boneMassKg = 0.2, totalMassKg = 5.9,  tissueFatPct = 33.8, regionFatPct = 32.8),
                    "left_leg"  to RegionComposition(fatMassKg = 5.2,  leanMassKg = 10.8, boneMassKg = 0.6, totalMassKg = 16.6, tissueFatPct = 32.5, regionFatPct = 31.4),
                    "right_leg" to RegionComposition(fatMassKg = 5.0,  leanMassKg = 11.4, boneMassKg = 0.6, totalMassKg = 17.0, tissueFatPct = 30.7, regionFatPct = 29.7)
                ),
                androidGynoidRatio = 42.4 / 35.2
            ),
            boneDensity = BoneDensity(
                total = BoneRegion(bmdGCm2 = 1.306, areaCm2 = 2184.71, bmcG = 2852.69),
                regions = mapOf(
                    "left_arm"  to BoneRegion(bmdGCm2 = 0.957, areaCm2 = 172.78, bmcG = 165.31),
                    "left_leg"  to BoneRegion(bmdGCm2 = 1.403, areaCm2 = 412.4,  bmcG = 578.45),
                    "right_arm" to BoneRegion(bmdGCm2 = 1.049, areaCm2 = 174.98, bmcG = 183.54),
                    "right_leg" to BoneRegion(bmdGCm2 = 1.434, areaCm2 = 406.67, bmcG = 583.32),
                    "trunk"     to BoneRegion(bmdGCm2 = 1.047, areaCm2 = 797.8,  bmcG = 835.52)
                )
            ),
            visceralFat = VisceralFat(vatMassKg = 1.4, vatVolumeCm3 = 1517.81),
            percentiles = Percentiles(
                params = PercentileParams("male", AgeRange(29, 33), 395000),
                metrics = PercentileMetrics(
                    totalBodyFatPct = PercentileValue(34.1, 92),
                    vatMassKg = PercentileValue(1.43, 93),
                    totalLmiKgM2 = PercentileValue(18.77, 49),
                    limbLmiKgM2 = PercentileValue(9.01, 43),
                    boneDensityGCm2 = PercentileValue(1.306, 38)
                )
            )
        )
    )
}
