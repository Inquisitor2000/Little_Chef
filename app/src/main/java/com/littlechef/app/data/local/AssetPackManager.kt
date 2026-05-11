package com.littlechef.app.data.local

import android.content.Context
import com.google.android.play.core.assetpacks.AssetPackLocation
import com.google.android.play.core.assetpacks.AssetPackManager
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetPackManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val assetPackManager: AssetPackManager = AssetPackManagerFactory.getInstance(context)
    
    /**
     * Check if an asset pack is downloaded and available
     */
    fun isPackAvailable(packName: String): Boolean {
        val location = assetPackManager.getPackLocation(packName)
        // Check if pack is available - location will be non-null if pack is installed
        return location != null
    }
    
    /**
     * Get the asset pack location (for accessing files)
     */
    fun getPackLocation(packName: String): AssetPackLocation? {
        return assetPackManager.getPackLocation(packName)
    }
    
    /**
     * Request download of an asset pack (for on-demand packs)
     */
    fun requestDownload(packName: String): Flow<AssetPackDownloadState> = callbackFlow {
        val listener = AssetPackStateUpdateListener { state ->
            when (state.status()) {
                AssetPackStatus.PENDING -> {
                    trySend(AssetPackDownloadState.Pending)
                }
                AssetPackStatus.DOWNLOADING -> {
                    val progress = if (state.totalBytesToDownload() > 0) {
                        (state.bytesDownloaded().toFloat() / state.totalBytesToDownload().toFloat() * 100).toInt()
                    } else 0
                    trySend(AssetPackDownloadState.Downloading(progress))
                }
                AssetPackStatus.COMPLETED -> {
                    trySend(AssetPackDownloadState.Completed)
                    close()
                }
                AssetPackStatus.FAILED -> {
                    trySend(AssetPackDownloadState.Failed(state.errorCode()))
                    close()
                }
                AssetPackStatus.CANCELED -> {
                    trySend(AssetPackDownloadState.Canceled)
                    close()
                }
                else -> {
                    // Other states: UNKNOWN, WAITING_FOR_WIFI, NOT_INSTALLED, etc.
                }
            }
        }
        
        assetPackManager.registerListener(listener)
        assetPackManager.fetch(listOf(packName))
        
        awaitClose {
            assetPackManager.unregisterListener(listener)
        }
    }
    
    /**
     * Cancel download of an asset pack
     */
    fun cancelDownload(packName: String) {
        assetPackManager.cancel(listOf(packName))
    }
}

sealed class AssetPackDownloadState {
    object Pending : AssetPackDownloadState()
    data class Downloading(val progress: Int) : AssetPackDownloadState()
    object Completed : AssetPackDownloadState()
    data class Failed(val errorCode: Int) : AssetPackDownloadState()
    object Canceled : AssetPackDownloadState()
}
