package com.littlechef.app.ui.util

/**
 * Shared time adjustment functions for calculating prep/cook time based on
 * serving size scaling. All preview cards and detail screens use the same
 * formula so times are consistent everywhere.
 *
 * Prep scales ~35% per serving doubling; cook scales ~5% (method-dependent).
 */
object TimeAdjuster {

    fun adjustPrepTime(
        baseMinutes: Int?,
        baseServings: Int?,
        selectedServings: Int
    ): Int {
        if (baseMinutes == null || baseMinutes <= 0 || baseServings == null || baseServings <= 0)
            return 0
        val ratio = selectedServings.toDouble() / baseServings.toDouble()
        return (baseMinutes * (ratio - 1.0) * 0.35).toInt().coerceAtLeast(0)
    }

    fun adjustCookTime(
        baseMinutes: Int?,
        baseServings: Int?,
        selectedServings: Int
    ): Int {
        if (baseMinutes == null || baseMinutes <= 0 || baseServings == null || baseServings <= 0)
            return 0
        val ratio = selectedServings.toDouble() / baseServings.toDouble()
        return (baseMinutes * (ratio - 1.0) * 0.05).toInt().coerceAtLeast(0)
    }
}
