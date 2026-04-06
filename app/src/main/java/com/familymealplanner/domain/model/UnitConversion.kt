package com.familymealplanner.domain.model

/**
 * Unit conversion system for the app.
 * 
 * DATABASE STORAGE:
 * - Weight: Always stored in grams (g)
 * - Volume: Always stored in milliliters (ml)
 * - Pieces: Stored as-is (pcs)
 * 
 * ALL UNITS ARE DEDUCTIBLE (tracked and deducted from inventory):
 * - g, kg (weight)
 * - ml, L (volume)
 * - pcs (pieces - countable items)
 * 
 * UI DISPLAY:
 * - Weight: g, kg (auto-scaled based on quantity)
 * - Volume: ml, L (auto-scaled based on quantity)
 * - Pieces: pcs (as-is)
 */
object UnitConversion {

    // ==================== STORAGE UNITS ====================
    const val STORAGE_WEIGHT_UNIT = "g"
    const val STORAGE_VOLUME_UNIT = "ml"
    const val STORAGE_PIECE_UNIT = "pcs"

    // ==================== CONVERSION FACTORS ====================
    
    // Weight conversions (to/from grams)
    private const val GRAMS_PER_KG = 1000.0

    // Volume conversions (to/from milliliters)
    private const val ML_PER_L = 1000.0

    // ==================== UNIT NORMALIZATION FOR API/EXTERNAL SOURCES ====================
    
    /**
     * Map of API/external unit variations to app's standard units.
     * Used when importing recipes from external sources.
     * Supports English, Russian, and Romanian.
     */
    private val unitMappings = mapOf(
        // Pieces - English
        "piece" to "pcs",
        "pieces" to "pcs",
        "pc" to "pcs",
        "pcs" to "pcs",
        "count" to "pcs",
        "item" to "pcs",
        "items" to "pcs",
        
        // Pieces - Russian
        "шт" to "pcs",
        "штук" to "pcs",
        "штука" to "pcs",
        
        // Pieces - Romanian
        "buc" to "pcs",
        "bucată" to "pcs",
        "bucata" to "pcs",
        "bucăți" to "pcs",
        "bucati" to "pcs",
        
        // Weight units - English
        "gram" to "g",
        "grams" to "g",
        "gr" to "g",
        "g" to "g",
        "kilogram" to "kg",
        "kilograms" to "kg",
        "kilo" to "kg",
        "kg" to "kg",
        
        // Weight units - Romanian
        "grame" to "g",
        "grama" to "g",
        "kilograme" to "kg",
        "kilogram" to "kg",
        
        // Volume units - English
        "milliliter" to "ml",
        "milliliters" to "ml",
        "millilitre" to "ml",
        "millilitres" to "ml",
        "ml" to "ml",
        "liter" to "L",
        "liters" to "L",
        "litre" to "L",
        "litres" to "L",
        "l" to "L",
        "L" to "L",
        
        // Volume units - Romanian
        "mililitru" to "ml",
        "mililitri" to "ml",
        "litru" to "L",
        "litri" to "L",
        
        // Small measurements - English
        "teaspoon" to "ml",
        "teaspoons" to "ml",
        "tsp" to "ml",
        "tablespoon" to "ml",
        "tablespoons" to "ml",
        "tbsp" to "ml",
        "Tbsp" to "ml",
        "pinch" to "ml",
        "spray" to "ml",
        "sprays" to "ml",
        
        // Small measurements - Romanian
        "linguriță" to "ml",
        "lingurita" to "ml",
        "lingurite" to "ml",
        "lingurițe" to "ml",
        "lingură" to "ml",
        "lingura" to "ml",
        "linguri" to "ml",
        "praf" to "ml",
        "strop" to "ml",
        "stropi" to "ml",
        
        // Cup measurements (convert to ml)
        "cup" to "ml",
        "cups" to "ml",
        "cană" to "ml",
        "cana" to "ml",
        "căni" to "ml",
        "cani" to "ml",
        
        // Ounces (convert to g for weight, ml for volume)
        "oz" to "g",
        "ounce" to "g",
        "ounces" to "g",
        "fl oz" to "ml",
        "uncie" to "g",
        "uncii" to "g",
        
        // Pounds (convert to g)
        "lb" to "g",
        "lbs" to "g",
        "pound" to "g",
        "pounds" to "g",
        "livră" to "g",
        "livra" to "g",
        "livre" to "g"
    )
    
    /**
     * Conversion factors for units that need quantity adjustment.
     * Applies to English, Russian, and Romanian unit variations.
     */
    private val unitConversionFactors = mapOf(
        // Teaspoon - English
        "teaspoon" to 5.0,   // 1 tsp = 5 ml
        "teaspoons" to 5.0,
        "tsp" to 5.0,
        
        // Teaspoon - Romanian
        "linguriță" to 5.0,
        "lingurita" to 5.0,
        "lingurite" to 5.0,
        "lingurițe" to 5.0,
        
        // Tablespoon - English
        "tablespoon" to 15.0, // 1 Tbsp = 15 ml
        "tablespoons" to 15.0,
        "tbsp" to 15.0,
        "Tbsp" to 15.0,
        
        // Tablespoon - Romanian
        "lingură" to 15.0,
        "lingura" to 15.0,
        "linguri" to 15.0,
        
        // Pinch - English
        "pinch" to 0.5,      // 1 pinch ≈ 0.5 ml
        
        // Pinch - Romanian
        "praf" to 0.5,
        
        // Spray - English
        "spray" to 0.25,     // 1 spray ≈ 0.25 ml
        "sprays" to 0.25,
        
        // Spray - Romanian
        "strop" to 0.25,
        "stropi" to 0.25,
        
        // Cup - English
        "cup" to 240.0,      // 1 cup = 240 ml
        "cups" to 240.0,
        
        // Cup - Romanian
        "cană" to 240.0,
        "cana" to 240.0,
        "căni" to 240.0,
        "cani" to 240.0,
        
        // Ounces - English
        "oz" to 28.35,       // 1 oz = 28.35 g
        "ounce" to 28.35,
        "ounces" to 28.35,
        "fl oz" to 29.57,    // 1 fl oz = 29.57 ml
        
        // Ounces - Romanian
        "uncie" to 28.35,
        "uncii" to 28.35,
        
        // Pounds - English
        "lb" to 453.59,      // 1 lb = 453.59 g
        "lbs" to 453.59,
        "pound" to 453.59,
        "pounds" to 453.59,
        
        // Pounds - Romanian
        "livră" to 453.59,
        "livra" to 453.59,
        "livre" to 453.59
    )
    
    /**
     * Normalize an external unit to app's standard unit format.
     * Handles various unit formats from APIs, user input, and different languages.
     * 
     * @param unit The unit from external source (API, user input, etc.)
     * @return Pair of (normalized unit, conversion factor) or null if unrecognized
     */
    fun normalizeUnit(unit: String): Pair<String, Double>? {
        val trimmedUnit = unit.trim()
        val normalizedUnit = trimmedUnit.lowercase()
        
        // Try lowercase mapping first, then original case
        val mappedUnit = unitMappings[normalizedUnit] ?: unitMappings[trimmedUnit]
        if (mappedUnit != null) {
            val conversionFactor = unitConversionFactors[normalizedUnit] ?: 1.0
            return mappedUnit to conversionFactor
        }
        
        return null
    }

    /**
     * Check if a unit is a weight unit.
     */
    fun isWeightUnit(unit: String): Boolean {
        return unit in setOf("g", "kg")
    }

    /**
     * Check if a unit is a volume unit.
     */
    fun isVolumeUnit(unit: String): Boolean {
        return unit in setOf("ml", "L")
    }

    /**
     * Check if a unit is a piece unit.
     */
    fun isPieceUnit(unit: String): Boolean {
        return unit == "pcs"
    }

    // ==================== CONVERSION TO STORAGE ====================

    /**
     * Convert any weight unit to grams for storage.
     * @return quantity in grams, or null if not a weight unit
     */
    fun toGrams(quantity: Double, fromUnit: String): Double? {
        return when (fromUnit) {
            "g" -> quantity
            "kg" -> quantity * GRAMS_PER_KG
            else -> null
        }
    }

    /**
     * Convert any volume unit to milliliters for storage.
     * @return quantity in milliliters, or null if not a volume unit
     */
    fun toMilliliters(quantity: Double, fromUnit: String): Double? {
        return when (fromUnit) {
            "ml" -> quantity
            "L" -> quantity * ML_PER_L
            else -> null
        }
    }

    /**
     * Convert to storage unit (g, ml, or pcs) based on the input unit type.
     * First normalizes the unit if it's from an external source, then converts to storage format.
     * @return Pair of (converted quantity, storage unit) or null if non-convertible
     */
    fun toStorageUnit(quantity: Double, fromUnit: String): Pair<Double, String>? {
        // Try to normalize the unit first (handles API units like "шт", "spray", etc.)
        val (normalizedUnit, conversionFactor) = normalizeUnit(fromUnit) ?: (fromUnit to 1.0)
        val adjustedQuantity = quantity * conversionFactor

        // Pieces stay as-is (no conversion needed)
        if (isPieceUnit(normalizedUnit)) {
            return adjustedQuantity to STORAGE_PIECE_UNIT
        }

        // Weight units -> grams
        toGrams(adjustedQuantity, normalizedUnit)?.let {
            return it to STORAGE_WEIGHT_UNIT
        }

        // Volume units -> milliliters
        toMilliliters(adjustedQuantity, normalizedUnit)?.let {
            return it to STORAGE_VOLUME_UNIT
        }

        return null
    }

    // ==================== CONVERSION FROM STORAGE ====================

    /**
     * Convert grams to the specified weight unit.
     */
    fun fromGrams(grams: Double, toUnit: String): Double? {
        return when (toUnit) {
            "g" -> grams
            "kg" -> grams / GRAMS_PER_KG
            else -> null
        }
    }

    /**
     * Convert milliliters to the specified volume unit.
     */
    fun fromMilliliters(ml: Double, toUnit: String): Double? {
        return when (toUnit) {
            "ml" -> ml
            "L" -> ml / ML_PER_L
            else -> null
        }
    }

    // ==================== DISPLAY CONVERSION ====================

    /**
     * Convert a stored value to the best display unit.
     * Automatically chooses the most readable unit (e.g., kg instead of 1500g).
     * 
     * @param quantity The stored quantity (in g, ml, or pcs)
     * @param storageUnit The storage unit ("g", "ml", or "pcs")
     * @return Pair of (display quantity, display unit)
     */
    fun toDisplayUnit(quantity: Double, storageUnit: String): Pair<Double, String> {
        // Pieces stay as-is
        if (isPieceUnit(storageUnit)) {
            return quantity to storageUnit
        }

        return when (storageUnit) {
            "g" -> toDisplayWeight(quantity)
            "ml" -> toDisplayVolume(quantity)
            else -> quantity to storageUnit
        }
    }

    /**
     * Convert grams to the best display weight unit.
     */
    private fun toDisplayWeight(grams: Double): Pair<Double, String> {
        return if (grams >= 1000) {
            (grams / GRAMS_PER_KG) to "kg"
        } else {
            grams to "g"
        }
    }

    /**
     * Convert milliliters to the best display volume unit.
     */
    private fun toDisplayVolume(ml: Double): Pair<Double, String> {
        return if (ml >= 1000) {
            (ml / ML_PER_L) to "L"
        } else {
            ml to "ml"
        }
    }

    // ==================== FORMATTING ====================

    /**
     * Format a quantity for display (removes unnecessary decimals).
     * Uses floor rounding to avoid displaying quantities higher than actual.
     */
    fun formatQuantity(quantity: Double): String {
        // Round to avoid floating-point precision issues
        val rounded = kotlin.math.round(quantity * 1000) / 1000.0
        
        return if (rounded == rounded.toLong().toDouble()) {
            rounded.toLong().toString()
        } else {
            // Always show 1 decimal place with floor rounding
            val floored = kotlin.math.floor(rounded * 10) / 10
            String.format("%.1f", floored)
        }
    }

    /**
     * Format quantity with unit for display.
     */
    fun formatWithUnit(quantity: Double, unit: String): String {
        return "${formatQuantity(quantity)} $unit"
    }

    /**
     * Format a stored value for display.
     */
    fun formatForDisplay(quantity: Double, storageUnit: String): String {
        val (displayQty, displayUnit) = toDisplayUnit(quantity, storageUnit)
        return formatWithUnit(displayQty, displayUnit)
    }

    // ==================== UNIT LISTS FOR UI ====================

    /**
     * Get weight units for input/editing.
     */
    fun getWeightUnits(): List<String> = listOf("g", "kg")

    /**
     * Get volume units for input/editing.
     */
    fun getVolumeUnits(): List<String> = listOf("ml", "L")

    /**
     * Get all supported units.
     * All units are deductible and tracked in inventory.
     */
    fun getAllUnits(): List<String> = getWeightUnits() + getVolumeUnits() + listOf("pcs")
}
