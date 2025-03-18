package com.insa.mygamelist.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.Serializable
import java.io.File
import java.io.IOException

object IGDB {

    var covers: MutableList<Cover> = mutableListOf()
    var coverstwitch: MutableList<Covertwitch> = mutableListOf()
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

    fun loadAllData(context: Context) {
        covers = loadJsonFromInternal<Cover>(context, "covers.json")
        coverstwitch = loadJsonFromInternal<Covertwitch>(context, "coverstwitch.json")
        games = (loadJsonFromInternal<Game>(context, "games.json") + loadJsonFromInternal<Game>(context, "gamestwitch.json")).toMutableList()
        genres = loadJsonFromInternal<Genre>(context, "genres.json")
        platform_logos = loadJsonFromInternal<Platform_logo>(context, "platform_logos.json")
        platforms = loadJsonFromInternal<Platform>(context, "platforms.json")

    }

}

data class Cover(val id: Long, val url: String)
data class Covertwitch(val id: Long, val image_id: String)
@Serializable
data class Game(val id: Long, val cover: Long?=0, val first_release_date: Long? = null, val genres: List<Long>? = emptyList(), val name: String? = "Nom du Jeu", val platforms: List<Long>? = emptyList(), val summary: String? = "Résumé", val total_rating: Double? = 0.0, var favorie: Boolean = false)
data class Genre(val id: Long, val name: String)
data class Platform_logo(val id: Long, val url: String)
data class Platform(val id: Long, val name: String, val platform_logo: Long)
