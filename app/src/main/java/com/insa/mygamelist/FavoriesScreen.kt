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
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Retour")
                }
            })
    }) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
           items(jeuxfavories) { gameId -> // Pour chaque jeu dans la liste, afficher un item dans la LazyColumn
                val game = IGDB.games.find { it.id == gameId }  // Trouve le jeu correspondant à l'ID
                if (game != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color(200, 200, 200), RoundedCornerShape(16.dp))
                            .clickable {
                                onNavigateToDetails.invoke(
                                    gameId,
                                    favoriesDataStore,
                                    favoriteGames
                                )
                            }
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            if (game.cover == null || getcoverfromid(game.cover) =="Pas d'image trouvée" ){
                                if(game.cover == null || getcoverfromimageid(game.cover) =="Pas d'image trouvée") {
                                    AsyncImage(
                                        model = "https://upload.wikimedia.org/wikipedia/commons/a/a3/Image-not-found.png?20210521171500",
                                        contentDescription = "Image not found",
                                        modifier = Modifier.size(100.dp),
                                    )
                                }else{
                                    AsyncImage(
                                        model = "https://images.igdb.com/igdb/image/upload/t_cover_big/" + getcoverfromimageid(game.cover)+".jpg",
                                        contentDescription = "Image de couverture"
                                    )
                                }
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
                                if (game.name==null){
                                    Text("Nom du jeu", fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                                }else {
                                    Text(game.name, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                                }
                                FlowRow(
                                    modifier = Modifier.padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("Genres : ")
                                    if (game.genres.isNullOrEmpty()) {
                                        Text(text = "genre indisponible")
                                    } else {
                                        for (genre in game.genres) {
                                            Text(getgenrefromid(genre))
                                            Text(" ")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}