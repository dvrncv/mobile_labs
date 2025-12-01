package com.example.mobile_labs.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile_labs.R
import com.example.mobile_labs.ui.theme.Pink40
import com.example.mobile_labs.ui.theme.Pink80
import androidx.compose.material.icons.filled.Notifications

@Composable
fun SettingsUi(
    notificationsEnabled: Boolean,
    fontSize: Float,
    onNotificationsChange: (Boolean) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.fon1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {
            SettingsTopBar(
                title = "Настройки",
                onBackClick = onBackClick,
                fontSize = fontSize
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FontSizeSection(
                    fontSize = fontSize,
                    onFontSizeChange = onFontSizeChange
                )

                NotificationSection(
                    notificationsEnabled = notificationsEnabled,
                    onNotificationsChange = onNotificationsChange,
                    fontSize = fontSize
                )
            }
        }
    }
}

@Composable
private fun FontSizeSection(
    fontSize: Float,
    onFontSizeChange: (Float) -> Unit
) {
    val fontSizeName = remember(fontSize) {
        getFontSizeName(fontSize)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Pink80.copy(alpha = 0.2f),
                            Pink40.copy(alpha = 0.2f)
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.FormatSize,
                    contentDescription = "Размер шрифта",
                    tint = Pink40,
                    modifier = Modifier.size((fontSize + 4).dp)
                )

                Text(
                    text = "Размер шрифта",
                    fontSize = fontSize.sp,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.3f),
                            offset = Offset(1f, 1f),
                            blurRadius = 3f
                        )
                    )
                )
            }

            Slider(
                value = fontSize,
                onValueChange = onFontSizeChange,
                valueRange = 12f..32f,
                steps = 5,
                colors = SliderDefaults.colors(
                    thumbColor = Pink40,
                    activeTrackColor = Pink40,
                    inactiveTrackColor = Pink80,
                    activeTickColor = Pink80,
                    inactiveTickColor = Pink40
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Pink80, Pink40)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = fontSizeName,
                        fontSize = fontSize.sp,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.3f),
                                offset = Offset(1f, 1f),
                                blurRadius = 3f
                            )
                        )
                    )

                    Text(
                        text = "${fontSize.toInt()}",
                        fontSize = fontSize.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


@Composable
private fun NotificationSection(
    notificationsEnabled: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    fontSize: Float
) {
    val switchScale = (fontSize / 16f).coerceIn(0.75f, 1.25f)
    val textSize = fontSize.sp
    val iconSize = fontSize.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Уведомления",
                tint = Pink40,
                modifier = Modifier.size(iconSize)
            )

            Text(
                text = "Уведомления",
                fontSize = textSize,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.3f),
                        offset = Offset(1f, 1f),
                        blurRadius = 3f
                    )
                )
            )
        }

        Box(
            modifier = Modifier.scale(switchScale)
        ) {
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = onNotificationsChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Pink80,
                    checkedTrackColor = Pink40,
                    uncheckedThumbColor = Pink40,
                    uncheckedTrackColor = Pink80
                )
            )
        }
    }
}

private fun getFontSizeName(fontSize: Float): String {
    return when {
        fontSize < 14f -> "Мелкий"
        fontSize < 18f -> "Средний"
        fontSize < 22f -> "Средне-крупный"
        fontSize < 26f -> "Крупный"
        fontSize < 30f -> "Очень крупный"
        else -> "Гигантский"
    }
}




@Composable
fun SettingsTopBar(
    title: String,
    onBackClick: () -> Unit,
    fontSize: Float
) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding)
            .height(fontSize.dp.coerceAtLeast(56.dp))
            .background(
                brush = Brush.horizontalGradient(listOf(Pink80, Pink40))
            )
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = Color.White,
                modifier = Modifier.size(fontSize.dp.coerceIn(28.dp, 40.dp))
            )
        }


        Text(
            text = title,
            fontSize = fontSize.sp,
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


        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(fontSize.dp.coerceIn(28.dp, 40.dp))
        )
    }
}