package com.littlechef.app

import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.littlechef.app.data.local.TranslationSystem
import com.littlechef.app.data.preferences.LocaleManager
import com.littlechef.app.utils.ImageLoaderConfig
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MealPlannerApp : Application(), ImageLoaderFactory {
    
    @Inject
    lateinit var imagePreloader: com.littlechef.app.data.local.ImagePreloader
    
    @Inject
    lateinit var substituteInitializer: com.littlechef.app.data.local.SubstituteInitializer
    
    @Inject
    lateinit var translationSystem: TranslationSystem
    
    @Inject
    lateinit var localeManager: LocaleManager
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        
        // Initialize translation system with saved language preference
        val savedLanguage = localeManager.getLanguage()
        translationSystem.initialize(savedLanguage)
        
        // Preload recipe images in the background
        imagePreloader.preloadAllRecipeImages()
        
        // Initialize ingredient substitutes in the database
        applicationScope.launch {
            try {
                substituteInitializer.initialize()
            } catch (e: Exception) {
                android.util.Log.e("MealPlannerApp", "Failed to initialize substitutes: ${e.message}")
            }
        }
    }
    
    override fun attachBaseContext(base: Context) {
        // Apply locale before calling super to ensure it's applied to the entire app
        super.attachBaseContext(base)
    }
    
    override fun newImageLoader(): ImageLoader {
        return ImageLoaderConfig.createOptimizedImageLoader(this)
    }
}
