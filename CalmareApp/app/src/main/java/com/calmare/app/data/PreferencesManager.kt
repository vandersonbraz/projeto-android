package com.calmare.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "calmare_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        // Settings keys
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val AUTOPLAY_ENABLED = booleanPreferencesKey("autoplay_enabled")
        val DOWNLOAD_WIFI_ONLY = booleanPreferencesKey("download_wifi_only")

        // Favorites key (stored as comma-separated IDs)
        val FAVORITE_SOUND_IDS = stringPreferencesKey("favorite_sound_ids")

        // Last mood key
        val LAST_MOOD = stringPreferencesKey("last_mood")
    }

    // Settings flows
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED] ?: true
    }

    val autoPlayEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTOPLAY_ENABLED] ?: false
    }

    val downloadWifiOnly: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DOWNLOAD_WIFI_ONLY] ?: true
    }

    // Favorites flow
    val favoriteSoundIds: Flow<Set<Int>> = context.dataStore.data.map { preferences ->
        val idsString = preferences[FAVORITE_SOUND_IDS] ?: ""
        if (idsString.isEmpty()) {
            emptySet()
        } else {
            idsString.split(",").mapNotNull { it.toIntOrNull() }.toSet()
        }
    }

    // Last mood flow
    val lastMood: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LAST_MOOD] ?: ""
    }

    // Save notification setting
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    // Save autoplay setting
    suspend fun setAutoPlayEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTOPLAY_ENABLED] = enabled
        }
    }

    // Save download wifi setting
    suspend fun setDownloadWifiOnly(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DOWNLOAD_WIFI_ONLY] = enabled
        }
    }

    // Add favorite
    suspend fun addFavorite(soundId: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[FAVORITE_SOUND_IDS] ?: ""
            val currentSet = if (current.isEmpty()) {
                emptySet()
            } else {
                current.split(",").mapNotNull { it.toIntOrNull() }.toSet()
            }
            val newSet = currentSet + soundId
            preferences[FAVORITE_SOUND_IDS] = newSet.joinToString(",")
        }
    }

    // Remove favorite
    suspend fun removeFavorite(soundId: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[FAVORITE_SOUND_IDS] ?: ""
            val currentSet = if (current.isEmpty()) {
                emptySet()
            } else {
                current.split(",").mapNotNull { it.toIntOrNull() }.toSet()
            }
            val newSet = currentSet - soundId
            preferences[FAVORITE_SOUND_IDS] = newSet.joinToString(",")
        }
    }

    // Check if sound is favorite
    suspend fun isFavorite(soundId: Int): Boolean {
        val ids = mutableSetOf<Int>()
        context.dataStore.data.map { preferences ->
            val idsString = preferences[FAVORITE_SOUND_IDS] ?: ""
            if (idsString.isNotEmpty()) {
                ids.addAll(idsString.split(",").mapNotNull { it.toIntOrNull() })
            }
        }.collect { }
        return ids.contains(soundId)
    }

    // Save last mood
    suspend fun saveMood(mood: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_MOOD] = mood
        }
    }
}
