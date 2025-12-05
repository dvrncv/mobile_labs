package com.example.mobile_labs.store.file

import android.content.Context

class InternalFileStorage(
    private val context: Context,
    private val fileName: String,
) {

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