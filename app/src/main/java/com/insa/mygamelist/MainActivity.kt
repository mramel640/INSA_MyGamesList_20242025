package com.insa.mygamelist

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.insa.mygamelist.data.Genre
import com.insa.mygamelist.data.IGDB
import com.insa.mygamelist.ui.theme.MyGamesListTheme
import kotlinx.serialization.Serializable



class MainScreen(navController: NavHostController) {
    @SuppressLint("NotConstructor")
    @Composable
    fun MainScreen(navController: NavController) {
        fun getnomfromid(id : Long, liste : List<Genre>): String{
            for(genre in liste){
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

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        //LazyColumn(modifier = Modifier.padding(innerPadding)) { //permet de dérouler l'écran

            //items(IGDB.games) { game -> // Pour chaque jeu dans la liste IGDB.games, afficher un item dans la LazyColumn
                Card(
                    modifier = Modifier
                        .fillMaxWidth() // Remplir toute la largeur disponible
                        .padding(8.dp) // Espacement autour de chaque jeu
                        .background(Color(200, 200, 200), RoundedCornerShape(8.dp))
                        .padding(16.dp) ,// Espacement interne dans la Box
                        .clickable {
                        // Lorsque la Box est cliquée, naviguer vers l'écran "details_screen"
                            navController.navigate("details_screen")
                        }
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        AsyncImage(model = "https:"+ getcoverfromid(game.cover), contentDescription = "Image de couverture")

                        Column(
                            modifier = Modifier
                                .fillMaxWidth() // Faire en sorte que la colonne occupe toute la largeur
                                .padding(8.dp)
                        ) {
                            //for (game in IGDB.games) {

                            Text(game.name)
                            FlowRow(
                                modifier = Modifier.padding(top = 4.dp), // Padding optionnel pour espacer un peu les genres
                                horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacement horizontal entre les genres
                                verticalArrangement = Arrangement.spacedBy(4.dp) // Espacement vertical entre les genres
                            ) {
                                Text("Genres : ")
                                for (genre in game.genres) {
                                    Text(getnomfromid(genre, IGDB.genres))
                                    Text(" ")
                                }
                            }

                            //}
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        IGDB.loadcovers(this)
        IGDB.loadgames(this)
        IGDB.loadgenres(this)
        IGDB.loadplatform_logos(this)
        IGDB.loadplatforms(this)

        enableEdgeToEdge()
        setContent {

            MyGamesListTheme {
                val navController = rememberNavController()

                Scaffold(topBar = {
                    TopAppBar(colors = topAppBarColors(
                        containerColor = Color.Magenta,
                        titleContentColor = Color.Black,
                    ), title = { Text("My Games List") })
                }) { innerPadding ->
                    NavHost(navController = navController, startDestination = "main_screen") {
                        composable("main_screen") {
                            MainScreen(navController)  // Écran principal avec la Box cliquable
                        }
                        composable("details_screen") {
                            DetailsScreen()  // Détails du jeu ou autre contenu
                        }
                    }
                }
            }
        }
    }
}

class DetailsScreen {

}




