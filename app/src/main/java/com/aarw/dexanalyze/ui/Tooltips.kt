package com.aarw.dexanalyze.ui

internal object Tooltips {

    // ── Dashboard metric cards ────────────────────────────────────────────────

    const val TOTAL_WEIGHT =
        "Your complete body mass as measured by DEXA — fat + lean tissue + bone. " +
        "More precise than a standard scale because it breaks mass into its components."

    const val BODY_FAT_PCT =
        "Percentage of your total body weight that is fat. Includes both essential fat " +
        "(needed for hormonal and organ function) and storage fat. " +
        "Healthy ranges vary by age and sex."

    const val LEAN_MASS =
        "All non-fat mass: skeletal muscle, organs, connective tissue, and water. " +
        "Higher lean mass is strongly linked to metabolic health, insulin sensitivity, and longevity."

    const val VAT_MASS =
        "Visceral Adipose Tissue — fat stored deep inside your abdomen around organs " +
        "such as the liver, pancreas, and intestines. " +
        "Even modest reductions (0.5–1 kg) significantly lower the risk of type 2 diabetes, " +
        "cardiovascular disease, and systemic inflammation."

    const val ANDROID_FAT =
        "Fat percentage in your central abdominal region (the android zone). " +
        "Central fat is metabolically active and is the strongest fat-related predictor of " +
        "insulin resistance, hypertension, and cardiovascular risk."

    const val GYNOID_FAT =
        "Fat percentage around your hips and pelvis (the gynoid zone). " +
        "Peripheral/gynoid fat is generally less metabolically harmful than android fat " +
        "and may offer mild cardiovascular protection in some populations."

    const val AG_RATIO =
        "Android ÷ Gynoid fat ratio. " +
        "Above 1.0 = 'apple shape' (central distribution, higher cardiometabolic risk). " +
        "Below 1.0 = 'pear shape' (peripheral distribution, lower risk)."

    const val TOTAL_BMD =
        "Bone Mineral Density (g/cm²): the amount of bone mineral packed into each square " +
        "centimetre of bone area. Used clinically to diagnose osteopenia (T-score < −1.0) " +
        "and osteoporosis (T-score < −2.5)."

    const val TOTAL_BMC =
        "Bone Mineral Content — the total mass of mineral in a bone region, in grams. " +
        "A direct measure of bone mass and an indicator of structural strength."

    // ── Dashboard percentile bars ─────────────────────────────────────────────

    const val PERCENTILE_BODY_FAT =
        "Your body fat % ranked against people of the same age and sex in BodySpec's dataset. " +
        "50th percentile = average. " +
        "For most health outcomes, lower body fat percentile is better."

    const val PERCENTILE_VAT =
        "Your visceral fat level compared to age/sex peers. " +
        "Being above the 75th percentile is associated with significantly elevated " +
        "metabolic and cardiovascular risk regardless of BMI."

    const val PERCENTILE_TOTAL_LMI =
        "Total Lean Mass Index = lean mass (kg) ÷ height² (m²). " +
        "Normalises muscularity for body size. " +
        "Higher percentile means more muscle relative to your peers of the same height, age, and sex."

    const val PERCENTILE_LIMB_LMI =
        "Limb Lean Mass Index = lean mass in arms + legs (kg) ÷ height² (m²). " +
        "A key clinical screening metric for sarcopenia (age-related muscle loss). " +
        "Values below the 20th percentile warrant further evaluation."

    const val PERCENTILE_BONE_DENSITY =
        "Your whole-body bone mineral density compared to age/sex peers. " +
        "Persistent values below the 20th percentile may indicate low bone mass " +
        "and should be discussed with a physician."

    // ── Analysis section headers ──────────────────────────────────────────────

    const val BODY_MAP =
        "Heat-map of fat % across body regions. " +
        "Blue = lower fat (~10%), red = higher fat (~50%). " +
        "Darker regions carry more fat relative to their total tissue mass."

    const val REGIONAL_FAT =
        "Fat % within each body region (fat ÷ total tissue mass × 100). " +
        "Android (central belly) fat above ~30% is a stronger risk marker than the same " +
        "fat % in limb regions."

    const val SYMMETRY =
        "Comparison of lean mass and fat mass between your left and right sides. " +
        "Asymmetry > 10 % in arms or > 5 % in legs may indicate a muscle imbalance, " +
        "a previous injury, or dominant-side adaptation worth monitoring."

    const val BONE_DENSITY_SECTION =
        "Regional bone density (BMD, g/cm²) and bone mineral content (BMC, g) from DEXA. " +
        "Low BMD in any region elevates fracture risk. " +
        "T-scores and Z-scores are calculated relative to sex-matched reference populations."

    const val MASS_BREAKDOWN =
        "How your total body mass is split between fat tissue, lean tissue (muscle + organs + water), " +
        "and bone mineral. This breakdown is the core output of a DEXA scan."
}
