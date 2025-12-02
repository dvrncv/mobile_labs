package com.example.mobile_labs.store.file

import java.text.SimpleDateFormat
import java.util.*

data class FileInfo(
    val name: String,
    val size: Long,
    val dateModified: Date,
    val path: String
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