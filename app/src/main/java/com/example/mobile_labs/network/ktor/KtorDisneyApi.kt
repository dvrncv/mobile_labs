package com.example.mobile_labs.network.ktor

import android.util.Log
import com.example.mobile_labs.model.disney.DisneyApiResponse
import com.example.mobile_labs.model.disney.DisneyCharacter
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorDisneyCharacterApi(
    private val client: HttpClient = defaultClient()
) {

    suspend fun getCharacters(ids: IntRange = 351..400): Result<List<DisneyCharacter>> =
        runCatching {
            coroutineScope {
                ids.map { id ->
                    async {
                        try {
                            val character = client
                                .get("$BASE_URL/$id")
                                .body<DisneyApiResponse<DisneyCharacter>>()
                                .data

                            Log.i(TAG, "Загружен персонаж: ${character.name} (ID: $id)")
                            character

                        } catch (e: Exception) {
                            Log.w(TAG, "Ошибка загрузки ID $id — ${e.message}")
                            null
                        }
                    }
                }.awaitAll().filterNotNull()
            }
        }

    companion object {
        private const val TAG = "DisneyService"
        private const val BASE_URL = "https://api.disneyapi.dev/character"

        private fun defaultClient() = HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d(TAG, "Ktor → $message")
                    }
                }
                level = LogLevel.ALL
            }
        }
    }
}
