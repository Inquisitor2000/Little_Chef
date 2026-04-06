package com.familymealplanner.di

import android.content.Context
import coil.ImageLoader
import com.familymealplanner.utils.ImageLoaderConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {
    
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader {
        return ImageLoaderConfig.createOptimizedImageLoader(context)
    }
}
