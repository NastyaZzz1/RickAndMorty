package com.nastya.rickandmorty.data.repository

import com.nastya.rickandmorty.data.remote.api.ApiService
import com.nastya.rickandmorty.domain.model.characters.CharactersResDTO
import com.nastya.rickandmorty.domain.model.characters.EpisodeResDTO
import com.nastya.rickandmorty.domain.model.characters.LocationsResDTO
import retrofit2.Response

class RemoteRepositoryImpl {
    private val apiService: ApiService by lazy {
        ApiService.getApiService()
    }

    suspend fun getOneCharacter(id: Int)
        : Response<CharactersResDTO> = apiService.getOneCharacter(id)

    suspend fun getOneLocation(id: Int)
            : Response<LocationsResDTO> = apiService.getOneLocation(id)

    suspend fun getEpisodeMultiple(urlsId: String)
            : Response<List<EpisodeResDTO>> = apiService.getListDataEpisodeMultiple(urlsId)
}