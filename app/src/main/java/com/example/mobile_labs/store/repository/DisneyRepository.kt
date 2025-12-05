package com.example.mobile_labs.store.repository

import android.content.Context
import com.example.mobile_labs.network.ktor.KtorDisneyApi
import com.example.mobile_labs.store.file.FormatBackup
import com.example.mobile_labs.store.file.FileInfo
import com.example.mobile_labs.store.file.ExternalFileStorage
import com.example.mobile_labs.store.file.InternalFileStorage
import kotlinx.coroutines.delay

class DisneyRepository(
    private val context: Context,
    private val userNumber: Int
) {
    private val backupFileName = "${userNumber}_disney_backup.txt"

    private val internalBackupStorage = InternalFileStorage(
        context = context,
        fileName = backupFileName
    )

    private val externalStorage = ExternalFileStorage(
        context = context,
        fileName = backupFileName
    )

    fun hasInternalBackup(): Boolean = internalBackupStorage.fileExists()

    fun getExternalFileInfo(): FileInfo? {
        return externalStorage.getFileInfo()
    }

    suspend fun createBackup(): BackupResult {
        return try {
            val characters = KtorDisneyApi.getCharacters(351..400).getOrNull() ?: emptyList()

            if (characters.isEmpty()) {
                return BackupResult.Error("Нет данных для создания резервной копии")
            }

            val formattedText = FormatBackup.format(characters, userNumber)
            val externalSaved = externalStorage.writeText(formattedText)
            if (!externalSaved) {
                return BackupResult.Error("Не удалось сохранить во внешнее хранилище")
            }

            delay(100)

            BackupResult.Success(
                externalFileInfo = externalStorage.getFileInfo(),
                hasInternalBackup = internalBackupStorage.fileExists()
            )
        } catch (e: Exception) {
            BackupResult.Error("Ошибка при создании резервной копии: ${e.message}")
        }
    }

    suspend fun restoreBackup(): BackupResult {
        return try {
            val backupText = internalBackupStorage.readText()
            if (backupText.isNullOrBlank()) {
                return BackupResult.Error("Резервная копия не найдена во внутреннем хранилище")
            }

            val externalSaved = externalStorage.writeText(backupText)
            if (!externalSaved) {
                return BackupResult.Error("Не удалось восстановить во внешнее хранилище")
            }

            internalBackupStorage.deleteFile()

            delay(100)

            BackupResult.Success(
                externalFileInfo = externalStorage.getFileInfo(),
                hasInternalBackup = internalBackupStorage.fileExists()
            )
        } catch (e: Exception) {
            BackupResult.Error("Ошибка при восстановлении резервной копии: ${e.message}")
        }
    }

    fun deleteExternalBackup(): BackupResult {
        return try {
            val backupText = externalStorage.readText()
            if (backupText.isNullOrBlank()) {
                return BackupResult.Error("Резервная копия не найдена во внешнем хранилище")
            }

            val internalSaved = internalBackupStorage.writeText(backupText)
            if (!internalSaved) {
                return BackupResult.Error("Не удалось сохранить во внутреннее хранилище")
            }

            val deleted = externalStorage.deleteFile()
            if (!deleted) {
                return BackupResult.Error("Не удалось удалить внешний файл")
            }

            BackupResult.Success(
                externalFileInfo = null,
                hasInternalBackup = internalBackupStorage.fileExists()
            )
        } catch (e: Exception) {
            BackupResult.Error("Ошибка при удалении резервной копии: ${e.message}")
        }
    }
}

sealed class BackupResult {
    data class Success(
        val externalFileInfo: FileInfo?,
        val hasInternalBackup: Boolean
    ) : BackupResult()

    data class Error(val message: String) : BackupResult()
}

