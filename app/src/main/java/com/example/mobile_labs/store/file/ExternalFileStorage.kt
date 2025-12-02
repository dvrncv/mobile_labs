package com.example.mobile_labs.store.file

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.mobile_labs.model.disney.DisneyCharacter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*

class ExternalFileStorage(private val context: Context) {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    fun saveDisneyCharacters(characters: List<DisneyCharacter>, fileName: String): Boolean {
        return try {
            val backupData = DisneyBackupData(
                characters = characters,
                timestamp = System.currentTimeMillis(),
                totalCount = characters.size,
                appVersion = "1.0.0",
                formatVersion = 1
            )

            val jsonContent = json.encodeToString(backupData)

            val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            if (!documentsDir.exists()) {
                documentsDir.mkdirs()
            }

            val file = File(documentsDir, "$fileName.txt")
            file.writeText(jsonContent)

            Log.d("ExternalFileStorage", "Файл сохранен в формате JSON: ${file.absolutePath}")
            Log.d("ExternalFileStorage", "Размер JSON: ${jsonContent.length} символов")
            true
        } catch (e: Exception) {
            Log.e("ExternalFileStorage", "Ошибка сохранения JSON", e)
            false
        }
    }

    fun readDisneyCharacters(fileName: String): List<DisneyCharacter>? {
        return try {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "$fileName.txt"
            )

            if (!file.exists()) {
                Log.d("ExternalFileStorage", "Файл не найден: $fileName.txt")
                return null
            }

            val jsonContent = file.readText()
            val backupData = json.decodeFromString<DisneyBackupData>(jsonContent)

            Log.d("ExternalFileStorage", "Прочитано из JSON: ${backupData.characters.size} персонажей")
            backupData.characters
        } catch (e: Exception) {
            Log.e("ExternalFileStorage", "Ошибка чтения JSON", e)
            null
        }
    }

    fun getFileInfo(fileName: String): FileInfo? {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "$fileName.txt"
        )

        return if (file.exists()) {
            FileInfo(
                name = "$fileName.txt",
                size = file.length(),
                dateModified = Date(file.lastModified()),
                path = file.absolutePath
            )
        } else {
            null
        }
    }

    fun deleteFile(fileName: String): Boolean {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "$fileName.txt"
        )
        return file.delete()
    }

    fun fileExists(fileName: String): Boolean {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "$fileName.txt"
        )
        return file.exists()
    }
}

@kotlinx.serialization.Serializable
data class DisneyBackupData(
    val characters: List<DisneyCharacter>,
    val timestamp: Long,
    val totalCount: Int,
    val appVersion: String,
    val formatVersion: Int
)
