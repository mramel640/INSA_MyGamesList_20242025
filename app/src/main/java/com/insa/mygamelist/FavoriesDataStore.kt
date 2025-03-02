package com.insa.mygamelist

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "favorite_games")

class FavoriesDataStore(private val context : Context) {
    private val FAVORITE_GAMES_ID = stringSetPreferencesKey("favorite_games") // Stocke les IDs favoris sous forme de Set<String> car ne prend pas en compte set<Long>

    // Lire les favoris sous forme de Flow (permet une mise à jour en temps réel)
    val favoriteGames: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVORITE_GAMES_ID] ?: emptySet() // Retourne une liste vide si aucune donnée
        }

    suspend fun addfavories(gameId : Long) {
        context.dataStore.edit { preferences ->
            val favorie = preferences[FAVORITE_GAMES_ID] ?: emptySet()
            preferences[FAVORITE_GAMES_ID] = favorie + gameId.toString()
        }
    }

    suspend fun removefavories(gameId: Long){
        context.dataStore.edit { preferences ->
            val favorie = preferences[FAVORITE_GAMES_ID] ?: emptySet()
            preferences[FAVORITE_GAMES_ID] = favorie - gameId.toString()
        }
    }
}