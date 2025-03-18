package com.insa.mygamelist

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.insa.mygamelist.data.IGDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onNavigateToDetails: (Long, FavoriesDataStore, Set<String>) -> Unit, onNavigateToFavories: (FavoriesDataStore, Set<String>) -> Unit, onNavigatetoAddGame : (FavoriesDataStore, Set<String>) -> Unit, innerPadding: PaddingValues) { //Page principale
    val context = LocalContext.current //Récupére le contexte de l'application

    // rememberSaveable permet de garder l'état de la bar de recherche même après un changement de page
    var query by rememberSaveable { mutableStateOf("") } //Définit query comme un état mutable avec une valeur initiale vide (""), remember permet de conserver sa valeur
    var active by rememberSaveable { mutableStateOf(false) } //Initialise active à false, donc la barre de recherche est fermée au début

    val favoriesDataStore = remember { FavoriesDataStore(context) }
    // Lire les favoris enregistrés
    val favoriteGames by favoriesDataStore.favoriteGames.collectAsState(initial = emptySet())

    val filteredItems = IGDB.games.filter {game ->
        game.name?.contains(query, ignoreCase = true) ?: false  ||
                game.genres?.any { genreid -> //genreid correspond à chaque id des genres pour chaque jeu "game"
                    getgenrefromid(genreid).contains(query, ignoreCase=true)
                } == true ||
                game.platforms?.any { platformid ->
                    getplatformfromid(platformid).contains(query, ignoreCase = true)
                } == true
    } //Tout les propositions que la searchbar peut trouver

    Scaffold(
        topBar = {
            TopAppBar(colors = topAppBarColors(
                containerColor = Color(0, 192, 144, 255),
                titleContentColor = Color.Black,
            ),
                title = {
                if(!active){ //Si la bar de recherche n'est pas activé
                    Text("My Games List", fontWeight = FontWeight.Bold)
                }else{
                    SearchBar(
                        query = query, // Le texte actuel de l'utilisateur
                        onQueryChange = { query = it }, // Met à jour `query` avec la nouvelle saisie
                        onSearch = { active = false },
                        active = active, //Contrôle si la SearchBar est ouverte
                        onActiveChange = { active = it }, // Change l'état d'activation
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text("Rechercher...") }, //Ce qui apparait dans la barre par défault
                        leadingIcon = {
                            IconButton(onClick = { active = false
                                query= ""
                            }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Fermer la recherche")
                            } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {}
                    }
                } ,
                navigationIcon = {
                    if(!active){
                        IconButton(onClick = {
                            // Utilisation de LocalContext pour obtenir l'Activity et fermer l'app
                            (context as? Activity)?.finishAffinity()
                        }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Retour") //Affiche la fleche de retour
                        }
                    } },
                actions = { //Search bar apparait quand on clique dessus
                    if (!active) {
                        IconButton(onClick = { active = true }) { // Bouton pour activer la recherche
                            Icon(Icons.Default.Search, contentDescription = "Rechercher...")
                        }
                    }
                    IconButton(onClick = { // Bouton pour aller à la page Favorie
                        onNavigateToFavories.invoke( favoriesDataStore,favoriteGames)
                        }
                     ) {
                        Icon(Icons.Default.Favorite, contentDescription = "Liste des favories")
                    }
                    IconButton(onClick = { // Bouton pour aller à la page d'ajout d'un jeu
                        onNavigatetoAddGame.invoke(favoriesDataStore,favoriteGames)
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter un jeu")
                    }
                }
            )
        }
    ) { innerPadding ->
        if ( filteredItems.isEmpty()){
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(text = "No match :(", fontSize = 26.sp)
            }
        }else{
            LazyColumn(modifier = Modifier.padding(innerPadding)) { //Permet de dérouler l'écran
                val listToShow = if (query.isNotEmpty()) filteredItems else IGDB.games // Choisie la bonne liste, si la bar de recherche est vide alors il montre toute la liste sinon seul le filtre

                items(listToShow) { game -> // Pour chaque jeu dans la liste, afficher un item dans la LazyColumn
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color(200, 200, 200), RoundedCornerShape(16.dp))
                            .clickable { //Box cliquable pour chaque jeu
                                onNavigateToDetails.invoke( //invoke n'est pas obligatoire mais plus simple de comprendre que onNavigatetoDetails est une lambda
                                    game.id,
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
                                        modifier = Modifier.size(100.dp), // Définie la taille de l'image
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
                                if(game.name.isNullOrEmpty()){
                                    Text(text = "Nom du jeu", fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                                }else {
                                    Text(text = game.name, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                                }
                                FlowRow( //Gère les textes trop long pour la largeur de l'écran
                                    modifier = Modifier.padding(top = 4.dp), // Padding optionnel pour espacer un peu les genres
                                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacement horizontal entre les genres
                                    verticalArrangement = Arrangement.spacedBy(4.dp) // Espacement vertical entre les genres
                                ) {
                                    Text("Genres : ")
                                    if(game.genres.isNullOrEmpty()){
                                        Text(text = "Genre indisponible")
                                    }else {
                                        for (genre in game.genres) {
                                            Text(getgenrefromid(genre))
                                            Text(" ")
                                        }
                                    }
                                }
                            }
                        }
                        if (favoriteGames.contains(game.id.toString())) {
                            IconButton(onClick = {
                                CoroutineScope(Dispatchers.IO).launch { //Nécessaire pour DataStore
                                    favoriesDataStore.removefavories(game.id)
                                }
                            },
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ){
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
                                                 },
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                Icon(
                                    imageVector =Icons.Default.FavoriteBorder,
                                    contentDescription = "Favori vide",
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                deleteGameFromInternalStorage(context, game.id)
                                IGDB.games = IGDB.games.filterNot { it.id == game.id }.toMutableList() // Mise à jour de la liste de jeu
                                },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 30.dp)
                        )
                        {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Supprimer le jeu")
                        }
                    }
                }
            }
        }
    }
}
