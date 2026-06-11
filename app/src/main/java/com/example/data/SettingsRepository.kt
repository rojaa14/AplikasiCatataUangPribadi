package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    private val THEME_KEY = stringPreferencesKey("theme") // "System", "Light", "Dark"
    private val AI_PROVIDER_KEY = stringPreferencesKey("ai_provider")
    private val CUSTOM_API_KEY = stringPreferencesKey("custom_api_key")

    val theme: Flow<String> = context.dataStore.data.map { it[THEME_KEY] ?: "Sistem" }
    val aiProvider: Flow<String> = context.dataStore.data.map { it[AI_PROVIDER_KEY] ?: "Gemini" }
    val customApiKey: Flow<String> = context.dataStore.data.map { it[CUSTOM_API_KEY] ?: "" }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { it[THEME_KEY] = theme }
    }

    suspend fun setAiProvider(provider: String) {
        context.dataStore.edit { it[AI_PROVIDER_KEY] = provider }
    }

    suspend fun setCustomApiKey(key: String) {
        context.dataStore.edit { it[CUSTOM_API_KEY] = key }
    }
}
