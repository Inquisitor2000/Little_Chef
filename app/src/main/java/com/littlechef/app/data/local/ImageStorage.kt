package com.littlechef.app.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles saving and loading recipe images to/from local storage.
 */
@Singleton
class ImageStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val imagesDir: File
        get() = File(context.filesDir, "recipe_images").also { 
            if (!it.exists()) it.mkdirs() 
        }

    /**
     * Save an image from a content Uri to local storage.
     * @return The local file path, or null if saving failed.
     */
    suspend fun saveFromUri(uri: Uri, mealId: String): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (bitmap != null) {
                saveBitmap(bitmap, mealId)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Save a bitmap to local storage.
     * @return The local file path, or null if saving failed.
     */
    suspend fun saveBitmap(bitmap: Bitmap, mealId: String): String? = withContext(Dispatchers.IO) {
        try {
            val fileName = "${mealId}_${UUID.randomUUID()}.jpg"
            val file = File(imagesDir, fileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Download an image from URL and save it locally.
     * @return The local file path, or null if download/saving failed.
     */
    suspend fun downloadAndSave(imageUrl: String, mealId: String): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.connect()
            
            val inputStream = connection.getInputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            
            if (bitmap != null) {
                saveBitmap(bitmap, mealId)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Load a bitmap from local storage.
     */
    suspend fun loadBitmap(path: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            if (file.exists()) {
                BitmapFactory.decodeFile(path)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Delete an image from local storage.
     */
    suspend fun deleteImage(path: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Check if an image exists at the given path.
     */
    fun imageExists(path: String?): Boolean {
        if (path.isNullOrBlank()) return false
        return File(path).exists()
    }
}
