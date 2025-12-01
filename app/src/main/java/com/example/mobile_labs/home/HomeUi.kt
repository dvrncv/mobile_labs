package com.example.mobile_labs.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mobile_labs.model.disney.DisneyCharacter
import com.example.mobile_labs.ui.theme.Pink40
import com.example.mobile_labs.ui.theme.Pink80

@Composable
fun TopBar(
    title: String,
    onSettingsClick: () -> Unit,
    fontSize: Float
) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding)
            .height(50.dp)
            .background(
                brush = Brush.horizontalGradient(listOf(Pink80, Pink40))
            )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = fontSize.sp,
                color = Color.White,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = Offset(2f,2f),
                    blurRadius = 4f
                )
            ),
            modifier = Modifier.align(Alignment.Center)
        )

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Настройки",
                tint = Color.White,
                modifier = Modifier.size(fontSize.dp)
            )
        }
    }
}

@Composable
fun DisneyCharacterCard(
    character: DisneyCharacter,
    modifier: Modifier = Modifier,
    fontSize: Float
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier.background(
                brush = Brush.verticalGradient(colors = listOf(Pink80, Pink40)),
                shape = RoundedCornerShape(20.dp)
            )
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(character.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = character.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.5f)
                                    ),
                                    startY = 100f
                                )
                            )
                    )

                    Text(
                        text = character.name,
                        color = Color.White,
                        fontSize = fontSize.sp,
                        style = MaterialTheme.typography.titleLarge.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.3f),
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    )
                }

                CharacterInfoBlock(character, fontSize)
            }
        }
    }
}


@Composable
private fun InfoSection(
    title: String,
    items: List<String>,
    fontSize: Float
) {
    if (items.isNotEmpty()) {
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = fontSize.sp,
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(Modifier.height(4.dp))

        items.forEach { item ->
            Text(
                text = "- $item",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = fontSize.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, bottom = 2.dp),
                lineHeight = fontSize.sp
            )
        }

        Spacer(Modifier.height(10.dp))
    }
}


@Composable
fun CharacterInfoBlock(
    character: DisneyCharacter,
    fontSize: Float
) {
    Column(modifier = Modifier.padding(16.dp)) {
        InfoSection("Фильмы", character.films, fontSize)
        InfoSection("ТВ-шоу", character.tvShows, fontSize)
        InfoSection("Игры", character.videoGames, fontSize)
        InfoSection("Короткометражки", character.shortFilms, fontSize)
        InfoSection("Аттракционы", character.parkAttractions, fontSize)
        InfoSection("Союзники", character.allies, fontSize)
        InfoSection("Враги", character.enemies, fontSize)

        if (
            character.films.isEmpty() &&
            character.tvShows.isEmpty() &&
            character.videoGames.isEmpty() &&
            character.shortFilms.isEmpty() &&
            character.parkAttractions.isEmpty() &&
            character.allies.isEmpty() &&
            character.enemies.isEmpty()
        ) {
            Text(
                text = "Информация отсутствует",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = fontSize.sp
            )
        }
    }
}


