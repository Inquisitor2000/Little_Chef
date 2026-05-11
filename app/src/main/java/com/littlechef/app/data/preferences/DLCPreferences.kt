package com.littlechef.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dlcDataStore: DataStore<Preferences> by preferencesDataStore(name = "dlc_preferences")

@Singleton
class DLCPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dlcDataStore
    
    companion object {
        private val PURCHASED_PACKS = stringSetPreferencesKey("purchased_packs")
    }
    
    val purchasedPacks: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[PURCHASED_PACKS] ?: emptySet()
    }
    
    suspend fun isPurchased(packName: String): Boolean {
        return purchasedPacks.first().contains(packName)
    }
    
    suspend fun markPurchased(packName: String) {
        dataStore.edit { preferences ->
            val current = preferences[PURCHASED_PACKS] ?: emptySet()
            preferences[PURCHASED_PACKS] = current + packName
        }
    }
    
    suspend fun removePurchase(packName: String) {
        dataStore.edit { preferences ->
            val current = preferences[PURCHASED_PACKS] ?: emptySet()
            preferences[PURCHASED_PACKS] = current - packName
        }
    }
    
    suspend fun clearAllPurchases() {
        dataStore.edit { preferences ->
            preferences.remove(PURCHASED_PACKS)
        }
    }
}
