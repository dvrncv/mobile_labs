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
import com.example.mobile_labs.store.repository.DisneyRepository
import com.example.mobile_labs.store.repository.BackupResult
import com.example.mobile_labs.store.dataStore.SettingsDataStore
import com.example.mobile_labs.store.file.FileInfo
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

    val backupRepository = remember {
        DisneyRepository(context, userNumber)
    }

    var externalFileInfo by remember { mutableStateOf<FileInfo?>(null) }
    var backupAvailable by remember { mutableStateOf(backupRepository.hasInternalBackup()) }
    var isLoading by remember { mutableStateOf(false) }

    val fontSize by settingsDataStore.currentFontSize.collectAsState(initial = 16f)
    var notificationsEnabled by remember { mutableStateOf(sharedPreferences.notification) }

    LaunchedEffect(Unit) {
        externalFileInfo = backupRepository.getExternalFileInfo()
        backupAvailable = backupRepository.hasInternalBackup()
    }

    fun handleBackupOperation(operation: suspend () -> BackupResult) {
        scope.launch {
            isLoading = true
            try {
                val result = operation()
                if (result is BackupResult.Success) {
                    externalFileInfo = result.externalFileInfo
                    backupAvailable = result.hasInternalBackup
                }
            } finally {
                isLoading = false
            }
        }
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
            handleBackupOperation { backupRepository.createBackup() }
        },
        onRestoreClick = {
            handleBackupOperation { backupRepository.restoreBackup() }
        },
        onDeleteBackupClick = {
            handleBackupOperation { backupRepository.deleteExternalBackup() }
        }
    )
}