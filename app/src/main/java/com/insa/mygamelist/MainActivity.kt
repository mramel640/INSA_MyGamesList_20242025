package com.insa.mygamelist

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.insa.mygamelist.data.Genre
import com.insa.mygamelist.data.IGDB
import com.insa.mygamelist.ui.theme.MyGamesListTheme


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

fun getplatformfromId(id: Long): String{
    for(platform in IGDB.platforms){
        if (platform.id==id){
            return platform.name
        }
    }
    return "Pas de platforme trouvée"
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, innerPadding: PaddingValues) {
    val context = LocalContext.current
    Scaffold(topBar = {
        TopAppBar(colors = topAppBarColors(
            containerColor = Color(0, 192, 144, 255),
            titleContentColor = Color.Black,
        ), title = { Text("My Games List", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = {
                    // Utilisation de LocalContext pour obtenir l'Activity et fermer l'app
                    (context as? Activity)?.finishAffinity()
                }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Retour")
                }
            })
    }) { innerPadding ->
        //Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        LazyColumn(modifier = Modifier.padding(innerPadding)) { //permet de dérouler l'écran

            items(IGDB.games) { game -> // Pour chaque jeu dans la liste IGDB.games, afficher un item dans la LazyColumn
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color(200, 200, 200), RoundedCornerShape(16.dp))
                        .clickable {
                            navController.navigate("details_screen/${game.id}")
                        }
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        AsyncImage(
                            model = "https:" + getcoverfromid(game.cover),
                            contentDescription = "Image de couverture"
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth() // Faire en sorte que la colonne occupe toute la largeur
                                .padding(8.dp)
                        ) {
                            //for (game in IGDB.games) {

                            Text(game.name, fontWeight = FontWeight.Bold,
                                        textDecoration = TextDecoration.Underline,
                            )
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

                Scaffold(topBar = {}) { innerPadding ->
                    NavHost(navController = navController, startDestination = "main_screen") {
                        composable("main_screen") {
                            MainScreen(navController,innerPadding)  // Écran principal avec la Box cliquable
                        }
                        composable("details_screen/{gameId}") {
                                backStackEntry ->
                            val gameId = backStackEntry.arguments?.getString("gameId")?.toLongOrNull()
                            if (gameId != null) {
                                DetailsScreen(navController,innerPadding, gameId)  // L'écran de détails, avec l'ID du jeu
                            } else {
                                Text("Invalid game ID", modifier = Modifier.padding(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailsScreen(navController: NavController,innerPadding: PaddingValues, gameId: Long) {
    val game = IGDB.games.find { it.id == gameId }  // Trouver le jeu correspondant à l'ID

    if (game != null) {
        Scaffold(topBar = {
            TopAppBar(colors = topAppBarColors(
                containerColor = Color(0, 192, 144, 255),
                titleContentColor = Color.Black,
            ), title = { Text(game.name,fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack() // Revenir à l'écran précédent grace à la pile de navigation
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                })
        }) { innerPadding ->
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                item {
                    AsyncImage(
                        model = "https:" + getcoverfromid(game.cover),
                        contentDescription = "Image de couverture"
                    )
                }
                item {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Genres : ", fontWeight = FontWeight.Bold)
                        FlowRow(
                            modifier = Modifier.padding(top = 4.dp), // Padding optionnel pour espacer un peu les genres
                            horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacement horizontal entre les genres
                            verticalArrangement = Arrangement.spacedBy(4.dp) // Espacement vertical entre les genres
                        ) {
                            for (genre in game.genres) {
                                Text(getnomfromid(genre, IGDB.genres))
                                Text(" ")
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Platformes disponibles : ", fontWeight = FontWeight.Bold)
                        FlowRow(
                            modifier = Modifier.padding(top = 4.dp), // Padding optionnel pour espacer un peu les genres
                            horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacement horizontal entre les genres
                            verticalArrangement = Arrangement.spacedBy(4.dp) // Espacement vertical entre les genres
                        ) {
                            for (platform in game.platforms) {
                                Text(getplatformfromId(platform))
                                Text(" ")
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Résumée : ", fontWeight = FontWeight.Bold)
                        Text(game.summary)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Rating :", fontWeight = FontWeight.Bold)
                        Text(game.total_rating.toString())

                    }
                }
            }

            // }
        }
    }else {
        // Si aucun jeu correspondant n'est trouvé
        Text("Game not found", modifier = Modifier.padding(16.dp))
    }
}




