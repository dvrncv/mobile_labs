package com.example.mobile_labs.store.file

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ExternalFileInfo(
    val name: String,
    val size: Long,
    val path: String,
    val modified: Long
) {
    val formattedSize: String
        get() = when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> String.format(Locale.getDefault(), "%.1f KB", size / 1024.0)
            else -> String.format(Locale.getDefault(), "%.1f MB", size / (1024.0 * 1024.0))
        }

    val formattedDate: String
        get() = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(modified))
}

class ExternalFileStorage(
    private val context: Context,
    private val fileName: String,
    private val directoryUri: Uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
) {

    private val resolver = context.contentResolver

    private fun findFileUri(): Uri? {
        val projection = arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        resolver.query(
            directoryUri,
            projection,
            selection,
            selectionArgs,
            null
        ).use { cursor ->
            return if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                Uri.withAppendedPath(directoryUri, id.toString())
            } else {
                null
            }
        }
    }

    private fun createFile(): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
        }

        return resolver.insert(directoryUri, values)
    }

    private fun getOrCreateFile(): Uri? = findFileUri() ?: createFile()

    fun writeText(content: String): Boolean = runCatching {
        val uri = requireNotNull(getOrCreateFile())
        resolver.openOutputStream(uri, "rwt")?.bufferedWriter().use { writer ->
            writer?.apply {
                write(content)
                flush()
            }
        }
        true
    }.getOrDefault(false)

    fun readText(): String? = runCatching {
        val uri = findFileUri() ?: return null
        resolver.openInputStream(uri)?.bufferedReader().use { reader ->
            reader?.readText()
        }
    }.getOrNull()

    fun getFileInfo(): ExternalFileInfo? {
        val uri = findFileUri() ?: return null
        val document = DocumentFile.fromSingleUri(context, uri)

        val name = document?.name ?: fileName
        val size = document?.length() ?: 0L
        val modified = document?.lastModified() ?: System.currentTimeMillis()

        return ExternalFileInfo(
            name = name,
            size = size,
            path = uri.toString(),
            modified = modified
        )
    }

    fun fileExists(): Boolean = findFileUri() != null

    fun deleteFile(): Boolean = runCatching {
        val uri = findFileUri() ?: return false
        resolver.delete(uri, null, null) > 0
    }.getOrDefault(false)
}
