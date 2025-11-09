package com.nastya.rickandmorty.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.nastya.rickandmorty.data.local.database.RickAndMortyDatabase
import com.nastya.rickandmorty.data.local.entity.CharacterEntity
import com.nastya.rickandmorty.data.paging.RemoteMediator
import com.nastya.rickandmorty.data.remote.api.ApiService
import kotlinx.coroutines.flow.Flow

class CharacterRepository(
    private val apiService: ApiService,
    private val database: RickAndMortyDatabase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getCharactersStream(
        name: String,
        gender: String,
        status: String,
        species: String,
    ): Flow<PagingData<CharacterEntity>> {

        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = RemoteMediator(
                networkService = apiService,
                database = database,
                name = name,
                status = status,
                gender = gender,
                species = species
            )
        ) {
            database.characterDao.pagingSource()
        }.flow
    }

    suspend fun refreshData() {
        database.withTransaction {
            database.characterDao.clearAll()
            database.remoteKeyDao.clearAll()
        }
    }
}