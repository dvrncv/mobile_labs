package com.example.mobile_labs.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.compose.runtime.collectAsState
import com.example.mobile_labs.R
import com.example.mobile_labs.model.disney.DisneyCharacter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.mobile_labs.ui.theme.Pink40
import com.example.mobile_labs.ui.theme.Pink80
import com.example.mobile_labs.store.dataStore.SettingsDataStore
import com.example.mobile_labs.store.repository.DisneyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreenContainer(
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userNumber = 8

    val repository = remember { DisneyRepository(context, userNumber) }
    
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentUserNumber by remember { mutableStateOf(userNumber) }

    val characters by repository.getCharactersUpToNumberFlow(currentUserNumber).collectAsState(initial = emptyList())

    var showDeleteAllDialog by remember { mutableStateOf(false) }

    val settingsDataStore = remember { SettingsDataStore(context) }
    val fontSize by settingsDataStore.currentFontSize.collectAsState(initial = 16f)

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            isLoading = true
            try {
                repository.coldStart()
                    .onSuccess {
                        Log.d("HomeScreen", "Холодный старт: данные загружены")
                    }
                    .onFailure { ex ->
                        errorMessage = ex.message
                        Log.e("HomeScreen", "Ошибка холодного старта", ex)
                    }
            } catch (e: Exception) {
                errorMessage = e.message
                Log.e("HomeScreen", "Ошибка при холодном старте", e)
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    fun handleRefresh() {
        scope.launch(Dispatchers.IO) {
            isLoading = true
            errorMessage = null
            try {
                repository.refreshCharacters()
                    .onSuccess {
                        Log.d("HomeScreen", "Данные обновлены")
                    }
                    .onFailure { ex ->
                        errorMessage = ex.message
                        Log.e("HomeScreen", "Ошибка обновления", ex)
                    }
            } catch (e: Exception) {
                errorMessage = e.message
                Log.e("HomeScreen", "Ошибка при обновлении", e)
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    fun handleLoadMore() {
        scope.launch(Dispatchers.IO) {
            isLoading = true
            errorMessage = null
            val nextNumber = currentUserNumber + 1
            try {
                repository.loadCharactersByNumber(nextNumber)
                    .onSuccess {
                        withContext(Dispatchers.Main) {
                            currentUserNumber = nextNumber
                        }
                        Log.d("HomeScreen", "Загружены данные для номера $nextNumber")
                    }
                    .onFailure { ex ->
                        withContext(Dispatchers.Main) {
                            errorMessage = ex.message
                        }
                        Log.e("HomeScreen", "Ошибка загрузки", ex)
                    }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = e.message
                }
                Log.e("HomeScreen", "Ошибка при загрузке", e)
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    fun handleDeleteCharacter(character: DisneyCharacter) {
        scope.launch(Dispatchers.IO) {
            isLoading = true
            errorMessage = null
            try {
                repository.deleteCharacter(character)
                    .onSuccess {
                        Log.d("HomeScreen", "Персонаж удален: ${character.name}")
                    }
                    .onFailure { ex ->
                        withContext(Dispatchers.Main) {
                            errorMessage = ex.message
                        }
                        Log.e("HomeScreen", "Ошибка удаления", ex)
                    }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = e.message
                }
                Log.e("HomeScreen", "Ошибка при удалении", e)
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    fun handleDeleteAllClick() {
        showDeleteAllDialog = true
    }

    fun handleDeleteAll() {
        scope.launch(Dispatchers.IO) {
            isLoading = true
            errorMessage = null
            try {
                repository.deleteAllCharacters()
                    .onSuccess {
                        withContext(Dispatchers.Main) {
                            showDeleteAllDialog = false
                            currentUserNumber = userNumber
                        }
                        Log.d("HomeScreen", "Все персонажи удалены")
                    }
                    .onFailure { ex ->
                        withContext(Dispatchers.Main) {
                            errorMessage = ex.message
                            showDeleteAllDialog = false
                        }
                        Log.e("HomeScreen", "Ошибка удаления всех", ex)
                    }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = e.message
                    showDeleteAllDialog = false
                }
                Log.e("HomeScreen", "Ошибка при удалении всех", e)
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    HomeScreen(
        onOpenSettings = onOpenSettings,
        fontSize = fontSize,
        characters = characters,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onRefresh = ::handleRefresh,
        onLoadMore = ::handleLoadMore,
        currentNumber = currentUserNumber,
        onDeleteClick = ::handleDeleteCharacter,
        onDeleteAllClick = ::handleDeleteAllClick,
        showDeleteAllDialog = showDeleteAllDialog,
        onDismissDialog = {
            showDeleteAllDialog = false
        },
        onConfirmDeleteAll = ::handleDeleteAll
    )
}


@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    fontSize: Float,
    characters: List<DisneyCharacter>,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    currentNumber: Int,
    onDeleteClick: (DisneyCharacter) -> Unit,
    onDeleteAllClick: () -> Unit,
    showDeleteAllDialog: Boolean,
    onDismissDialog: () -> Unit,
    onConfirmDeleteAll: () -> Unit
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
                onRefreshClick = onRefresh,
                fontSize = fontSize,
                isLoading = isLoading
            )

            ActionButtons(
                onDeleteAllClick = onDeleteAllClick,
                fontSize = fontSize,
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading && characters.isEmpty() -> LoadingContent(fontSize)
                    errorMessage != null -> ErrorContent(errorMessage, fontSize)
                    characters.isEmpty() -> EmptyContent(fontSize)
                    else -> CharactersList(
                        characters = characters,
                        fontSize = fontSize,
                        isLoading = isLoading,
                        onDeleteClick = onDeleteClick,
                        onLoadMore = onLoadMore,
                        currentNumber = currentNumber
                    )
                }
            }
        }

        if (showDeleteAllDialog) {
            DeleteAllConfirmDialog(
                onConfirm = onConfirmDeleteAll,
                onDismiss = onDismissDialog,
                fontSize = fontSize
            )
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
fun ActionButtons(
    onDeleteAllClick: () -> Unit,
    fontSize: Float,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val deleteAllInteractionSource = remember { MutableInteractionSource() }
        Button(
            onClick = { if (!isLoading) onDeleteAllClick() },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(listOf(Pink80, Pink40)),
                    shape = RoundedCornerShape(8.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.White
            ),
            interactionSource = deleteAllInteractionSource
        ) {
            Text("Удалить все", fontSize = (fontSize * 0.9f).sp)
        }
    }
}

@Composable
fun CharactersList(
    characters: List<DisneyCharacter>,
    fontSize: Float,
    isLoading: Boolean,
    onDeleteClick: (DisneyCharacter) -> Unit,
    onLoadMore: () -> Unit,
    currentNumber: Int
) {
    Box {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            items(characters) { character ->
                DisneyCharacterCard(
                    character = character,
                    fontSize = fontSize,
                    onDeleteClick = { onDeleteClick(character) }
                )
            }

            item {
                val loadMoreInteractionSource = remember { MutableInteractionSource() }
                Button(
                    onClick = { if (!isLoading) onLoadMore() },
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .background(
                            brush = Brush.horizontalGradient(listOf(Pink80, Pink40)),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.White
                    ),
                    interactionSource = loadMoreInteractionSource
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        "Загрузить еще...",
                        fontSize = (fontSize * 0.9f).sp
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteAllConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    fontSize: Float
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Удалить все данные?",
                fontSize = fontSize.sp
            )
        },
        text = {
            Text(
                text = "Вы уверены, что хотите удалить всех персонажей из базы данных? Это действие нельзя отменить.",
                fontSize = (fontSize * 0.9f).sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.background(
                    brush = Brush.horizontalGradient(listOf(Pink80, Pink40)),
                    shape = RoundedCornerShape(8.dp)
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                )
            ) {
                Text("Удалить", fontSize = (fontSize * 0.9f).sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Pink40
                )
            ) {
                Text("Отмена", fontSize = (fontSize * 0.9f).sp)
            }
        }
    )
}