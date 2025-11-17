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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding)
            .height(50.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Pink80, Pink40)
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun DisneyCharacterCard(
    character: DisneyCharacter,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Pink80, Pink40)
                    ),
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
                        fontSize = 22.sp,
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

                CharacterInfoBlock(character)
            }
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    items: List<String>
) {
    if (items.isNotEmpty()) {
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 15.sp,
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(Modifier.height(4.dp))

        items.forEach {
            Text(
                text = "- $it",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 6.dp, bottom = 2.dp)
            )
        }

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
fun CharacterInfoBlock(character: DisneyCharacter) {
    Column(modifier = Modifier.padding(16.dp)) {
        InfoSection("Фильмы", character.films)
        InfoSection("ТВ-шоу", character.tvShows)
        InfoSection("Игры", character.videoGames)
        InfoSection("Короткометражки", character.shortFilms)
        InfoSection("Аттракционы", character.parkAttractions)
        InfoSection("Союзники", character.allies)
        InfoSection("Враги", character.enemies)

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
                fontSize = 14.sp
            )
        }
    }
}


