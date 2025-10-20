package com.nastya.rickandmorty.presentation.ui.characterDetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nastya.rickandmorty.data.repository.RemoteRepositoryImpl
import com.nastya.rickandmorty.domain.model.characters.CharactersResDTO
import com.nastya.rickandmorty.domain.model.characters.EpisodeResDTO
import com.nastya.rickandmorty.domain.model.characters.LocationsResDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(savedStateHandle: SavedStateHandle): ViewModel() {
    val characterId = savedStateHandle.get<Int>("characterId")

    private val remoteRepositoryImpl = RemoteRepositoryImpl()

    private val _character = MutableStateFlow<CharactersResDTO?>(null)
    val character: StateFlow<CharactersResDTO?> = _character.asStateFlow()

    private val _location = MutableStateFlow<LocationsResDTO?>(null)
    val location: StateFlow<LocationsResDTO?> = _location.asStateFlow()

    private val _episodes = MutableStateFlow<List<EpisodeResDTO>?>(null)
    val episodes: StateFlow<List<EpisodeResDTO>?> = _episodes.asStateFlow()

    init {
        requestCharacter(characterId)
    }

    fun requestCharacter(characterId: Int?) {
        viewModelScope.launch {
            try {
                if(characterId != null) {
                    val response = remoteRepositoryImpl.getOneCharacter(characterId)
                    if(response.isSuccessful) {
                        _character.emit(response.body())
                    }
                    else {
                        Log.d("DetailViewModel", "API error: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.d("DetailViewModel", "Error: ${e.message}")
            }
        }
    }

    fun requestLocation(locationId: Int?) {
        viewModelScope.launch {
            try {
                if(locationId != null) {
                    val response = remoteRepositoryImpl.getOneLocation(locationId)
                    if(response.isSuccessful) {
                        _location.emit(response.body())
                    }
                    else {
                        Log.d("DetailViewModel", "API error: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.d("DetailViewModel", "Error: ${e.message}")
            }
        }
    }

    fun requestEpisode(episodeUrls: List<String>) {
        viewModelScope.launch {
            try {
                val episodesId = episodeUrls.joinToString(",") { it.substringAfterLast('/') }
                val response = remoteRepositoryImpl.getEpisodeMultiple(episodesId)
                if(response.isSuccessful) {
                    _episodes.emit(response.body())
                }
                else {
                    Log.d("DetailViewModel", "API error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.d("DetailViewModel", "Error: ${e.message}")
            }
        }
    }
}