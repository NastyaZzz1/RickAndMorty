package com.nastya.rickandmorty.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nastya.rickandmorty.data.paging.CharacterPagingSource
import com.nastya.rickandmorty.data.paging.EpisodePagingSource
import com.nastya.rickandmorty.data.paging.LocationPagingSource
import com.nastya.rickandmorty.data.remote.api.ApiService
import com.nastya.rickandmorty.domain.model.characters.CharactersResDTO
import com.nastya.rickandmorty.domain.model.characters.EpisodeResDTO
import com.nastya.rickandmorty.domain.model.characters.LocationsResDTO
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class RemoteRepositoryImpl {
    private val apiService: ApiService by lazy {
        ApiService.getApiService()
    }

    fun getCharactersStream(
        name: String,
        onError: (Throwable) -> Unit
    )
        : Flow<PagingData<CharactersResDTO>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { CharacterPagingSource(apiService, name, onError) }
        ).flow
    }

    suspend fun getOneCharacter(id: Int)
        : Response<CharactersResDTO> = apiService.getOneCharacter(id)

    fun getLocationsStream(name: String)
            : Flow<PagingData<LocationsResDTO>> {
        return Pager(
            config = PagingConfig(pageSize = 1),
            pagingSourceFactory = { LocationPagingSource(apiService, name) }
        ).flow
    }

    suspend fun getOneLocation(id: Int)
            : Response<LocationsResDTO> = apiService.getOneLocation(id)

    fun getEpisodesStream(name: String)
            : Flow<PagingData<EpisodeResDTO>> {
        return Pager(
            config = PagingConfig(pageSize = 1),
            pagingSourceFactory = { EpisodePagingSource(apiService, name) }
        ).flow
    }

    suspend fun getEpisodeMultiple(urlsId: String)
            : Response<List<EpisodeResDTO>> = apiService.getListDataEpisodeMultiple(urlsId)
}