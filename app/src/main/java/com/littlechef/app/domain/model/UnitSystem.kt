package com.littlechef.app.domain.model

/**
 * Measurement type for ingredients.
 */
enum class MeasurementType {
    WEIGHT,     // g/kg - stored as g
    VOLUME,     // ml/L - stored as ml
    PIECE       // pcs - stored as pcs
}

/**
 * Unit options for the app (metric only).
 * 
 * STORAGE: Database always stores weight in grams (g) and volume in milliliters (ml).
 * 
 * DISPLAY: g, kg, ml, L (auto-scaled based on quantity)
 * 
 * ALL UNITS ARE DEDUCTIBLE: g, kg, ml, L, pcs
 */
object UnitOptions {
    private val allUnits = listOf(
        "g" to "grams",
        "kg" to "kilograms",
        "ml" to "milliliters",
        "L" to "liters",
        "pcs" to "pieces"
    )

    // Unit groups by measurement type
    val weightUnits = listOf("g", "kg")
    val volumeUnits = listOf("ml", "L")
    val pieceUnits = listOf("pcs")

    fun getUnits(): List<Pair<String, String>> = allUnits

    /**
     * Get the measurement type for a given unit.
     */
    fun getMeasurementType(unit: String): MeasurementType {
        return when (unit) {
            in weightUnits -> MeasurementType.WEIGHT
            in volumeUnits -> MeasurementType.VOLUME
            else -> MeasurementType.PIECE
        }
    }

    /**
     * Get allowed units for a measurement type.
     */
    fun getAllowedUnits(measurementType: MeasurementType): List<String> {
        return when (measurementType) {
            MeasurementType.WEIGHT -> weightUnits
            MeasurementType.VOLUME -> volumeUnits
            MeasurementType.PIECE -> pieceUnits
        }
    }

    /**
     * Get allowed units for an ingredient based on its default unit.
     */
    fun getAllowedUnitsForIngredient(defaultUnit: String): List<String> {
        val measurementType = getMeasurementType(defaultUnit)
        return getAllowedUnits(measurementType)
    }
}
