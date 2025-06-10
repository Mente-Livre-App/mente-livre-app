package com.example.safelife.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// ✅ Extensão para o DataStore associada ao contexto
private val Context.dataStore by preferencesDataStore(name = "lgpd_prefs")

/**
 * Classe responsável por gerenciar o estado de consentimento do usuário quanto à LGPD.
 */
class LGPDPreferences(private val context: Context) {

    companion object {
        private val CONSENTIMENTO_KEY = booleanPreferencesKey("lgpd_consentimento_dado")
    }

    /**
     * Salva o consentimento do usuário localmente.
     */
    suspend fun salvarConsentimento(dado: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[CONSENTIMENTO_KEY] = dado
        }
    }

    /**
     * Lê se o consentimento já foi fornecido.
     */
    suspend fun consentimentoFoiDado(): Boolean {
        return context.dataStore.data
            .map { prefs -> prefs[CONSENTIMENTO_KEY] ?: false }
            .first()
    }
}
