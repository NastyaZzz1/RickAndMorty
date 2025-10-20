package com.nastya.rickandmorty.domain.model.characters

import com.nastya.rickandmorty.data.remote.api.ApiService

data class EpisodeDTO (
    val info: ApiService,
    val results: List<EpisodeResDTO>,
)

data class EpisodeResDTO (
    val id: Int,
    val name: String,
    val air_date: String,
    val episode: String,
    val characters: List<String>,
    val url: String,
    val created: String,
)