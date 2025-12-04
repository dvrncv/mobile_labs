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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import com.example.mobile_labs.R
import com.example.mobile_labs.model.disney.DisneyCharacter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.mobile_labs.network.ktor.KtorDisneyApi
import com.example.mobile_labs.store.dataStore.SettingsDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreenContainer(
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current

    val characters = remember { mutableStateListOf<DisneyCharacter>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val settingsDataStore = remember { SettingsDataStore(context) }
    val fontSize by settingsDataStore.currentFontSize.collectAsState(initial = 16f)

    LaunchedEffect(Unit) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            KtorDisneyApi
                .getCharacters(351..400)
                .onSuccess { list ->
                    characters.clear()
                    characters.addAll(list)
                }
                .onFailure { ex ->
                    errorMessage = ex.message
                }

            withContext(Dispatchers.Main) {
                isLoading = false
            }
        }
    }

    HomeScreen(
        onOpenSettings = onOpenSettings,
        fontSize = fontSize,
        characters = characters,
        isLoading = isLoading,
        errorMessage = errorMessage
    )
}


@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    fontSize: Float,
    characters: List<DisneyCharacter>,
    isLoading: Boolean,
    errorMessage: String?
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.fon1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                title = "Персонажи Дисней",
                onSettingsClick = onOpenSettings,
                fontSize = fontSize
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> LoadingContent(fontSize)
                    errorMessage != null -> ErrorContent(errorMessage, fontSize)
                    characters.isEmpty() -> EmptyContent(fontSize)
                    else -> CharactersList(characters, fontSize)
                }
            }
        }
    }
}

@Composable
fun LoadingContent(fontSize: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = Color.White)
        Spacer(Modifier.height(8.dp))
        Text("Загрузка", color = Color.White, fontSize = fontSize.sp)
    }
}

@Composable
fun ErrorContent(message: String, fontSize: Float) {
    Text("Ошибка: $message", color = Color.White, fontSize = fontSize.sp)
}

@Composable
fun EmptyContent(fontSize: Float) {
    Text("Нет данных для отображения", color = Color.White, fontSize = fontSize.sp)
}

@Composable
fun CharactersList(characters: List<DisneyCharacter>, fontSize: Float) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        items(characters) { character ->
            DisneyCharacterCard(character = character, fontSize = fontSize)
        }
    }
}