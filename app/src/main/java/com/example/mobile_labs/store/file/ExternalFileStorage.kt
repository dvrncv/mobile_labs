package com.example.mobile_labs.store.file

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

private const val TAG = "ExternalFileStorage"

class ExternalFileStorage(
    context: Context,
    fileName: String,
    private val resolver: ContentResolver = context.contentResolver,
    private val directoryUri: Uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI,
) {

    private val fileName = "$fileName.txt"

    // Найти существующий файл
    private fun findFile(): Uri? {
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        resolver.query(
            directoryUri,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                return Uri.withAppendedPath(directoryUri, id.toString())
            }
        }
        return null
    }

    // Создать файл
    private fun createFile(): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
        }
        return resolver.insert(directoryUri, values)
    }

    // Записать данные в файл
    fun <T> writeToFile(value: T, serializer: KSerializer<T>) {
        val jsonString = Json.encodeToString(serializer, value)
        val uri = findFile() ?: createFile()
        if (uri == null) {
            Log.e(TAG, "Failed to write to file. Uri is null")
            return
        }

        resolver.openOutputStream(uri)?.bufferedWriter().use { writer ->
            writer?.write(jsonString)
        }
    }

    // Прочитать данные
    fun <T> readFromFile(serializer: KSerializer<T>): T? {
        val uri = findFile()
        if (uri == null) {
            Log.e(TAG, "Failed to read the file. Uri is null")
            return null
        }

        return resolver.openInputStream(uri)?.bufferedReader().use { reader ->
            reader?.readText()?.let { Json.decodeFromString(serializer, it) }
        }
    }

    // Удалить файл
    fun deleteFile(): Boolean {
        val uri = findFile() ?: return false
        return try {
            resolver.delete(uri, null, null) > 0
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete file: ${e.message}")
            false
        }
    }

    // Получить размер файла
    fun getFileSize(): Long {
        val uri = findFile() ?: return 0
        return resolver.query(uri, arrayOf(MediaStore.MediaColumns.SIZE), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getLong(0) else 0L
        } ?: 0L
    }

    // Получить дату последнего изменения
    fun getFileModifiedDate(): Long {
        val uri = findFile() ?: return 0
        return resolver.query(uri, arrayOf(MediaStore.MediaColumns.DATE_MODIFIED), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getLong(0) * 1000 else 0L
        } ?: 0L
    }
}
