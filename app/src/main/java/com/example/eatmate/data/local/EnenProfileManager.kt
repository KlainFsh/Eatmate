package com.example.eatmate.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.enenDataStore by preferencesDataStore("enen_profile")

class EnenProfileManager(private val context: Context) {

    companion object {
        private val KEY_NAME = stringPreferencesKey("enen_name")
        const val DEFAULT_NAME = "恩恩"
    }

    val nameFlow: Flow<String> = context.enenDataStore.data.map { prefs ->
        prefs[KEY_NAME] ?: DEFAULT_NAME
    }

    suspend fun setName(name: String) {
        context.enenDataStore.edit { prefs ->
            prefs[KEY_NAME] = name
        }
    }

    suspend fun getName(): String {
        return context.enenDataStore.data.map { it[KEY_NAME] ?: DEFAULT_NAME }.first()
    }
}
