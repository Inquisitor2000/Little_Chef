package com.littlechef.app.data.repository

import com.littlechef.app.data.local.dao.IngredientDao
import com.littlechef.app.data.local.dao.MealDao
import com.littlechef.app.data.local.dao.MealIngredientDao
import com.littlechef.app.data.local.entity.IngredientEntity
import com.littlechef.app.data.local.entity.MealEntity
import com.littlechef.app.data.local.entity.MealIngredientEntity
import com.littlechef.app.domain.model.Ingredient
import com.littlechef.app.domain.model.Meal
import com.littlechef.app.domain.model.MealIngredient
import com.littlechef.app.domain.repository.MealIngredientInput
import com.littlechef.app.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val mealIngredientDao: MealIngredientDao,
    private val ingredientDao: IngredientDao,
    private val ingredientAllergenDao: com.littlechef.app.data.local.dao.IngredientAllergenDao,
    private val allergenDao: com.littlechef.app.data.local.dao.AllergenDao,
    private val ingredientSubstituteDao: com.littlechef.app.data.local.dao.IngredientSubstituteDao,
    private val localeManager: com.littlechef.app.data.preferences.LocaleManager,
    private val translationSystem: com.littlechef.app.data.local.TranslationSystem,
    private val ingredientMatcher: com.littlechef.app.domain.util.IngredientMatcher
) : MealRepository {

    override suspend fun createMeal(meal: Meal, ingredients: List<MealIngredientInput>): Result<Unit> {
        // Validate all ingredients exist
        for (input in ingredients) {
            val ingredient = ingredientDao.getById(input.ingredientId)
            if (ingredient == null) {
                return Result.failure(
                    IllegalArgumentException("Ingredient with id ${input.ingredientId} does not exist")
                )
            }
        }
        
        // Get current language and update meal with it
        val currentLanguage = localeManager.getLanguage()
        val mealWithLanguage = meal.copy(createdInLanguage = currentLanguage)
        
        // Insert meal
        mealDao.insert(mealWithLanguage.toEntity())
        
        // Insert meal ingredients
        val mealIngredients = ingredients.map { input ->
            MealIngredientEntity(
                id = UUID.randomUUID().toString(),
                mealId = meal.id,
                ingredientId = input.ingredientId,
                quantity = input.quantity,
                unit = null,
                isStarIngredient = input.isStarIngredient
            )
        }
        if (mealIngredients.isNotEmpty()) {
            mealIngredientDao.insertAll(mealIngredients)
        }
        
        return Result.success(Unit)
    }

    override suspend fun getMealById(id: String): Meal? {
        val entity = mealDao.getById(id) ?: return null
        return entity.toDomainWithIngredients()
    }

    override suspend fun getAllMeals(): List<Meal> {
        return mealDao.getAll().map { it.toDomainWithIngredients() }
    }

    override fun observeAllMeals(): Flow<List<Meal>> {
        return mealDao.observeAll().map { list ->
            list.map { it.toDomainWithIngredients() }
        }
    }

    override fun observeScrapedMeals(): Flow<List<Meal>> {
        return mealDao.observeScrapedMeals().map { list ->
            list.map { it.toDomainWithIngredients() }
        }
    }

    override suspend fun searchMeals(query: String): List<Meal> {
        return mealDao.search(query).map { it.toDomainWithIngredients() }
    }

    override suspend fun updateMeal(meal: Meal, ingredients: List<MealIngredientInput>): Result<Unit> {
        // Validate all ingredients exist
        for (input in ingredients) {
            val ingredient = ingredientDao.getById(input.ingredientId)
            if (ingredient == null) {
                return Result.failure(
                    IllegalArgumentException("Ingredient with id ${input.ingredientId} does not exist")
                )
            }
        }
        
        // Update meal
        mealDao.update(meal.toEntity())
        
        // Delete existing meal ingredients and insert new ones
        mealIngredientDao.deleteByMealId(meal.id)
        val mealIngredients = ingredients.map { input ->
            MealIngredientEntity(
                id = UUID.randomUUID().toString(),
                mealId = meal.id,
                ingredientId = input.ingredientId,
                quantity = input.quantity,
                unit = null,
                isStarIngredient = input.isStarIngredient
            )
        }
        if (mealIngredients.isNotEmpty()) {
            mealIngredientDao.insertAll(mealIngredients)
        }
        
        return Result.success(Unit)
    }

    override suspend fun deleteMeal(meal: Meal) {
        mealDao.delete(meal.toEntity())
    }

    override suspend fun updateMealImage(mealId: String, imagePath: String?): Result<Unit> {
        return try {
            mealDao.updateImagePath(mealId, imagePath, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun MealEntity.toDomainWithIngredients(): Meal {
        val mealIngredientEntities = mealIngredientDao.getByMealId(id)
        val mealIngredients = mealIngredientEntities.mapNotNull { entity ->
            val ingredient = ingredientDao.getById(entity.ingredientId)?.toDomain()
            ingredient?.let {
                MealIngredient(
                    id = entity.id,
                    ingredient = it,
                    quantity = entity.quantity,
                    unit = entity.unit,
                    isStarIngredient = entity.isStarIngredient
                )
            }
        }
        
        return Meal(
            id = id,
            name = name,
            instructions = instructions,
            simpleInstructions = simpleInstructions,
            prepTimeMinutes = prepTimeMinutes,
            cookTimeMinutes = cookTimeMinutes,
            servings = servings,
            isScraped = isScraped,
            isBundled = isBundled,
            imagePath = imagePath,
            mealType = mealType?.let { com.littlechef.app.domain.model.MealType.valueOf(it) },
            dishCategory = dishCategory?.let { com.littlechef.app.domain.model.DishCategory.valueOf(it) },
            createdInLanguage = createdInLanguage,
            createdAt = createdAt,
            updatedAt = updatedAt,
            ingredients = mealIngredients
        )
    }

    private fun Meal.toEntity(): MealEntity {
        return MealEntity(
            id = id,
            name = name,
            instructions = instructions,
            simpleInstructions = simpleInstructions,
            prepTimeMinutes = prepTimeMinutes,
            cookTimeMinutes = cookTimeMinutes,
            servings = servings,
            isScraped = isScraped,
            isBundled = isBundled,
            imagePath = imagePath,
            mealType = mealType?.name,
            dishCategory = dishCategory?.name,
            createdInLanguage = createdInLanguage,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private suspend fun IngredientEntity.toDomain(): Ingredient {
        // Ingredient names in the database might be in English (from bundled recipes)
        // but the catalog is in the current app language, so we need to translate first
        
        // Translate ingredient name to current app language before matching
        val translatedName = translationSystem.translateIngredient(name)
        
        // Try to find allergens from catalog first using fuzzy matching
        val catalogAllergens = mutableListOf<com.littlechef.app.domain.model.Allergen>()
        val matchResult = ingredientMatcher.findMatch(translatedName, threshold = 0.6)
        val catalogIngredient = matchResult?.catalogIngredient
        
        if (catalogIngredient != null && catalogIngredient.allergens.isNotEmpty()) {
            // Use allergens from catalog (more reliable for bundled recipes)
            catalogAllergens.addAll(
                catalogIngredient.allergens.map { commonAllergen ->
                    com.littlechef.app.domain.model.Allergen(
                        id = commonAllergen.name.lowercase(),
                        name = commonAllergen.displayName,
                        createdAt = 0,
                        updatedAt = 0
                    )
                }
            )
        }
        
        // Load allergens from database (for user-created ingredients)
        val ingredientAllergenEntities = ingredientAllergenDao.getByIngredientId(id)
        val dbAllergens = ingredientAllergenEntities.mapNotNull { ingredientAllergen ->
            allergenDao.getById(ingredientAllergen.allergenId)?.let { allergenEntity ->
                com.littlechef.app.domain.model.Allergen(
                    id = allergenEntity.id,
                    name = allergenEntity.name,
                    createdAt = allergenEntity.createdAt,
                    updatedAt = allergenEntity.updatedAt
                )
            }
        }
        
        // Combine catalog and database allergens, removing duplicates by ID
        val allAllergens = (catalogAllergens + dbAllergens).distinctBy { it.id }
        
        // Load substitutes for this ingredient
        val substituteEntities = ingredientSubstituteDao.getByIngredientId(id)
        val substitutes = substituteEntities.mapNotNull { substituteEntity ->
            ingredientDao.getById(substituteEntity.substituteId)?.let { subIngredientEntity ->
                com.littlechef.app.domain.model.IngredientSubstitute(
                    id = substituteEntity.id,
                    substituteIngredient = Ingredient(
                        id = subIngredientEntity.id,
                        name = subIngredientEntity.name,
                        unit = subIngredientEntity.unit,
                        category = subIngredientEntity.category,
                        subcategory = subIngredientEntity.subcategory,
                        createdInLanguage = subIngredientEntity.createdInLanguage,
                        createdAt = subIngredientEntity.createdAt,
                        updatedAt = subIngredientEntity.updatedAt,
                        allergens = emptyList(), // Don't load nested allergens to avoid deep recursion
                        substitutes = emptyList() // Don't load nested substitutes to avoid recursion
                    ),
                    notes = substituteEntity.notes,
                    createdAt = substituteEntity.createdAt
                )
            }
        }
        
        return Ingredient(
            id = id,
            name = name,
            unit = unit,
            category = category,
            subcategory = subcategory,
            createdInLanguage = createdInLanguage,
            createdAt = createdAt,
            updatedAt = updatedAt,
            allergens = allAllergens,
            substitutes = substitutes
        )
    }
}
