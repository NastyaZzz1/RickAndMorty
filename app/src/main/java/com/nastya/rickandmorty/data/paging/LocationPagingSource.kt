package com.nastya.rickandmorty.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nastya.rickandmorty.data.remote.api.ApiService
import com.nastya.rickandmorty.domain.model.characters.LocationsResDTO

class LocationPagingSource(
    val backend: ApiService,
    val query: String
) : PagingSource<Int, LocationsResDTO>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, LocationsResDTO> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = backend.getListDataLocation(nextPageNumber, query)
            val responseData = mutableListOf<LocationsResDTO>()
            val data = response.body()?.results ?: emptyList()
            responseData.addAll(data)

            LoadResult.Page(
                data = responseData,
                prevKey = if (nextPageNumber == 1) null else -1,
                nextKey = nextPageNumber.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LocationsResDTO>): Int? {
        return null
    }
}