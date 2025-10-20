package com.nastya.rickandmorty.domain.model.characters

import com.nastya.rickandmorty.data.remote.api.ApiService

data class LocationsDTO(
    val info: ApiService,
    val results: List<LocationsResDTO>,
)

data class LocationsResDTO (
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: List<String>,
    val url: String,
    val created: String,
)