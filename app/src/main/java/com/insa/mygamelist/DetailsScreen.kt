package com.insa.mygamelist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.insa.mygamelist.data.IGDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailsScreen(navController: NavController, innerPadding: PaddingValues, gameId: Long, favoriesDataStore: FavoriesDataStore, favoriteGames: Set<String>) { //Écran de détail du jeu
    val game = IGDB.games.find { it.id == gameId }  // Trouve le jeu correspondant à l'ID

    val context = LocalContext.current

    if (game != null) {
        Scaffold(topBar = {
            TopAppBar(colors = topAppBarColors(
                containerColor = Color(0, 192, 144, 255),
                titleContentColor = Color.Black,
            ), title = {
                if (game.name.isNullOrEmpty()) {
                    Text(text = "Nom du jeu", fontWeight = FontWeight.Bold)
                } else {
                    Text(text = game.name, fontWeight = FontWeight.Bold)
                }
            }, //On met le nom du jeu sur lequel on a cliqué dans l'AppBar
                actions = {
                    if (favoriteGames.contains(game.id.toString())) {
                        IconButton(onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                favoriesDataStore.removefavories(game.id)
                            }
                        }){
                            Icon(
                                imageVector =Icons.Default.Favorite,
                                contentDescription = "Favori plein",
                                tint = Color.Red
                            )
                        }
                    }else{
                        IconButton(onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                favoriesDataStore.addfavories(game.id)
                            }
                        }) {
                            Icon(
                                imageVector =Icons.Default.FavoriteBorder,
                                contentDescription = "Favori vide",
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            IGDB.games= (IGDB.games - game).toMutableList()
                            deleteGameFromInternalStorage(context, game.id)
                            navController.popBackStack()},
                    )
                    {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Supprimer le jeu")
                    }
                          },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack() // Revenir à l'écran précédent grace à la pile de navigation en cliquant sur la flèche
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                })
        }) { innerPadding ->
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                item { //Pas de s, on ne spécifie pas pour chaque jeu car on en a qu'un
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if(game.name.isNullOrEmpty()){
                            Text(text = "Nom du jeu", fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline, fontSize = 30.sp)
                        }else {
                            Text(text = game.name, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline, fontSize = 30.sp)
                        }
                    }
                }
                item {
                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (game.cover == null || getcoverfromid(game.cover) =="Pas d'image trouvée" ){
                            if(game.cover == null || getcoverfromimageid(game.cover) =="Pas d'image trouvée") {
                                AsyncImage(
                                    model = "https://upload.wikimedia.org/wikipedia/commons/a/a3/Image-not-found.png?20210521171500",
                                    contentDescription = "Image not found",
                                    modifier = Modifier.size(300.dp),
                                )
                            }else{
                                AsyncImage(
                                    model = "https://images.igdb.com/igdb/image/upload/t_cover_big/" + getcoverfromimageid(game.cover)+".jpg",
                                    contentDescription = "Image de couverture",
                                    modifier = Modifier.size(300.dp),
                                    )
                            }
                        }else {
                            AsyncImage(
                                model = "https:" + getcoverfromid(game.cover),
                                contentDescription = "Image de couverture",
                                modifier = Modifier.size(300.dp), // Définie la taille de l'image
                            )
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FlowRow(
                            modifier = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            if(game.genres.isNullOrEmpty()){
                                Text(text = "genre indisponible",
                                    style = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                                    color = Color(150, 150, 150)
                                )
                            }else {
                                for (genre in game.genres) {
                                    Text(
                                        getgenrefromid(genre),
                                        style = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                                        color = Color(150, 150, 150),
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                    Column {
                        FlowRow(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            if (game.platforms.isNullOrEmpty()){
                                Text(text = "platforme indisponible")
                            }else {
                                for (platform in game.platforms) {
                                    val id: Long = getidlogofromIdplatform(platform)
                                    if (getlogofromplatform(id) == "Pas d'image trouvée") {
                                        AsyncImage(
                                            model = "https://upload.wikimedia.org/wikipedia/commons/a/a3/Image-not-found.png?20210521171500",
                                            contentDescription = "Image not found",
                                            modifier = Modifier.size(100.dp),
                                        )
                                    } else {
                                        AsyncImage(
                                            model = "https:" + getlogofromplatform(id),
                                            contentDescription = "Image de logo",
                                            modifier = Modifier.size(100.dp),
                                        )
                                    }

                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (game.summary.isNullOrEmpty()){
                        Text("Pas de résumé disponible", modifier = Modifier.padding(10.dp))
                    }else {
                        Text(game.summary, modifier = Modifier.padding(10.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}