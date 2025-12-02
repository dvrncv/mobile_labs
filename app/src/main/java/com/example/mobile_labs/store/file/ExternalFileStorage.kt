package com.example.mobile_labs.store.file

import android.content.Context
import android.os.Environment
import com.example.mobile_labs.model.disney.DisneyCharacter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*

data class ExternalFileInfo(
    val name: String,
    val size: Long,
    val path: String,
    val modified: Long
) {
    val formattedSize: String
        get() = when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${String.format("%.1f", size / 1024.0)} KB"
            else -> "${String.format("%.1f", size / (1024.0 * 1024.0))} MB"
        }

    val formattedDate: String
        get() = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            .format(Date(modified))
}

class ExternalBackupManager(private val context: Context) {

    fun saveToExternal(characters: List<DisneyCharacter>, fileName: String): Boolean {
        return try {
            val json = Json.encodeToString<List<DisneyCharacter>>(characters)
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "$fileName.txt")
            file.writeText(json)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    fun getFileInfo(fileName: String): ExternalFileInfo? {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "$fileName.txt")
        return if (file.exists()) {
            ExternalFileInfo(file.name, file.length(), file.absolutePath, file.lastModified())
        } else null
    }

    fun deleteExternal(fileName: String): Boolean {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "$fileName.txt")
        return file.exists() && file.delete()
    }

    fun fileExists(fileName: String): Boolean {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "$fileName.txt")
        return file.exists()
    }
}
