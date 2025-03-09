package com.insa.mygamelist

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.insa.mygamelist.data.Game
import com.insa.mygamelist.data.IGDB
import com.insa.mygamelist.ui.theme.MyGamesListTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException


fun getgenrefromid(id : Long): String{
    for(genre in IGDB.genres){
        if (genre.id==id){
            return genre.name
        }
    }
    return "Genre inconnu"
}

fun getcoverfromid(id: Long): String{
    for(cover in IGDB.covers){
        if (cover.id==id){
            return cover.url
        }
    }
    return "Pas d'image trouvée"
}

fun getidlogofromIdplatform(id: Long): Long{
    for(platform in IGDB.platforms){
        if (platform.id==id){
            return platform.platform_logo
        }
    }
    return 0
}

fun getlogofromplatform(id: Long): String{
    for(logo in IGDB.platform_logos){
        if (logo.id==id){
            return logo.url
        }
    }
    return "Pas d'image trouvée"
}

fun getplatformfromid(id: Long): String{
    for(platform in IGDB.platforms){
        if (platform.id==id){
            return platform.name
        }
    }
    return "Platforme inconnue"
}

fun copyJsonToInternalStorage(context: Context, filename: String) {
    val file = File(context.filesDir, filename)

    // Vérifier si le fichier existe déjà pour éviter de l'écraser
    if (!file.exists()) {
        try {
            val resourceId = context.resources.getIdentifier(
                filename.removeSuffix(".json"), "raw", context.packageName
            )

            if (resourceId == 0) {
                throw IOException("Fichier $filename introuvable dans res/raw/")
            }

            context.resources.openRawResource(resourceId).use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            println("✅ Copie de $filename réussie vers le stockage interne.")

        } catch (e: IOException) {
            e.printStackTrace()
            println("❌ Erreur d'entrée/sortie lors de la copie de $filename : ${e.message}")
        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ Erreur inattendue : ${e.message}")
        }
    } else {
        println("ℹ️ Le fichier $filename existe déjà dans le stockage interne.")
    }
}

fun saveGameToInternalStorage(context: Context, game: Game) {
    val file = File(context.filesDir, "games.json")
    val gamesList = if (file.exists()) {
        val json = file.readText()
        try {
            Json.decodeFromString<List<Game>>(json).toMutableList()
        } catch (e: Exception) {
            mutableListOf()
        }
    } else {
        mutableListOf()
    }

    if (gamesList.none { it.id == game.id }) {
        gamesList.add(game)
        file.writeText(Json.encodeToString(gamesList))
    }
}

fun isGameIdUnique(newId: Long, existingGames: List<Game>): Boolean {
    return existingGames.none { it.id == newId }
}

fun deleteGameFromInternalStorage(context: Context, gameId: Long) {
    val file = File(context.filesDir, "games.json")

    // Charger les jeux existants à partir du fichier
    val gamesList = if (file.exists()) {
        val json = file.readText()
        try {
            Json.decodeFromString<List<Game>>(json).toMutableList()
        } catch (e: Exception) {
            mutableListOf() // Si une erreur survient (par exemple fichier vide), on retourne une liste vide
        }
    } else {
        mutableListOf()
    }

    // Supprimer le jeu dont l'ID correspond
    val updatedList = gamesList.filterNot { it.id == gameId }.toMutableList()

    // Si la liste a été modifiée, on la sauvegarde de nouveau
    if (updatedList.size != gamesList.size) {
        file.writeText(Json.encodeToString(updatedList))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() { //gere le passage des différents écrans

    @kotlinx.serialization.Serializable
    object Main

    // Define a profile route that takes an ID
    @Serializable
    data class Details(val gameid: Long)

    @kotlinx.serialization.Serializable
    object Favories

    @kotlinx.serialization.Serializable
    object AddGame

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        copyJsonToInternalStorage(this, "covers.json") // Copie le fichier si besoin
        copyJsonToInternalStorage(this, "games.json") // Copie le fichier si besoin
        copyJsonToInternalStorage(this, "genres.json") // Copie le fichier si besoin
        copyJsonToInternalStorage(this, "platforms.json") // Copie le fichier si besoin
        copyJsonToInternalStorage(this, "platform_logos.json") // Copie le fichier si besoin

        IGDB.loadAllData(this)


        enableEdgeToEdge()
        setContent {

            MyGamesListTheme {
                val navController = rememberNavController() //pour naviguer entre les écrans

                Scaffold(topBar = {}) //la topBar se définie pour chaque écran dans leur classe
                { innerPadding ->
                    NavHost(navController = navController, startDestination = Main) { //à l'ouverture de l'appli, on arrive sur la page main
                        composable<Main> {
                            MainScreen(
                                onNavigateToDetails = { id: Long, favoriesdatastore: FavoriesDataStore, favoriteGames: Set<String> ->
                                    navController.navigate(Details(id))
                                },
                                onNavigateToFavories = { favoriesdatastore: FavoriesDataStore, favoriteGames: Set<String> ->
                                    navController.navigate(Favories)
                                },
                                onNavigatetoAddGame = { favoriesdatastore: FavoriesDataStore, favoriteGames: Set<String> ->
                                    navController.navigate(AddGame)
                                },
                                innerPadding
                            ) // Écran principal avec la Box cliquable
                        }
                        composable<Details> {
                            backStackEntry ->
                            val details : Details= backStackEntry.toRoute() //recrée l'objet Details à partir de NavBackStackEntry et de ses arguments.
                            val favoriesDataStore = FavoriesDataStore(LocalContext.current)  // Récupération de l'instance de FavoriesDataStore avec le contexte actuel
                            val favoriteGames by favoriesDataStore.favoriteGames.collectAsState(initial = emptySet())
                            DetailsScreen(navController, innerPadding, details.gameid, favoriesDataStore, favoriteGames)
                        }
                        composable<Favories> {
                            backStackEntry ->
                            val favories : Favories= backStackEntry.toRoute() //recrée l'objet Details à partir de NavBackStackEntry et de ses arguments.
                            val favoriesDataStore = FavoriesDataStore(LocalContext.current)  // Récupération de l'instance de FavoriesDataStore avec le contexte actuel
                            val favoriteGames by favoriesDataStore.favoriteGames.collectAsState(initial = emptySet())
                            FavoriesScreen(
                                onNavigateToDetails = { id: Long, favoriesdatastore: FavoriesDataStore, favoriteGames: Set<String> ->
                                    navController.navigate(Details(id))
                                },
                                navController, innerPadding, favoriesDataStore, favoriteGames)
                        }
                        composable<AddGame> {
                                backStackEntry ->
                            val addgame : AddGame = backStackEntry.toRoute() //recrée l'objet Details à partir de NavBackStackEntry et de ses arguments.
                            val context = LocalContext.current
                            AddGameScreen(
                                onGameAdded = { game ->
                                    // Sauvegarde le jeu dans le stockage interne
                                    saveGameToInternalStorage(context, game)

                                    // Met à jour la liste de jeux dans IGDB (ou autre gestion)
                                    IGDB.games = (IGDB.games + game).toMutableList()
                                },
                                navController, innerPadding
                            )
                        }
                    }
                }
            }
        }
    }
}







