package com.nastya.rickandmorty.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nastya.rickandmorty.data.remote.api.ApiService
import com.nastya.rickandmorty.domain.model.characters.CharactersResDTO

class CharacterPagingSource(
    val backend: ApiService,
    val name: String,
    val status: String,
    val gender: String,
    val species: String,
    private val onError: (Throwable) -> Unit
) : PagingSource<Int, CharactersResDTO>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, CharactersResDTO> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = backend.getListDataCharacter(nextPageNumber, name, gender, status, species)
            val responseData = mutableListOf<CharactersResDTO>()
            val data = response.body()?.results ?: emptyList()
            responseData.addAll(data)
            LoadResult.Page(
                data = responseData,
                prevKey = if (nextPageNumber == 1) null else nextPageNumber - 1,
                nextKey = if (response.body()?.info?.next == null) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            onError(e)
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, CharactersResDTO>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}