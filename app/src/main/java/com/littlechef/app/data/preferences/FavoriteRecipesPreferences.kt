package com.littlechef.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.favoriteRecipesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "favorite_recipes"
)

@Singleton
class FavoriteRecipesPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.favoriteRecipesDataStore
    
    companion object {
        private val FAVORITE_RECIPE_IDS = stringPreferencesKey("favorite_recipe_ids_ordered")
    }
    
    val favoriteRecipeIds: Flow<Set<String>> = dataStore.data.map { preferences ->
        val orderedList = preferences[FAVORITE_RECIPE_IDS]?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
        orderedList.toSet()
    }
    
    val favoriteRecipeIdsOrdered: Flow<List<String>> = dataStore.data.map { preferences ->
        preferences[FAVORITE_RECIPE_IDS]?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    }
    
    suspend fun toggleFavorite(recipeId: String) {
        dataStore.edit { preferences ->
            val currentList = preferences[FAVORITE_RECIPE_IDS]?.split(",")?.filter { it.isNotEmpty() }?.toMutableList() ?: mutableListOf()
            if (currentList.contains(recipeId)) {
                currentList.remove(recipeId)
            } else {
                currentList.add(recipeId)
            }
            preferences[FAVORITE_RECIPE_IDS] = currentList.joinToString(",")
        }
    }
    
    suspend fun isFavorite(recipeId: String): Boolean {
        val preferences = dataStore.data.first()
        val orderedList = preferences[FAVORITE_RECIPE_IDS]?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
        return orderedList.contains(recipeId)
    }
}
