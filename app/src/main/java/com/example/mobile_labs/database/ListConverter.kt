package com.example.mobile_labs.database

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class ListConverter {
    private val json = Json { ignoreUnknownKeys = true }
    
    @TypeConverter
    fun fromStringList(value: String): List<String> {
        return if (value.isEmpty()) {
            emptyList()
        } else {
            try {
                json.decodeFromString<List<String>>(value)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    @TypeConverter
    fun toStringList(value: List<String>): String {
        return if (value.isEmpty()) {
            ""
        } else {
            try {
                json.encodeToString(value)
            } catch (e: Exception) {
                ""
            }
        }
    }
}

