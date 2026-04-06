package com.familymealplanner.domain.usecase

import com.familymealplanner.domain.model.GroceryItem
import com.familymealplanner.domain.model.IngredientCatalog
import com.familymealplanner.domain.repository.IngredientRepository
import javax.inject.Inject

class AddGroceryItemToPantryUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository,
    private val restockIngredientUseCase: RestockIngredientUseCase,
    private val createIngredientUseCase: CreateIngredientUseCase
) {
    suspend operator fun invoke(
        groceryItem: GroceryItem
    ): Result {
        android.util.Log.d("AddGroceryItemToPantry", "=== Starting to add grocery item to pantry ===")
        android.util.Log.d("AddGroceryItemToPantry", "Grocery Item: ${groceryItem.ingredientName}")
        android.util.Log.d("AddGroceryItemToPantry", "Quantity: ${groceryItem.quantity} ${groceryItem.unit}")
        android.util.Log.d("AddGroceryItemToPantry", "Meal: ${groceryItem.mealName}")
        android.util.Log.d("AddGroceryItemToPantry", "Planned Date: ${groceryItem.plannedDate}")
        
        // Try to find existing ingredient
        var ingredient = ingredientRepository.getIngredientByName(groceryItem.ingredientName)
        
        // Always look up the ingredient in catalog to verify/correct category
        val catalogIngredient = IngredientCatalog.allIngredients.find { 
            it.nameKey.equals(groceryItem.ingredientName, ignoreCase = true) 
        }
        
        if (catalogIngredient != null) {
            android.util.Log.d("AddGroceryItemToPantry", "Found in catalog: ${catalogIngredient.nameKey}")
            android.util.Log.d("AddGroceryItemToPantry", "Catalog category: ${catalogIngredient.category.displayName}")
            android.util.Log.d("AddGroceryItemToPantry", "Catalog subcategory: ${catalogIngredient.subcategory}")
            android.util.Log.d("AddGroceryItemToPantry", "Catalog default unit: ${catalogIngredient.defaultUnit}")
        } else {
            android.util.Log.w("AddGroceryItemToPantry", "Ingredient not found in catalog: ${groceryItem.ingredientName}")
        }
        
        if (ingredient != null) {
            android.util.Log.d("AddGroceryItemToPantry", "Found existing ingredient: ${ingredient.name} (ID: ${ingredient.id})")
            android.util.Log.d("AddGroceryItemToPantry", "Existing ingredient unit: ${ingredient.unit}, category: ${ingredient.category}")
            
            // Check if the existing ingredient needs correction based on catalog
            if (catalogIngredient != null) {
                val needsUpdate = ingredient.unit != catalogIngredient.defaultUnit || 
                                 ingredient.name != catalogIngredient.nameKey ||
                                 (ingredient.category == "Other" && catalogIngredient.category.displayName != "Other")
                
                if (needsUpdate) {
                    android.util.Log.w("AddGroceryItemToPantry", "Existing ingredient needs correction!")
                    android.util.Log.w("AddGroceryItemToPantry", "  Current name: '${ingredient.name}' -> Expected: '${catalogIngredient.nameKey}'")
                    android.util.Log.w("AddGroceryItemToPantry", "  Current unit: '${ingredient.unit}' -> Expected: '${catalogIngredient.defaultUnit}'")
                    android.util.Log.w("AddGroceryItemToPantry", "  Current category: '${ingredient.category}' -> Expected: '${catalogIngredient.category.displayName}'")
                    android.util.Log.w("AddGroceryItemToPantry", "  Updating ingredient to match catalog...")
                    
                    // Update the ingredient with the correct values from catalog
                    val updatedIngredient = ingredient.copy(
                        name = catalogIngredient.nameKey, // Fix capitalization
                        unit = catalogIngredient.defaultUnit, // Fix unit
                        category = catalogIngredient.category.displayName, // Fix category
                        subcategory = catalogIngredient.subcategory // Fix subcategory
                    )
                    ingredientRepository.updateIngredient(updatedIngredient, ingredient.allergens.map { it.name })
                    ingredient = updatedIngredient
                    
                    android.util.Log.d("AddGroceryItemToPantry", "Ingredient updated successfully")
                }
            }
        } else {
            android.util.Log.d("AddGroceryItemToPantry", "Ingredient not found in pantry, creating new ingredient...")
        }
        
        // If ingredient doesn't exist, create it
        if (ingredient == null) {
            val category = catalogIngredient?.category?.displayName
            
            // Determine storage unit (always use base units: g or ml)
            val storageUnit = if (catalogIngredient != null) {
                android.util.Log.d("AddGroceryItemToPantry", "Catalog ingredient found, defaultUnit: ${catalogIngredient.defaultUnit}")
                android.util.Log.d("AddGroceryItemToPantry", "isVolumeUnit: ${com.familymealplanner.domain.model.UnitConversion.isVolumeUnit(catalogIngredient.defaultUnit)}")
                android.util.Log.d("AddGroceryItemToPantry", "isWeightUnit: ${com.familymealplanner.domain.model.UnitConversion.isWeightUnit(catalogIngredient.defaultUnit)}")
                
                // For countable items (pcs, etc.), use the catalog's default unit directly
                if (!com.familymealplanner.domain.model.UnitConversion.isVolumeUnit(catalogIngredient.defaultUnit) &&
                    !com.familymealplanner.domain.model.UnitConversion.isWeightUnit(catalogIngredient.defaultUnit)) {
                    android.util.Log.d("AddGroceryItemToPantry", "Using countable unit: ${catalogIngredient.defaultUnit}")
                    catalogIngredient.defaultUnit
                } else {
                    // Use catalog's default unit type, but convert to storage unit
                    if (com.familymealplanner.domain.model.UnitConversion.isVolumeUnit(catalogIngredient.defaultUnit)) {
                        android.util.Log.d("AddGroceryItemToPantry", "Using volume storage unit: ml")
                        "ml"
                    } else {
                        android.util.Log.d("AddGroceryItemToPantry", "Using weight storage unit: g")
                        "g"
                    }
                }
            } else {
                // Determine from grocery item's unit
                if (!com.familymealplanner.domain.model.UnitConversion.isVolumeUnit(groceryItem.unit) &&
                    !com.familymealplanner.domain.model.UnitConversion.isWeightUnit(groceryItem.unit)) {
                    groceryItem.unit
                } else {
                    if (com.familymealplanner.domain.model.UnitConversion.isVolumeUnit(groceryItem.unit)) {
                        "ml"
                    } else {
                        "g"
                    }
                }
            }
            
            android.util.Log.d("AddGroceryItemToPantry", "Determined storage unit: $storageUnit")
            
            val subcategory = catalogIngredient?.subcategory
            
            android.util.Log.d("AddGroceryItemToPantry", "Creating ingredient with:")
            android.util.Log.d("AddGroceryItemToPantry", "  Name: ${groceryItem.ingredientName}")
            android.util.Log.d("AddGroceryItemToPantry", "  Unit: $storageUnit")
            android.util.Log.d("AddGroceryItemToPantry", "  Category: $category")
            android.util.Log.d("AddGroceryItemToPantry", "  Subcategory: $subcategory")
            
            val createResult = createIngredientUseCase(
                name = groceryItem.ingredientName,
                unit = storageUnit,
                category = category,
                subcategory = subcategory,
                allergenIds = emptyList()
            )
            
            if (createResult.isFailure) {
                val errorMsg = "Failed to create ingredient: ${createResult.exceptionOrNull()?.message}"
                android.util.Log.e("AddGroceryItemToPantry", errorMsg)
                return Result.Error(errorMsg)
            }
            
            android.util.Log.d("AddGroceryItemToPantry", "Ingredient created successfully")
            
            ingredient = ingredientRepository.getIngredientByName(groceryItem.ingredientName)
            if (ingredient == null) {
                val errorMsg = "Failed to retrieve created ingredient"
                android.util.Log.e("AddGroceryItemToPantry", errorMsg)
                return Result.Error(errorMsg)
            }
            
            android.util.Log.d("AddGroceryItemToPantry", "Retrieved created ingredient: ${ingredient.name} (ID: ${ingredient.id})")
        }
        
        // Add to pantry
        android.util.Log.d("AddGroceryItemToPantry", "=== Adding to pantry ===")
        android.util.Log.d("AddGroceryItemToPantry", "Grocery item quantity: ${groceryItem.quantity} ${groceryItem.unit}")
        android.util.Log.d("AddGroceryItemToPantry", "Ingredient storage unit: ${ingredient.unit}")
        
        // Convert the quantity from the user's chosen unit to the ingredient's storage unit
        val quantityInIngredientUnit = if (groceryItem.unit != ingredient.unit) {
            android.util.Log.d("AddGroceryItemToPantry", "Unit conversion needed: ${groceryItem.unit} -> ${ingredient.unit}")
            
            // Check if both are countable units (pcs, etc.) - no conversion needed
            if (!com.familymealplanner.domain.model.UnitConversion.isVolumeUnit(groceryItem.unit) &&
                !com.familymealplanner.domain.model.UnitConversion.isWeightUnit(groceryItem.unit) &&
                !com.familymealplanner.domain.model.UnitConversion.isVolumeUnit(ingredient.unit) &&
                !com.familymealplanner.domain.model.UnitConversion.isWeightUnit(ingredient.unit)) {
                android.util.Log.d("AddGroceryItemToPantry", "Both are countable units, using original quantity")
                groceryItem.quantity
            } else {
                // Use toStorageUnit to convert to base unit (g or ml)
                val storageResult = com.familymealplanner.domain.model.UnitConversion.toStorageUnit(
                    groceryItem.quantity,
                    groceryItem.unit
                )
                if (storageResult != null) {
                    android.util.Log.d("AddGroceryItemToPantry", "Converted ${groceryItem.quantity} ${groceryItem.unit} -> ${storageResult.first} ${storageResult.second}")
                    // The storage unit should match the ingredient's unit (both should be g or ml)
                    storageResult.first
                } else {
                    android.util.Log.w("AddGroceryItemToPantry", "Unit conversion failed, using original quantity")
                    // If conversion fails, use the original quantity
                    groceryItem.quantity
                }
            }
        } else {
            android.util.Log.d("AddGroceryItemToPantry", "No unit conversion needed")
            groceryItem.quantity
        }
        
        android.util.Log.d("AddGroceryItemToPantry", "Final quantity to add: $quantityInIngredientUnit ${ingredient.unit}")
        
        val restockResult = restockIngredientUseCase(
            ingredientId = ingredient.id,
            quantity = quantityInIngredientUnit,
            reason = "Purchased from grocery list: ${groceryItem.mealName}"
        )
        
        android.util.Log.d("AddGroceryItemToPantry", "Restock completed for ingredient: ${ingredient.name}")
        android.util.Log.d("AddGroceryItemToPantry", "=== Successfully added to pantry ===")
        
        return Result.Success
    }
    
    sealed class Result {
        object Success : Result()
        data class Error(val message: String) : Result()
    }
}
