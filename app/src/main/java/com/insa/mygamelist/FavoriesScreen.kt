package com.insa.mygamelist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.insa.mygamelist.data.IGDB

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FavoriesScreen(onNavigateToDetails: (Long, FavoriesDataStore, Set<String>) -> Unit, navController: NavController, innerPadding: PaddingValues, favoriesDataStore: FavoriesDataStore, favoriteGames: Set<String>) { //écran des jeux favories
    val jeuxfavories: MutableList<Long> = mutableListOf()

    for (idgame in favoriteGames) {
        jeuxfavories.add(idgame.toLong())
    }

    Scaffold(topBar = {
        TopAppBar(colors = topAppBarColors(
            containerColor = Color(0, 192, 144, 255),
            titleContentColor = Color.Black,
        ), title = { Text("Mes Favories", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack() // Revenir à l'écran précédent grace à la pile de navigation en cliquant sur la flèche
                }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Retour")
                }
            })
    }) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) { //permet de dérouler l'écran


           items(jeuxfavories) { gameId -> // Pour chaque jeu dans la liste, afficher un item dans la LazyColumn
                val game = IGDB.games.find { it.id == gameId }  // Trouver le jeu correspondant à l'ID
                if (game != null) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color(200, 200, 200), RoundedCornerShape(16.dp))
                            .clickable { //box pour chaque jeu cliquable
                                onNavigateToDetails.invoke(
                                    gameId,
                                    favoriesDataStore,
                                    favoriteGames
                                ) //invoke n'est pas obligatoire mais plus simple de comprendre que onNavigatetoDetails est une lambda
                            }
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {

                            if (getcoverfromid(game.cover) =="Pas d'image trouvée"){
                                AsyncImage(
                                    model = "https://upload.wikimedia.org/wikipedia/commons/a/a3/Image-not-found.png?20210521171500",
                                    contentDescription = "Image not found",
                                    modifier = Modifier.size(100.dp), // Définir la taille de l'image
                                )
                            }else {
                                AsyncImage(
                                    model = "https:" + getcoverfromid(game.cover),
                                    contentDescription = "Image de couverture"
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth() // Faire en sorte que la colonne occupe toute la largeur
                                    .padding(8.dp)
                            ) {
                                //for (game in IGDB.games) {

                                Text(
                                    game.name, fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline
                                )
                                FlowRow( //gere les textes trop long pour la largeur de l'écran
                                    modifier = Modifier.padding(top = 4.dp), // Padding optionnel pour espacer un peu les genres
                                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacement horizontal entre les genres
                                    verticalArrangement = Arrangement.spacedBy(4.dp) // Espacement vertical entre les genres
                                ) {
                                    Text("Genres : ")
                                    for (genre in game.genres) {
                                        Text(getgenrefromid(genre))
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
}