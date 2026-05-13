package com.littlechef.app.ui.export

import com.littlechef.app.domain.model.UnitConversion
import com.littlechef.app.ui.screens.MealGroup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility for exporting grocery lists as plain text that can be easily shared
 * via messaging apps, email, or any text-based communication.
 */
object GroceriesTextExport {
    
    /**
     * Generates a beautifully formatted plain text grocery list.
     * 
     * @param mealGroups List of meal groups with their grocery items
     * @param translateIngredient Function to translate ingredient names
     * @param translateCategory Function to translate category names
     * @param translateUnit Function to translate unit names
     * @param getCategoryForIngredient Function to determine category for an ingredient (by translated name)
     * @param customHeader Optional custom header text (max 100 chars, replaces default header)
     * @param defaultHeader Localized default header text (e.g., "GROCERY LIST")
     * @param servingsText Function to format servings text (e.g., "4 servings")
     * @param taskFromText Localized "Task from [App Name]" text (e.g., "Task from Little Chef")
     * @param allCheckedText Localized "All items checked!" text
     * @param consolidateIngredients If true, combines duplicate ingredients across all meals into one list
     * @return Formatted plain text grocery list
     */
    fun generatePlainText(
        mealGroups: List<MealGroup>,
        translateIngredient: (String) -> String,
        translateCategory: (String) -> String,
        translateUnit: (String) -> String,
        getCategoryForIngredient: (String) -> String = { "Other" },
        customHeader: String? = null,
        defaultHeader: String = "GROCERY LIST",
        servingsText: (Int) -> String = { servings -> "$servings servings" },
        taskFromText: String = "Task from Little Chef",
        allCheckedText: String = "All items checked!",
        consolidateIngredients: Boolean = true
    ): String {
        val builder = StringBuilder()
        
        // Header - use custom header if provided, otherwise use default
        if (!customHeader.isNullOrBlank()) {
            builder.appendLine(customHeader)
            builder.appendLine("━━━━━━━━━━━━━━━━━━━━━━━")
            builder.appendLine()
        } else {
            builder.appendLine("🛒 $defaultHeader")
            builder.appendLine("━━━━━━━━━━━━━━━━━━━━━━━")
            builder.appendLine()
        }
        
        // Filter out checked items
        val uncheckedGroups = mealGroups.mapNotNull { group ->
            val uncheckedItems = group.items.filter { !it.isChecked }
            if (uncheckedItems.isEmpty()) null
            else group.copy(items = uncheckedItems)
        }
        
        if (uncheckedGroups.isEmpty()) {
            builder.appendLine("✓ $allCheckedText")
            builder.appendLine()
        } else if (consolidateIngredients) {
            // Consolidated mode: combine all ingredients across meals
            generateConsolidatedList(
                builder,
                uncheckedGroups,
                translateIngredient,
                translateCategory,
                translateUnit,
                getCategoryForIngredient
            )
        } else {
            // Original mode: show ingredients grouped by meal
            generateMealGroupedList(
                builder,
                uncheckedGroups,
                translateIngredient,
                translateCategory,
                translateUnit,
                getCategoryForIngredient,
                servingsText
            )
        }
        
        // Footer
        builder.appendLine("━━━━━━━━━━━━━━━━━━━━━━━")
        builder.appendLine("$taskFromText 👨‍🍳")
        
        return builder.toString()
    }
    
    /**
     * Generates a consolidated shopping list where duplicate ingredients are combined.
     */
    private fun generateConsolidatedList(
        builder: StringBuilder,
        mealGroups: List<MealGroup>,
        translateIngredient: (String) -> String,
        translateCategory: (String) -> String,
        translateUnit: (String) -> String,
        getCategoryForIngredient: (String) -> String
    ) {
        // Flatten all items from all meals
        val allItems = mealGroups.flatMap { it.items }
        
        // Group by translated ingredient name and consolidate quantities
        val consolidatedItems = allItems
            .groupBy { translateIngredient(it.ingredientName) }
            .mapNotNull { (translatedName, items) ->
                // Since app normalizes to g/ml, all items should have same unit
                val units = items.map { it.unit }.distinct()
                
                if (units.size == 1) {
                    // All items have the same unit - simple sum
                    val totalQuantity = items.sumOf { it.quantity }
                    val unit = items.first().unit
                    val category = getCategoryForIngredient(translatedName)
                    
                    ConsolidatedItem(
                        name = translatedName,
                        quantity = totalQuantity,
                        unit = unit,
                        category = category
                    )
                } else {
                    // Multiple units - try to convert to a common unit
                    val convertedItems = items.mapNotNull { item ->
                        val converted = UnitConversion.toStorageUnit(item.quantity, item.unit)
                        if (converted != null) {
                            val (qty, storageUnit) = converted
                            Triple(qty, storageUnit, item)
                        } else {
                            // Can't convert - keep original
                            Triple(item.quantity, item.unit, item)
                        }
                    }
                    
                    // Check if all converted to the same unit
                    val convertedUnits = convertedItems.map { it.second }.distinct()
                    if (convertedUnits.size == 1) {
                        // All converted to same unit - sum them
                        val totalQuantity = convertedItems.sumOf { it.first }
                        val unit = convertedItems.first().second
                        val category = getCategoryForIngredient(translatedName)
                        
                        ConsolidatedItem(
                            name = translatedName,
                            quantity = totalQuantity,
                            unit = unit,
                            category = category
                        )
                    } else {
                        // Can't consolidate - use first item and sum same units
                        val firstUnit = items.first().unit
                        val totalQuantity = items.filter { it.unit == firstUnit }.sumOf { it.quantity }
                        val category = getCategoryForIngredient(translatedName)
                        
                        ConsolidatedItem(
                            name = translatedName,
                            quantity = totalQuantity,
                            unit = firstUnit,
                            category = category
                        )
                    }
                }
            }
        
        // Group by category
        val itemsByCategory = consolidatedItems.groupBy { it.category }
        
        // Define category display order
        val categoryOrder = listOf(
            "Meat & Poultry", "Seafood", "Dairy & Eggs", "Vegetables",
            "Fruits", "Grains & Bread", "Canned Goods",
            "Beverages", "Snacks", "Spices & Seasonings", "Other"
        )
        
        // Sort categories by predefined order
        val sortedCategories = itemsByCategory.entries.sortedBy { (categoryName, _) ->
            val index = categoryOrder.indexOfFirst { it.equals(categoryName, ignoreCase = true) }
            if (index >= 0) index else categoryOrder.size
        }
        
        // Display each category with its consolidated items
        sortedCategories.forEachIndexed { index, (categoryName, items) ->
            builder.appendLine("${translateCategory(categoryName)}:")
            
            // Sort items alphabetically within category
            items.sortedBy { it.name }.forEach { item ->
                val formattedQuantity = formatQuantityWithUnit(
                    item.quantity,
                    item.unit,
                    translateUnit
                )
                builder.appendLine("  ☐ ${item.name} - $formattedQuantity")
            }
            
            // Add spacing between categories
            if (index < sortedCategories.size - 1) {
                builder.appendLine()
            }
        }
        
        builder.appendLine()
    }
    
    /**
     * Generates a meal-grouped list (original format).
     */
    private fun generateMealGroupedList(
        builder: StringBuilder,
        mealGroups: List<MealGroup>,
        translateIngredient: (String) -> String,
        translateCategory: (String) -> String,
        translateUnit: (String) -> String,
        getCategoryForIngredient: (String) -> String,
        servingsText: (Int) -> String
    ) {
        // Separate planned meals from category groups
        val categoryNames = listOf(
            "Meat & Poultry", "Seafood", "Dairy & Eggs", "Vegetables",
            "Fruits", "Grains & Bread", "Canned Goods", "Beverages",
            "Snacks", "Spices & Seasonings", "Other"
        )
        
        val (categoryGroups, mealGroupsList) = mealGroups.partition { group ->
            categoryNames.any { it.equals(group.mealName, ignoreCase = true) }
        }
        
        // Display meal groups first
        mealGroupsList.forEach { group ->
                // Parse and extract meal name and servings separately
                val (recipeName, servingsCount) = extractMealNameAndServings(
                    group.mealName,
                    translateCategory
                )
                
                builder.appendLine("📋 $recipeName")
                
                // Add servings on a new line if available
                if (servingsCount != null) {
                    builder.appendLine("🍽️ ${servingsText(servingsCount)}")
                }
                
                // Add planned date if available (on its own line, format: "15 Апреля")
                group.plannedDate?.let { date ->
                    val dateFormat = SimpleDateFormat("d MMMM", Locale.getDefault())
                    builder.appendLine("📅 ${dateFormat.format(Date(date))}")
                }
                
                builder.appendLine()
                
                // Group items by category for better organization
                val itemsByCategory = group.items.groupBy { item ->
                    // Translate ingredient name first
                    val translatedName = translateIngredient(item.ingredientName)
                    // Use provided category lookup function
                    getCategoryForIngredient(translatedName)
                }
                
                itemsByCategory.entries.forEachIndexed { index, (category, items) ->
                    // Only show category header if there are multiple categories
                    if (itemsByCategory.size > 1) {
                        builder.appendLine("  ${translateCategory(category)}:")
                    }
                    
                    items.forEach { item ->
                        val translatedName = translateIngredient(item.ingredientName)
                        val formattedQuantity = formatQuantityWithUnit(
                            item.quantity,
                            item.unit,
                            translateUnit
                        )
                        
                        val indent = if (itemsByCategory.size > 1) "    " else "  "
                        builder.appendLine("$indent☐ $translatedName - $formattedQuantity")
                    }
                    
                    // Add spacing between categories
                    if (index < itemsByCategory.size - 1) {
                        builder.appendLine()
                    }
                }
                
                builder.appendLine()
            }
            
            // Add separator if both meal and category groups exist
            if (mealGroups.isNotEmpty() && categoryGroups.isNotEmpty()) {
                builder.appendLine("━━━━━━━━━━━━━━━━━━━━━━━")
                builder.appendLine()
            }
            
            // Display category groups
            categoryGroups.forEach { group ->
                builder.appendLine("📦 ${translateCategory(group.mealName)}")
                builder.appendLine()
                
                group.items.forEach { item ->
                    val translatedName = translateIngredient(item.ingredientName)
                    val formattedQuantity = formatQuantityWithUnit(
                        item.quantity,
                        item.unit,
                        translateUnit
                    )
                    builder.appendLine("  ☐ $translatedName - $formattedQuantity")
                }
                
                builder.appendLine()
            }
    }
    
    /**
     * Data class for consolidated grocery items.
     */
    private data class ConsolidatedItem(
        val name: String,
        val quantity: Double,
        val unit: String,
        val category: String
    )
    
    /**
     * Generates a compact plain text list (minimal formatting).
     * Useful for quick sharing or when space is limited.
     */
    fun generateCompactText(
        mealGroups: List<MealGroup>,
        translateIngredient: (String) -> String,
        translateUnit: (String) -> String
    ): String {
        val builder = StringBuilder()
        
        builder.appendLine("🛒 Grocery List:")
        builder.appendLine()
        
        // Filter out checked items and flatten all items
        val allItems = mealGroups
            .flatMap { it.items }
            .filter { !it.isChecked }
        
        if (allItems.isEmpty()) {
            builder.appendLine("✓ All items checked!")
        } else {
            // Group by ingredient name and sum quantities
            val consolidatedItems = allItems
                .groupBy { it.ingredientName }
                .map { (name, items) ->
                    val totalQuantity = items.sumOf { it.quantity }
                    val unit = items.first().unit
                    Triple(name, totalQuantity, unit)
                }
                .sortedBy { it.first }
            
            consolidatedItems.forEach { (name, quantity, unit) ->
                val translatedName = translateIngredient(name)
                val formattedQuantity = formatQuantityWithUnit(quantity, unit, translateUnit)
                builder.appendLine("• $translatedName - $formattedQuantity")
            }
        }
        
        return builder.toString()
    }
    
    /**
     * Formats quantity with unit, handling decimal places intelligently.
     */
    private fun formatQuantityWithUnit(
        quantity: Double,
        unit: String,
        translateUnit: (String) -> String
    ): String {
        val formattedQuantity = if (quantity == quantity.toInt().toDouble()) {
            quantity.toInt().toString()
        } else {
            // Show up to 2 decimal places, removing trailing zeros
            String.format("%.2f", quantity).trimEnd('0').trimEnd('.')
        }
        
        val translatedUnit = translateUnit(unit)
        return "$formattedQuantity $translatedUnit"
    }
    
    /**
     * Extracts meal name and servings count separately.
     * Parses patterns like "Recipe Name (4 servings)" and returns the name and count.
     * @return Pair of (translated recipe name, servings count or null)
     */
    private fun extractMealNameAndServings(
        mealName: String,
        translateCategory: (String) -> String
    ): Pair<String, Int?> {
        // Pattern to match servings in various formats: (4 servings), (4 порций), (4 porții), etc.
        val servingsPattern = """\((\d+)\s+[^\)]+\)""".toRegex()
        val match = servingsPattern.find(mealName)
        
        return if (match != null) {
            val servingsCount = match.groupValues[1].toIntOrNull()
            // Extract recipe name without servings
            val recipeName = mealName.substring(0, match.range.first).trim()
            val translatedRecipeName = translateCategory(recipeName)
            translatedRecipeName to servingsCount
        } else {
            // No servings found, just translate the name
            translateCategory(mealName) to null
        }
    }
    
    /**
     * Translates meal name with servings information.
     * Parses patterns like "Recipe Name (4 servings)" and translates the servings part.
     * @deprecated Use extractMealNameAndServings instead for separate display
     */
    private fun translateMealNameWithServings(
        mealName: String,
        translateCategory: (String) -> String,
        servingsText: (Int) -> String
    ): String {
        // Pattern to match servings in various formats: (4 servings), (4 порций), (4 porții), etc.
        val servingsPattern = """\((\d+)\s+[^\)]+\)""".toRegex()
        val match = servingsPattern.find(mealName)
        
        return if (match != null) {
            val servingsCount = match.groupValues[1].toIntOrNull()
            if (servingsCount != null) {
                // Extract recipe name without servings
                val recipeName = mealName.substring(0, match.range.first).trim()
                val translatedRecipeName = translateCategory(recipeName)
                // Reconstruct with translated servings
                "$translatedRecipeName (${servingsText(servingsCount)})"
            } else {
                // If we can't parse the number, just translate the whole name
                translateCategory(mealName)
            }
        } else {
            // No servings found, just translate the name
            translateCategory(mealName)
        }
    }
}
