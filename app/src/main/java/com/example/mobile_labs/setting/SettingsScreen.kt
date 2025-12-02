package com.example.mobile_labs.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.mobile_labs.store.DisneyCacheManager
import com.example.mobile_labs.store.dataStore.SettingsDataStore
import com.example.mobile_labs.store.file.ExternalFileStorage
import com.example.mobile_labs.store.file.InternalFileStorage
import com.example.mobile_labs.store.sharedPref.SharedPreferences
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // DataStore для fontSize
    val dataStore = remember { SettingsDataStore(context) }
    val fontSize by dataStore.currentFontSize.collectAsState(initial = 16f)

    // SharedPreferences для уведомлений
    val sharedPrefs = remember { SharedPreferences(context) }
    var notificationsEnabled by remember { mutableStateOf(sharedPrefs.notification) }

    // Хранилища
    val internalStorage = remember { InternalFileStorage(context) }
    val externalStorage = remember { ExternalFileStorage(context) }

    // Внешний файл резервной копии
    var externalFileInfo by remember {
        mutableStateOf(externalStorage.getFileInfo("disney_backup"))
    }

    // Флаг наличия внутреннего бэкапа
    var backupAvailable by remember {
        mutableStateOf(internalStorage.backupExists("disney_backup"))
    }

    SettingsUi(
        notificationsEnabled = notificationsEnabled,
        fontSize = fontSize,
        onNotificationsChange = {
            notificationsEnabled = it
            sharedPrefs.setNotification(it)
        },
        onFontSizeChange = { size ->
            scope.launch { dataStore.setFontSize(size) }
        },
        onBackClick = onBackClick,

        externalFileInfo = externalFileInfo,
        backupAvailable = backupAvailable,

        onCreateBackupClick = {
            val characters = DisneyCacheManager.getCharacters()

            // Сохраняем во внутреннем хранилище
            if (internalStorage.saveBackup(characters, "disney_backup")) {
                backupAvailable = true
            }

            // Сохраняем на внешнем хранилище
            if (externalStorage.saveDisneyCharacters(characters, "disney_backup")) {
                // Обновляем информацию о внешнем файле
                externalFileInfo = externalStorage.getFileInfo("disney_backup")
            }
        },

        // --- RESTORE TO EXTERNAL ---
        onRestoreClick = {
            val characters = internalStorage.readBackup("disney_backup")
            if (characters != null) {
                // Восстанавливаем на внешнем хранилище
                externalStorage.saveDisneyCharacters(characters, "disney_backup")
                externalFileInfo = externalStorage.getFileInfo("disney_backup")
            }
        },

        // --- DELETE INTERNAL BACKUP ---
        onDeleteBackupClick = {
            if (internalStorage.deleteBackup("disney_backup")) {
                backupAvailable = false
            }
        }
    )
}


