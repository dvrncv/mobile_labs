package com.example.mobile_labs.store.repository

import android.content.Context
import com.example.mobile_labs.model.disney.DisneyCharacter
import com.example.mobile_labs.store.file.ExternalFileStorage
import com.example.mobile_labs.store.file.InternalFileStorage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class BackupData(
    val characters: List<DisneyCharacter>,
    val timestamp: Long,
    val fileName: String
)

class FileRepository(private val context: Context, private val userId: String = "default") {

    private val externalStorage = ExternalFileStorage(context, "backup_$userId")
    private val internalStorage = InternalFileStorage(context, "internal_backup_$userId.txt")
    private val json = Json { prettyPrint = true }

    // Создание файла с данными
    fun createBackup(characters: List<DisneyCharacter>): Boolean {
        return try {
            val backupData = BackupData(
                characters = characters,
                timestamp = System.currentTimeMillis(),
                fileName = "backup_$userId"
            )

            // Сохраняем во внешнее хранилище
            externalStorage.writeToFile(backupData, BackupData.serializer())

            // Также сохраняем во внутреннее хранилище как резервную копию
            internalStorage.writeToFile(backupData, BackupData.serializer())

            true
        } catch (e: Exception) {
            false
        }
    }

    // Получение информации о внешнем файле
    fun getExternalFileInfo(): FileInfo {
        return try {
            val backupData = externalStorage.readFromFile(BackupData.serializer())
            if (backupData != null) {
                FileInfo(
                    exists = true,
                    name = "${backupData.fileName}.txt",
                    size = externalStorage.getFileSize(),
                    created = backupData.timestamp,
                    modified = externalStorage.getFileModifiedDate(),
                    characterCount = backupData.characters.size
                )
            } else {
                FileInfo(exists = false)
            }
        } catch (e: Exception) {
            FileInfo(exists = false)
        }
    }

    // Проверка наличия резервной копии во внутреннем хранилище
    fun hasInternalBackup(): Boolean {
        return internalStorage.isFileExists()
    }

    // Удаление внешнего файла с сохранением во внутреннем хранилище
    fun deleteExternalFileWithBackup(): Boolean {
        return try {
            // Сначала копируем данные из внешнего во внутреннее хранилище
            val backupData = externalStorage.readFromFile(BackupData.serializer())
            if (backupData != null) {
                internalStorage.writeToFile(backupData, BackupData.serializer())
            }

            // Затем удаляем внешний файл
            externalStorage.deleteFile()
        } catch (e: Exception) {
            false
        }
    }

    // Восстановление из внутреннего хранилища во внешнее
    fun restoreFromInternalBackup(): Boolean {
        return try {
            val backupData = internalStorage.readFromFile(BackupData.serializer())
            if (backupData != null) {
                externalStorage.writeToFile(backupData, BackupData.serializer())
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    // Очистка внутренней резервной копии
    fun clearInternalBackup(): Boolean {
        return internalStorage.deleteFile()
    }

    // Форматирование данных в текстовый формат для .txt файла
    fun formatToText(characters: List<DisneyCharacter>): String {
        return buildString {
            append("Резервная копия персонажей Дисней\n")
            append("Дата создания: ${java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(java.util.Date())}\n")
            append("Количество персонажей: ${characters.size}\n")
            append("=".repeat(50) + "\n\n")

            characters.forEachIndexed { index, character ->
                append("${index + 1}. ${character.name}\n")
                append("   Фильмы: ${character.films?.joinToString() ?: "Не указано"}\n")
                append("   Игроки: ${character.tvShows?.joinToString() ?: "Не указано"}\n")
                append("=".repeat(30) + "\n")
            }
        }
    }
}

data class FileInfo(
    val exists: Boolean,
    val name: String = "",
    val size: Long = 0,
    val created: Long = 0,
    val modified: Long = 0,
    val characterCount: Int = 0
)