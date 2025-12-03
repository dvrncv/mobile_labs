package com.example.mobile_labs.store.file

import android.content.Context
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class InternalFileStorage(
    private val context: Context,
    private val fileName: String,
) {

    fun <T> writeToFile(value: T, serializer: KSerializer<T>): Boolean = runCatching {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).bufferedWriter().use { writer ->
            writer.write(Json.encodeToString(serializer, value))
        }
        true
    }.getOrDefault(false)

    fun <T> readFromFile(serializer: KSerializer<T>): T? = runCatching {
        context.openFileInput(fileName).bufferedReader().use { reader ->
            Json.decodeFromString(serializer, reader.readText())
        }
    }.getOrNull()

    fun writeText(content: String): Boolean = runCatching {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).bufferedWriter().use { writer ->
            writer.write(content)
        }
        true
    }.getOrDefault(false)

    fun readText(): String? = runCatching {
        context.openFileInput(fileName).bufferedReader().use { it.readText() }
    }.getOrNull()

    fun fileExists(): Boolean = context.getFileStreamPath(fileName)
        .exists()

    fun deleteFile(): Boolean = context.deleteFile(fileName)
}