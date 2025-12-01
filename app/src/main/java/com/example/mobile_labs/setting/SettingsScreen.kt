package com.example.mobile_labs.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.mobile_labs.store.dataStore.SettingsDataStore
import com.example.mobile_labs.store.sharedPref.SharedPreferences
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val dataStore = remember { SettingsDataStore(context) }
    val fontSize by dataStore.currentFontSize.collectAsState(initial = 16f)

    val sharedPrefs = remember { SharedPreferences(context) }
    var notificationsEnabled by remember { mutableStateOf(sharedPrefs.notification) }

    SettingsUi(
        notificationsEnabled = notificationsEnabled,
        fontSize = fontSize,
        onNotificationsChange = { enabled ->
            notificationsEnabled = enabled
            sharedPrefs.setNotification(enabled)
        },
        onFontSizeChange = { newSize ->
            scope.launch { dataStore.setFontSize(newSize) }
        },
        onBackClick = onBackClick
    )
}
