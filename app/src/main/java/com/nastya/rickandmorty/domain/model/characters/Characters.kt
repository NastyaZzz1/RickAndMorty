package com.nastya.rickandmorty.domain.model.characters

import com.nastya.rickandmorty.data.remote.model.ApiResponse

data class CharactersDTO (
    val info: ApiResponse,
    val results: List<CharactersResDTO>,
)

data class CharactersResDTO (
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: Origin,
    val location: Location,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String,
)

data class Location (
    val name: String,
    val url: String,
)

data class Origin (
    val name: String,
    val url: String,
)