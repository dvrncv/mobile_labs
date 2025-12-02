package com.example.mobile_labs.store

import com.example.mobile_labs.model.disney.DisneyCharacter
import kotlinx.serialization.Serializable

@Serializable
data class DisneyCache(
    val characters: List<DisneyCharacter>,
    val timestamp: Long = System.currentTimeMillis(),
    val version: Int = 1
)

object DisneyCacheManager {
    private var _cache: DisneyCache? = null

    fun saveCache(characters: List<DisneyCharacter>) {
        _cache = DisneyCache(
            characters = characters,
            timestamp = System.currentTimeMillis()
        )
    }

    fun getCache(): DisneyCache? = _cache

    fun getCharacters(): List<DisneyCharacter> = _cache?.characters ?: emptyList()

    fun clearCache() {
        _cache = null
    }

    fun hasCache(): Boolean = _cache != null

    fun isCacheValid(maxAgeMinutes: Long = 60): Boolean {
        return _cache?.let {
            val cacheAge = System.currentTimeMillis() - it.timestamp
            cacheAge < maxAgeMinutes * 60 * 1000
        } ?: false
    }
}