package com.example.mobile_labs.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.mobile_labs.R
import com.example.mobile_labs.model.disney.DisneyCharacter
import androidx.compose.ui.graphics.Color
import com.example.mobile_labs.network.ktor.KtorDisneyApi

@Composable
fun HomeScreen() {
    val characters = remember { mutableStateListOf<DisneyCharacter>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { 
        runCatching {
            KtorDisneyApi.getCharacters(351..400)
        }
            .onSuccess { result ->
                result.onSuccess { chars ->
                    characters.clear()
                    characters.addAll(chars)
                    Log.d("HomeScreen", "Загружено ${chars.size} персонажей")
                }.onFailure { ex ->
                    errorMessage = ex.message
                    Log.e("HomeScreen", "Ошибка загрузки: ${ex.message}")
                }
            }
            .onFailure { ex ->
                errorMessage = ex.message
                Log.e("HomeScreen", "Ошибка сети: ${ex.message}")
            }
        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.fon1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(title = "Персонажи Дисней")
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                when {
                    isLoading -> LoadingContent()
                    errorMessage != null -> ErrorContent(errorMessage!!)
                    characters.isEmpty() -> EmptyContent()
                    else -> CharactersList(characters)
                }
            }
        }
    }
}

@Composable
fun LoadingContent() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = Color.White)
        Spacer(Modifier.height(8.dp))
        Text("Загрузка", color = Color.White)
    }
}

@Composable
fun ErrorContent(message: String) {
    Text("Ошибка: $message", color = Color.White)
}

@Composable
fun EmptyContent() {
    Text("Нет данных для отображения", color = Color.White)
}

@Composable
fun CharactersList(characters: List<DisneyCharacter>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        items(characters) { character ->
            DisneyCharacterCard(character = character, modifier = Modifier)
        }
    }
}