package com.example.mobile_labs.model.disney

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisneyCharacter(
    @SerialName("_id")
    val id: Int,

    val name: String,

    val films: List<String> = emptyList(),

    val shortFilms: List<String> = emptyList(),

    val tvShows: List<String> = emptyList(),

    val videoGames: List<String> = emptyList(),

    val parkAttractions: List<String> = emptyList(),

    val allies: List<String> = emptyList(),

    val enemies: List<String> = emptyList(),

    val imageUrl: String? = null,

    val url: String
)

@Serializable
data class DisneyApiResponse<T>(
    val data: T
)