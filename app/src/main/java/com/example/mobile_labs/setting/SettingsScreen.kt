package com.example.mobile_labs.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.mobile_labs.model.disney.DisneyCharacter
import com.example.mobile_labs.store.DisneyRepository
import com.example.mobile_labs.store.file.ExternalBackupManager
import com.example.mobile_labs.store.file.InternalBackupManager
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    repository: DisneyRepository,
    characters: List<DisneyCharacter>
) {
    val scope = rememberCoroutineScope()

    var externalFileInfo by remember { mutableStateOf(repository.getExternalFileInfo("disney_backup")) }
    var backupAvailable by remember { mutableStateOf(repository.internalBackupExists()) }

    SettingsUi(
        notificationsEnabled = false,
        fontSize = 16f,
        onNotificationsChange = {},
        onFontSizeChange = {},
        onBackClick = onBackClick,

        externalFileInfo = externalFileInfo,
        backupAvailable = backupAvailable,

        onCreateBackupClick = {
            if (repository.saveExternalBackup(characters, "disney_backup")) {
                externalFileInfo = repository.getExternalFileInfo("disney_backup")
                backupAvailable = repository.internalBackupExists()
            }
        },

        onRestoreClick = {
            scope.launch {
                if (repository.restoreInternalBackup()) {
                    externalFileInfo = repository.getExternalFileInfo("disney_backup")
                }
            }
        },

        onDeleteBackupClick = {
            externalFileInfo?.let {
                val file = java.io.File(it.path)
                scope.launch {
                    repository.saveInternalBackupFromFile(file)
                    file.delete()
                    externalFileInfo = null
                    backupAvailable = repository.internalBackupExists()
                }
            }
        }
    )
}





