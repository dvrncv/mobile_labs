package com.example.mobile_labs.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mobile_labs.model.disney.DisneyCharacter

@Entity(tableName = "characters")
@TypeConverters(ListConverter::class)
data class DisneyCharacterEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val films: List<String>,
    val shortFilms: List<String>,
    val tvShows: List<String>,
    val videoGames: List<String>,
    val parkAttractions: List<String>,
    val allies: List<String>,
    val enemies: List<String>,
    val imageUrl: String?,
    val url: String
) {
    fun toDisneyCharacter(): DisneyCharacter {
        return DisneyCharacter(
            id = id,
            name = name,
            films = films,
            shortFilms = shortFilms,
            tvShows = tvShows,
            videoGames = videoGames,
            parkAttractions = parkAttractions,
            allies = allies,
            enemies = enemies,
            imageUrl = imageUrl,
            url = url
        )
    }

    companion object {
        fun fromDisneyCharacter(character: DisneyCharacter): DisneyCharacterEntity {
            return DisneyCharacterEntity(
                id = character.id,
                name = character.name,
                films = character.films,
                shortFilms = character.shortFilms,
                tvShows = character.tvShows,
                videoGames = character.videoGames,
                parkAttractions = character.parkAttractions,
                allies = character.allies,
                enemies = character.enemies,
                imageUrl = character.imageUrl,
                url = character.url
            )
        }
    }
}


