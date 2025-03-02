package com.insa.mygamelist.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.insa.mygamelist.R

object IGDB {

    lateinit var covers: List<Cover>
    lateinit var games: List<Game>
    lateinit var genres: List<Genre>
    lateinit var platform_logos: List<Platform_logo>
    lateinit var platforms: List<Platform>

    fun loadcovers(context: Context) {
        try {
            val coversFromJson: List<Cover> = Gson().fromJson(
                context.resources.openRawResource(R.raw.covers).bufferedReader(),
                object : TypeToken<List<Cover>>() {}.type
            )
            Log.d("IGDB", "Covers loaded successfully: ${coversFromJson.size} items")
            covers = coversFromJson
        } catch (e: Exception) {
            // Log en cas d'erreur
            Log.e("IGDB", "Error loading covers: ${e.message}")
        }
    }

    fun loadgames(context: Context) {
        val gamesFromJson: List<Game> = Gson().fromJson(
            context.resources.openRawResource(R.raw.games).bufferedReader(),
            object : TypeToken<List<Game>>() {}.type
        )

        games = gamesFromJson
    }

    fun loadgenres(context: Context) {
        val genresFromJson: List<Genre> = Gson().fromJson(
            context.resources.openRawResource(R.raw.genres).bufferedReader(),
            object : TypeToken<List<Genre>>() {}.type
        )

        genres = genresFromJson
    }

    fun loadplatform_logos(context: Context) {
        val platform_logosFromJson: List<Platform_logo> = Gson().fromJson(
            context.resources.openRawResource(R.raw.platform_logos).bufferedReader(),
            object : TypeToken<List<Platform_logo>>() {}.type
        )

        platform_logos = platform_logosFromJson
    }

    fun loadplatforms(context: Context) {
        val platformsFromJson: List<Platform> = Gson().fromJson(
            context.resources.openRawResource(R.raw.platforms).bufferedReader(),
            object : TypeToken<List<Platform>>() {}.type
        )

        platforms = platformsFromJson
    }

}

data class Cover(val id: Long, val url: String)
data class Game(val id: Long, val cover: Long, val first_release_date: Long, val genres: List<Long>, val name: String, val platforms: List<Long>, val summary: String, val total_rating: Double, var favorie: Boolean = false)
data class Genre(val id: Long, val name: String)
data class Platform_logo(val id: Long, val url: String)
data class Platform(val id: Long, val name: String, val platform_logo: Long)
