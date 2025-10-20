package com.nastya.rickandmorty.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?,
)