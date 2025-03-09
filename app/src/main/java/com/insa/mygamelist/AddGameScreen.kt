package com.insa.mygamelist

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.insa.mygamelist.data.Game
import com.insa.mygamelist.data.IGDB


@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddGameScreen(onGameAdded: (Game) -> Unit, // Callback pour renvoyer le jeu ajouté
                  navController: NavController,
                  innerPadding: PaddingValues) { //écran de détail du jeu

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var resume by remember { mutableStateOf("") }

    var selectedGenres by remember { mutableStateOf<List<Long>>(emptyList()) } //le genre est d'abord non choisi
    var selectedPlatforms by remember { mutableStateOf<List<Long>>(emptyList()) } //la platform non plus

    val genres by remember { mutableStateOf(IGDB.genres) }
    val platforms by remember { mutableStateOf((IGDB.platforms)) }

    var id : Long = 1

        Scaffold(topBar = {
            TopAppBar(colors = topAppBarColors(
                containerColor = Color(0, 192, 144, 255),
                titleContentColor = Color.Black,
            ), title = { Text("Ajouter un jeu",fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        while (!isGameIdUnique(id, IGDB.games)){
                            id=id+1
                        }
                        val newGame = Game(
                            name = name,
                            summary = resume,
                            genres = selectedGenres,
                            platforms = selectedPlatforms,
                            cover = 0,
                            first_release_date = 0,
                            id = id,
                            total_rating = 0.0
                            )
                        onGameAdded(newGame)
                        saveGameToInternalStorage(context, newGame) // Sauvegarde du jeu dans le stockage interne
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Validé le jeu",
                        )
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
            LazyColumn(modifier = Modifier.padding(innerPadding)) { //pour avoir un écran qui défile
                item {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = name,  // 🔹 Valeur actuelle du champ de texte
                            onValueChange = {
                                name = it
                            },  // 🔹 Callback qui met à jour 'name' lorsque l'utilisateur tape
                            label = { Text("Nom du jeu") },  // 🔹 Texte flottant au-dessus du champ (placeholder)
                            modifier = Modifier.fillMaxWidth() // 🔹 Prend toute la largeur possible
                        )
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = resume,  // 🔹 Valeur actuelle du champ de texte
                            onValueChange = {
                                resume = it
                            },  // 🔹 Callback qui met à jour 'name' lorsque l'utilisateur tape
                            label = { Text("Résumé du jeu") },  // 🔹 Texte flottant au-dessus du champ (placeholder)
                            modifier = Modifier.fillMaxWidth() // 🔹 Prend toute la largeur possible
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Genres", fontWeight = FontWeight.Bold, fontSize = 30.sp)
                    }
                }

                items(genres) { genre ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedGenres = if (genre.id in selectedGenres) {
                                    selectedGenres - genre.id
                                } else {
                                    selectedGenres + genre.id
                                }
                            }
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = genre.id in selectedGenres,
                            onCheckedChange = null
                        )
                        Text(text = genre.name, modifier = Modifier.padding(start = 8.dp)) //le nom des genres
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Platform", fontWeight = FontWeight.Bold, fontSize = 30.sp)
                    }
                }

                items(platforms) { platform ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPlatforms = if (platform.id in selectedPlatforms) {
                                    selectedPlatforms - platform.id
                                } else {
                                    selectedPlatforms + platform.id
                                }
                            }
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = platform.id in selectedPlatforms,
                            onCheckedChange = null
                        )
                        Text(text = platform.name, modifier = Modifier.padding(start = 8.dp)) //le nom des genres
                    }
                }
            }
        }
    }
