package com.nastya.rickandmorty.data.remote.api

import com.nastya.rickandmorty.domain.model.characters.CharactersDTO
import com.nastya.rickandmorty.domain.model.characters.CharactersResDTO
import com.nastya.rickandmorty.domain.model.characters.EpisodeDTO
import com.nastya.rickandmorty.domain.model.characters.EpisodeResDTO
import com.nastya.rickandmorty.domain.model.characters.LocationsDTO
import com.nastya.rickandmorty.domain.model.characters.LocationsResDTO
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("character")
    suspend fun getListDataCharacter(
        @Query("page") pageNumber: Int = 1,
        @Query("name") name : String = "",
    ): Response<CharactersDTO>

    @GET("character/{id}")
    suspend fun getOneCharacter(
        @Path("id") id: Int
    ): Response<CharactersResDTO>


    @GET("location")
    suspend fun getListDataLocation(
        @Query("page") pageNumber: Int = 1,
        @Query("name") name : String = "",
    ): Response<LocationsDTO>

    @GET("location/{id}")
    suspend fun getOneLocation(
        @Path("id") id: Int
    ): Response<LocationsResDTO>


    @GET("episode")
    suspend fun getListDataEpisode(
        @Query("page") pageNumber: Int = 1,
        @Query("name") name : String = "",
    ): Response<EpisodeDTO>

    @GET("episode/[{urlsId}]")
    suspend fun getListDataEpisodeMultiple(
        @Path("urlsId") urlsId: String = "",
    ): Response<List<EpisodeResDTO>>


    companion object {
        const val BASE_URL = "https://rickandmortyapi.com/api/"

        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        fun getApiService() = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiService::class.java)
    }
}

