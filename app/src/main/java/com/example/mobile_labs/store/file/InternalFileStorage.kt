package com.example.mobile_labs.store.file

import android.content.Context
import android.util.Log
import com.example.mobile_labs.model.disney.DisneyCharacter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
class InternalFileStorage(private val context: Context) {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    fun saveBackup(characters: List<DisneyCharacter>, fileName: String): Boolean {
        return try {
            val backupData = DisneyBackupData(
                characters = characters,
                timestamp = System.currentTimeMillis(),
                totalCount = characters.size,
                appVersion = "1.0.0",
                formatVersion = 1
            )

            val jsonString = json.encodeToString(backupData)
            val file = File(context.filesDir, "backup_$fileName.json")

            file.writeText(jsonString)

            Log.d("InternalFileStorage", "Резервная копия сохранена в JSON: ${file.absolutePath}")
            Log.d("InternalFileStorage", "Размер JSON: ${jsonString.length} символов, персонажей: ${characters.size}")
            true
        } catch (e: Exception) {
            Log.e("InternalFileStorage", "Ошибка сохранения резервной копии JSON", e)
            false
        }
    }

    fun readBackup(fileName: String): List<DisneyCharacter>? {
        return try {
            val file = File(context.filesDir, "backup_$fileName.json")
            if (!file.exists()) {
                Log.d("InternalFileStorage", "Резервная копия JSON не найдена: backup_$fileName.json")
                return null
            }

            val jsonString = file.readText()
            val backupData = json.decodeFromString<DisneyBackupData>(jsonString)

            Log.d("InternalFileStorage", "Резервная копия загружена из JSON: ${backupData.characters.size} персонажей")
            backupData.characters
        } catch (e: Exception) {
            Log.e("InternalFileStorage", "Ошибка чтения резервной копии JSON", e)
            null
        }
    }

    fun backupExists(fileName: String): Boolean {
        val file = File(context.filesDir, "backup_$fileName.json")
        return file.exists()
    }

    fun deleteBackup(fileName: String): Boolean {
        val file = File(context.filesDir, "backup_$fileName.json")
        return try {
            val deleted = file.delete()
            if (deleted) {
                Log.d("InternalFileStorage", "Резервная копия JSON удалена: backup_$fileName.json")
            }
            deleted
        } catch (e: Exception) {
            Log.e("InternalFileStorage", "Ошибка удаления резервной копии JSON", e)
            false
        }
    }

    fun getBackupInfo(fileName: String): BackupInfo? {
        return try {
            val file = File(context.filesDir, "backup_$fileName.json")
            if (!file.exists()) return null

            val backupData = readBackup(fileName)

            BackupInfo(
                name = file.name,
                size = file.length(),
                dateModified = Date(file.lastModified()),
                characterCount = backupData?.size ?: 0
            )
        } catch (e: Exception) {
            null
        }
    }
}

data class BackupInfo(
    val name: String,
    val size: Long,
    val dateModified: Date,
    val characterCount: Int
) {
    val formattedSize: String
        get() = when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${String.format("%.1f", size / 1024.0)} KB"
            else -> "${String.format("%.1f", size / (1024.0 * 1024.0))} MB"
        }

    val formattedDate: String
        get() = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(dateModified)
}
