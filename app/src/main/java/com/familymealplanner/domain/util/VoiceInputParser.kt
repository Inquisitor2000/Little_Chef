package com.familymealplanner.domain.util

/**
 * Represents a parsed ingredient name from voice input.
 * New simplified format: just ingredient names, one per line
 */
data class ParsedIngredient(
    val rawText: String,
    val name: String
)

/**
 * Parser for voice input text that extracts ingredient names.
 * New simplified approach: extracts ingredient names only (no quantities/units).
 * 
 * Supports:
 * - Multiple ingredients separated by newlines or common delimiters
 * - Cleans up extra whitespace and punctuation
 */
object VoiceInputParser {
    
    /**
     * Common delimiters that separate ingredients in speech.
     * These are used as fallback if no newlines are present.
     */
    private val delimiters: List<String> = listOf(
        "\n",           // Newline (primary separator)
        " and ",        // "milk and eggs"
        ", ",           // "milk, eggs, bread"
        " then ",       // "milk then eggs"
        " also ",       // "milk also eggs"
        " plus "        // "milk plus eggs"
    )

    /**
     * Parse transcribed text into a list of ingredient names.
     * Splits by newlines or delimiters and cleans each entry.
     * 
     * @param transcribedText The raw text from voice recognition
     * @return List of parsed ingredient names
     */
    fun parse(transcribedText: String): List<ParsedIngredient> {
        if (transcribedText.isBlank()) {
            return emptyList()
        }
        
        val normalizedText = transcribedText.trim()
        val entries = splitByDelimiters(normalizedText)
        
        return entries
            .map { cleanIngredientName(it) }
            .filter { it.isNotBlank() }
            .map { name ->
                ParsedIngredient(
                    rawText = name,
                    name = name
                )
            }
    }
    
    /**
     * Split text by any of the defined delimiters.
     * Prioritizes newlines, then falls back to other delimiters.
     */
    private fun splitByDelimiters(text: String): List<String> {
        // First try splitting by newlines
        if (text.contains("\n")) {
            return text.split("\n")
        }
        
        // Otherwise, split by other delimiters
        var result = listOf(text)
        
        for (delimiter in delimiters.drop(1)) { // Skip newline since we already checked
            result = result.flatMap { segment ->
                segment.split(delimiter, ignoreCase = true)
            }
        }
        
        return result
    }
    
    /**
     * Clean an ingredient name by removing extra whitespace and punctuation.
     * 
     * @param text The raw ingredient name text
     * @return Cleaned ingredient name
     */
    private fun cleanIngredientName(text: String): String {
        return text.trim()
            .replace(Regex("[,;.!?]+$"), "") // Remove trailing punctuation
            .replace(Regex("\\s+"), " ")      // Normalize whitespace
            .trim()
    }
}
