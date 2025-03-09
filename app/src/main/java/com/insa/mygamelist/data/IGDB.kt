package com.insa.mygamelist.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.insa.mygamelist.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

object IGDB {

    var covers: MutableList<Cover> = mutableListOf()
    var games: MutableList<Game> = mutableListOf()
    var genres: MutableList<Genre> = mutableListOf()
    var platform_logos: MutableList<Platform_logo> = mutableListOf()
    var platforms: MutableList<Platform> = mutableListOf()

    inline fun <reified T> loadJsonFromInternal(context: Context, filename: String): MutableList<T> {
        val file = File(context.filesDir, filename)
        return try {
            if (!file.exists()) {
                Log.w("IGDB", "⚠️ Fichier $filename introuvable en stockage interne, retour d'une liste vide.")
                return mutableListOf()
            }
            val jsonText = file.readText()
            Gson().fromJson(jsonText, object : TypeToken<MutableList<T>>() {}.type) ?: mutableListOf()
        } catch (e: IOException) {
            Log.e("IGDB", "❌ Erreur lors du chargement de $filename: ${e.message}")
            mutableListOf()
        }
    }

    private inline fun <reified T> saveJsonToInternal(context: Context, filename: String, data: MutableList<T>) {
        val file = File(context.filesDir, filename)
        try {
            file.writeText(Gson().toJson(data))
            Log.d("IGDB", "✅ Fichier $filename mis à jour avec succès.")
        } catch (e: IOException) {
            Log.e("IGDB", "❌ Erreur lors de l'écriture dans $filename: ${e.message}")
        }
    }

    fun loadAllData(context: Context) {
        covers = loadJsonFromInternal(context, "covers.json")
        games = loadJsonFromInternal(context, "games.json")
        genres = loadJsonFromInternal(context, "genres.json")
        platform_logos = loadJsonFromInternal(context, "platform_logos.json")
        platforms = loadJsonFromInternal(context, "platforms.json")
    }

}

data class Cover(val id: Long, val url: String)
@Serializable
data class Game(val id: Long, val cover: Long, val first_release_date: Long, val genres: List<Long>, val name: String, val platforms: List<Long>, val summary: String, val total_rating: Double, var favorie: Boolean = false)
data class Genre(val id: Long, val name: String)
data class Platform_logo(val id: Long, val url: String)
data class Platform(val id: Long, val name: String, val platform_logo: Long)
