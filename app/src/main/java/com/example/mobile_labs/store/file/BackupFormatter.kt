package com.example.mobile_labs.store.file

import com.example.mobile_labs.model.disney.DisneyCharacter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupFormatter {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

    fun format(characters: List<DisneyCharacter>): String = buildString {
        appendLine("=== Резервная копия данных Disney Characters ===")
        appendLine("Дата создания: ${dateFormat.format(Date())}")
        appendLine("Количество записей: ${characters.size}")
        appendLine()
        appendLine("=".repeat(60))
        appendLine()

        characters.forEachIndexed { index, character ->
            appendLine("Запись ${index + 1}:")
            appendLine("  ID: ${character.id}")
            appendLine("  Имя: ${character.name}")
            if (character.films.isNotEmpty()) {
                appendLine("  Фильмы: ${character.films.joinToString(", ")}")
            }
            if (character.tvShows.isNotEmpty()) {
                appendLine("  TV Шоу: ${character.tvShows.joinToString(", ")}")
            }
            if (character.videoGames.isNotEmpty()) {
                appendLine("  Видеоигры: ${character.videoGames.joinToString(", ")}")
            }
            if (character.allies.isNotEmpty()) {
                appendLine("  Союзники: ${character.allies.joinToString(", ")}")
            }
            if (character.enemies.isNotEmpty()) {
                appendLine("  Враги: ${character.enemies.joinToString(", ")}")
            }
            if (!character.imageUrl.isNullOrEmpty()) {
                appendLine("  Изображение: ${character.imageUrl}")
            }
            appendLine("  URL: ${character.url}")
            appendLine()
            appendLine("-".repeat(60))
            appendLine()
        }
    }
}

