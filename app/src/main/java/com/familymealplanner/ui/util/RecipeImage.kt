package com.familymealplanner.ui.util

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Displays a recipe image from local storage path or assets.
 * Shows a placeholder icon if the image doesn't exist or fails to load.
 */
@Composable
fun RecipeImage(
    imagePath: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    var bitmap by remember(imagePath) { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isLoading by remember(imagePath) { mutableStateOf(true) }

    LaunchedEffect(imagePath) {
        isLoading = true
        bitmap = if (!imagePath.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                try {
                    // Check if it's an asset path (starts with "recipes/")
                    if (imagePath.startsWith("recipes/")) {
                        // Load from assets
                        context.assets.open(imagePath).use { inputStream ->
                            BitmapFactory.decodeStream(inputStream)
                        }
                    } else {
                        // Load from file system
                        val file = File(imagePath)
                        if (file.exists()) {
                            BitmapFactory.decodeFile(imagePath)
                        } else null
                    }
                } catch (e: Exception) {
                    null
                }
            }
        } else null
        isLoading = false
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale
            )
        } else {
            // Placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "No image",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}
