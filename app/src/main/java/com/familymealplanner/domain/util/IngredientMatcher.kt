package com.familymealplanner.domain.util

import com.familymealplanner.data.local.TranslationSystem
import com.familymealplanner.domain.model.CatalogIngredient
import com.familymealplanner.domain.model.IngredientCatalog
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fuzzy matches parsed ingredient names to catalog ingredients using Levenshtein distance.
 * 
 * This class provides fuzzy matching capabilities to handle typos and variations in
 * ingredient names (e.g., "tomatoe" → "tomato", "chiken" → "chicken").
 * 
 * Supports multi-language matching by translating catalog ingredients to the current
 * language before performing fuzzy matching.
 */
@Singleton
class IngredientMatcher @Inject constructor(
    private val translationSystem: TranslationSystem
) {
    
    /**
     * Result of a fuzzy match operation.
     * 
     * @param catalogIngredient The matched catalog ingredient
     * @param confidence Similarity score from 0.0 to 1.0 (1.0 = exact match)
     */
    data class MatchResult(
        val catalogIngredient: CatalogIngredient,
        val confidence: Double
    )
    
    /**
     * Cached catalog ingredient names for performance.
     * Initialized lazily on first use.
     * Maps English ingredient names to their catalog entries.
     */
    private val catalogNames: List<Pair<String, CatalogIngredient>> by lazy {
        IngredientCatalog.allIngredients.map { 
            it.nameKey.lowercase() to it 
        }
    }
    
    /**
     * Finds the best matching catalog ingredient for the given name.
     * 
     * Uses Levenshtein distance to calculate similarity and returns the best match
     * if it meets the confidence threshold.
     * 
     * Matches are performed ONLY in the current language. The query is compared against
     * translated ingredient names in the current language, with no fallback to English.
     * 
     * Smart matching: Handles common prefixes/suffixes like "Fresh", "Ground", "Dried",
     * "Chopped", etc. by trying both the original name and the stripped version.
     * Works in all languages by translating the modifiers.
     * 
     * Performance optimization (NFR-1.3): Uses cached catalog names and limits
     * search to top matches for efficiency.
     * 
     * @param name The ingredient name to match (case-insensitive)
     * @param threshold Minimum confidence score required (default 0.7)
     * @return MatchResult if a match is found, null otherwise
     */
    fun findMatch(name: String, threshold: Double = 0.7): MatchResult? {
        val normalizedName = name.lowercase().trim()
        
        if (normalizedName.isEmpty()) {
            return null
        }
        
        // Common modifiers in English (will be translated to current language)
        val commonModifiersEnglish = listOf(
            "fresh", "dried", "ground", "chopped", "minced", "sliced", 
            "diced", "crushed", "whole", "raw", "cooked", "frozen",
            "canned", "shredded", "grated", "powdered"
        )
        
        // Translate modifiers to current language
        val commonModifiers = commonModifiersEnglish.map { 
            translationSystem.translateIngredient(it).lowercase()
        }
        
        // Try matching with the original name first
        val directMatches = findMatchesForName(normalizedName, threshold)
        
        // If no good match found, try stripping common modifiers
        if (directMatches.isEmpty() || (directMatches.maxByOrNull { it.confidence }?.confidence ?: 0.0) < 0.85) {
            // Try removing common prefixes/suffixes
            for (modifier in commonModifiers) {
                val strippedName = normalizedName
                    .removePrefix("$modifier ")
                    .removeSuffix(" $modifier")
                    .trim()
                
                if (strippedName != normalizedName && strippedName.isNotEmpty()) {
                    val strippedMatches = findMatchesForName(strippedName, threshold)
                    val bestStripped = strippedMatches.maxByOrNull { it.confidence }
                    val bestDirect = directMatches.maxByOrNull { it.confidence }
                    
                    // Use stripped version if it's significantly better
                    if (bestStripped != null && (bestDirect == null || bestStripped.confidence > bestDirect.confidence)) {
                        return bestStripped
                    }
                }
            }
        }
        
        return directMatches.maxByOrNull { it.confidence }
    }
    
    /**
     * Helper function to find matches for a specific name.
     * Translates catalog names to current language and calculates similarity.
     */
    private fun findMatchesForName(normalizedName: String, threshold: Double): List<MatchResult> {
        return catalogNames.map { (englishName, ingredient) ->
            val translatedName = translationSystem.translateIngredient(englishName).lowercase()
            val similarity = calculateSimilarity(normalizedName, translatedName)
            MatchResult(ingredient, similarity)
        }.filter { it.confidence >= threshold }
    }
    
    /**
     * Finds the top N best matching catalog ingredients for the given name.
     * 
     * Matches are performed ONLY in the current language. The query is compared against
     * translated ingredient names in the current language, with no fallback to English.
     * 
     * Performance optimization (NFR-1.3): Limits results to top 3 matches by default
     * to improve performance for fuzzy search operations.
     * 
     * @param name The ingredient name to match (case-insensitive)
     * @param limit Maximum number of matches to return (default 3)
     * @return List of MatchResult sorted by confidence (highest first)
     */
    fun findBestMatches(name: String, limit: Int = 3): List<MatchResult> {
        val normalizedName = name.lowercase().trim()
        
        if (normalizedName.isEmpty()) {
            return emptyList()
        }
        
        // Translate all catalog names to current language and match against them
        // No fallback to English - search only in current language
        return catalogNames
            .map { (englishName, ingredient) ->
                val translatedName = translationSystem.translateIngredient(englishName).lowercase()
                val similarity = calculateSimilarity(normalizedName, translatedName)
                MatchResult(ingredient, similarity)
            }
            .sortedByDescending { it.confidence }
            .take(limit)
    }
    
    /**
     * Calculates similarity score between two strings using Levenshtein distance.
     * 
     * The similarity score is calculated as: 1.0 - (distance / maxLength)
     * where maxLength is the length of the longer string.
     * 
     * @param s1 First string (normalized)
     * @param s2 Second string (normalized)
     * @return Similarity score from 0.0 to 1.0 (1.0 = exact match)
     */
    private fun calculateSimilarity(s1: String, s2: String): Double {
        val distance = levenshteinDistance(s1, s2)
        val maxLength = maxOf(s1.length, s2.length)
        return if (maxLength == 0) 1.0 else 1.0 - (distance.toDouble() / maxLength)
    }
    
    /**
     * Calculates the Levenshtein distance between two strings.
     * 
     * The Levenshtein distance is the minimum number of single-character edits
     * (insertions, deletions, or substitutions) required to change one string
     * into the other.
     * 
     * Uses dynamic programming with a 2D array for efficiency.
     * 
     * @param s1 First string
     * @param s2 Second string
     * @return The Levenshtein distance (number of edits)
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        
        // Initialize first row and column
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        
        // Fill the DP table
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
            }
        }
        
        return dp[s1.length][s2.length]
    }
}
