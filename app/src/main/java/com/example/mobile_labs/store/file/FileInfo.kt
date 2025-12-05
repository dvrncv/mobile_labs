package com.example.mobile_labs.store.file

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FileInfo(
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

