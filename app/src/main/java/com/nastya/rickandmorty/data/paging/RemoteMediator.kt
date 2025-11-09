package com.nastya.rickandmorty.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import com.nastya.rickandmorty.data.local.database.RickAndMortyDatabase
import com.nastya.rickandmorty.data.remote.api.ApiService
import retrofit2.HttpException
import java.io.IOException
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.nastya.rickandmorty.data.local.entity.CharacterEntity
import com.nastya.rickandmorty.data.local.entity.RemoteKeys
import com.nastya.rickandmorty.domain.model.characters.toCharacterEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class RemoteMediator(
    private val name: String,
    private val status: String,
    private val gender: String,
    private val species: String,
    private val database: RickAndMortyDatabase,
    private val networkService: ApiService
) : RemoteMediator<Int, CharacterEntity>() {
    private val characterDao = database.characterDao
    private val remoteKeysDao = database.remoteKeyDao

    private val STARTING_PAGE_INDEX = 1

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRefreshRemoteKey(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = false)
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        try {
            val response = networkService.getListDataCharacter(
                page,
                name = name,
                gender = gender,
                status = status,
                species = species
            )

            val endOfList = response.body()?.info?.next == null
            database.withTransaction {
                if(loadType == LoadType.REFRESH){
                    remoteKeysDao.clearAll()
                    characterDao.clearAll()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page-1
                val nextKey = if(endOfList) null else page+1
                response.body()?.let {
                    val keys = response.body()!!.results.map {
                        RemoteKeys(it.id,prevKey,nextKey)
                    }
                    val characters = response.body()!!.results.map { it.toCharacterEntity() }
                    remoteKeysDao.insertRemote(keys)
                    characterDao.insert(characters)
                }

            }
            return MediatorResult.Success(endOfPaginationReached = endOfList)
        }catch (e:IOException){
            return   MediatorResult.Error(e)
        }catch (e:HttpException){
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, CharacterEntity>) : RemoteKeys? {
        return withContext(Dispatchers.IO){
            state.pages
                .firstOrNull { it.data.isNotEmpty() }
                ?.data?.firstOrNull()
                ?.let { character -> remoteKeysDao.remoteKeysByCharacterId(character.id)}
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, CharacterEntity>) : RemoteKeys? {
        return withContext(Dispatchers.IO){
            state.pages
                .lastOrNull{it.data.isNotEmpty()}
                ?.data?.lastOrNull()
                ?.let { character -> remoteKeysDao.remoteKeysByCharacterId(character.id) }
        }
    }

    private suspend fun getRefreshRemoteKey(state: PagingState<Int, CharacterEntity>) : RemoteKeys? {
        return withContext(Dispatchers.IO){
            state.anchorPosition?.let { position->
                state.closestItemToPosition(position)?.id?.let {repId ->
                    remoteKeysDao.remoteKeysByCharacterId(repId)
                }
            }
        }
    }
}