package com.example.mobile_labs.store.file

import android.content.Context
import android.util.Log
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File

private const val TAG = "InternalFileStorage"

class InternalFileStorage(
    private val context: Context,
    private val fileName: String,
) {
    fun isFileExists(): Boolean = File(context.filesDir, fileName).exists()

    fun <T> writeToFile(value: T, serializer: KSerializer<T>): Boolean {
        try {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) {
                file.createNewFile()
            }

            file.writer().use { writer ->
                writer.write(Json.encodeToString(serializer, value))
            }

            return true
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to write to internal file")
            return false
        }
    }

    fun <T> readFromFile(serializer: KSerializer<T>): T? {
        try {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) {
                return null
            }

            file.reader().use { reader ->
                return Json.decodeFromString(serializer, reader.readText())
            }
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to read the internal file")
            return null
        }
    }

    fun deleteFile(): Boolean = File(context.filesDir, fileName).delete()
}