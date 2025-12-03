package com.example.mobile_labs.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.mobile_labs.model.disney.DisneyCharacter
import com.example.mobile_labs.store.dataStore.SettingsDataStore
import com.example.mobile_labs.store.file.BackupFormatter
import com.example.mobile_labs.store.file.ExternalFileInfo
import com.example.mobile_labs.store.file.ExternalFileStorage
import com.example.mobile_labs.store.file.InternalFileStorage
import com.example.mobile_labs.store.sharedPref.SharedPreferences
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    userNumber: Int
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val sharedPreferences = remember { SharedPreferences(context) }

    val externalFileName = remember { "${userNumber}_disney_backup.txt" }

    val internalDataStorage = remember {
        InternalFileStorage(
            context = context,
            fileName = "disney_data.txt"
        )
    }
    val internalBackupStorage = remember {
        InternalFileStorage(
            context = context,
            fileName = "disney_backup_internal.txt"
        )
    }
    val externalStorage = remember {
        ExternalFileStorage(
            context = context,
            fileName = externalFileName
        )
    }

    var externalFileInfo by remember { mutableStateOf<ExternalFileInfo?>(null) }
    var backupAvailable by remember { mutableStateOf(internalBackupStorage.fileExists()) }
    var isLoading by remember { mutableStateOf(false) }

    val fontSize by settingsDataStore.currentFontSize.collectAsState(initial = 16f)

    var notificationsEnabled by remember { mutableStateOf(sharedPreferences.notification) }

    LaunchedEffect(Unit) {
        externalFileInfo = externalStorage.getFileInfo()
        backupAvailable = internalBackupStorage.fileExists()
    }

    SettingsUi(
        notificationsEnabled = notificationsEnabled,
        fontSize = fontSize,
        onNotificationsChange = { enabled ->
            notificationsEnabled = enabled
            sharedPreferences.setNotification(enabled)
        },
        onFontSizeChange = { size ->
            scope.launch {
                settingsDataStore.setFontSize(size)
            }
        },
        onBackClick = onBackClick,

        externalFileInfo = externalFileInfo,
        backupAvailable = backupAvailable,
        isLoading = isLoading,

        onCreateBackupClick = {
            scope.launch {
                isLoading = true
                try {
                    val serializer = kotlinx.serialization.builtins.ListSerializer(
                        DisneyCharacter.serializer()
                    )
                    val characters =
                        internalDataStorage.readFromFile(serializer) ?: emptyList<DisneyCharacter>()

                    if (characters.isNotEmpty()) {
                        val formattedText = BackupFormatter.format(characters)
                        if (externalStorage.writeText(formattedText)) {
                            externalFileInfo = externalStorage.getFileInfo()
                        }
                    }
                } finally {
                    isLoading = false
                }
            }
        },

        onRestoreClick = {
            scope.launch {
                isLoading = true
                try {
                    val backupText = internalBackupStorage.readText()
                    if (!backupText.isNullOrBlank()) {
                        if (externalStorage.writeText(backupText)) {
                            externalFileInfo = externalStorage.getFileInfo()
                            backupAvailable = internalBackupStorage.fileExists()
                        }
                    }
                } finally {
                    isLoading = false
                }
            }
        },

        onDeleteBackupClick = {
            scope.launch {
                isLoading = true
                try {
                    val externalText = externalStorage.readText()
                    if (!externalText.isNullOrBlank()) {
                        internalBackupStorage.writeText(externalText)
                    }
                    if (externalStorage.deleteFile()) {
                        externalFileInfo = null
                        backupAvailable = internalBackupStorage.fileExists()
                    }
                } finally {
                    isLoading = false
                }
            }
        }
    )
}





