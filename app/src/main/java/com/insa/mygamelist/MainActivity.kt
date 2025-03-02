package com.insa.mygamelist

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
import com.insa.mygamelist.data.IGDB
import com.insa.mygamelist.ui.theme.MyGamesListTheme
import kotlinx.serialization.Serializable


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
    return "Pas de platforme trouvée"
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

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        IGDB.loadcovers(this) //recup les .json
        IGDB.loadgames(this)
        IGDB.loadgenres(this)
        IGDB.loadplatform_logos(this)
        IGDB.loadplatforms(this)

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
                    }
                }
            }
        }
    }
}







