package com.familymealplanner.data.local

import android.content.Context
import coil.ImageLoader
import coil.request.ImageRequest
import com.familymealplanner.domain.model.Cuisine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagePreloader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bundledRecipeLoader: BundledRecipeLoader,
    private val imageLoader: ImageLoader
) {
    private val preloadScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val preloadedImages = mutableSetOf<String>()
    
    /**
     * Preload all recipe images in the background
     * This should be called once when the app starts
     */
    fun preloadAllRecipeImages() {
        preloadScope.launch {
            val allRecipes = bundledRecipeLoader.loadAllBundledRecipes()
            
            allRecipes.values.flatten().forEach { recipe ->
                recipe.imageUrl?.let { url ->
                    if (!preloadedImages.contains(url)) {
                        preloadImage(url)
                        preloadedImages.add(url)
                    }
                }
            }
        }
    }
    
    /**
     * Preload images for a specific cuisine
     * Useful for on-demand preloading when user navigates to a cuisine
     */
    fun preloadCuisineImages(cuisine: Cuisine) {
        preloadScope.launch {
            val recipes = bundledRecipeLoader.loadRecipesForCuisine(cuisine)
            
            recipes.forEach { recipe ->
                recipe.imageUrl?.let { url ->
                    if (!preloadedImages.contains(url)) {
                        preloadImage(url)
                        preloadedImages.add(url)
                    }
                }
            }
        }
    }
    
    private suspend fun preloadImage(url: String) {
        try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()
            
            // Execute the request to cache the image
            imageLoader.execute(request)
        } catch (e: Exception) {
            // Silently fail - image will be loaded on demand if preload fails
        }
    }
}
