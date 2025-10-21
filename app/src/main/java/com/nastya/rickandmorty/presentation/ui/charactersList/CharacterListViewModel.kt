package com.nastya.rickandmorty.presentation.ui.charactersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nastya.rickandmorty.data.repository.RemoteRepositoryImpl
import com.nastya.rickandmorty.domain.model.characters.CharactersResDTO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CharacterListViewModel: ViewModel() {
    sealed class UiState() {
        object Loading : UiState()
        data class Success(val data: PagingData<CharactersResDTO>) : UiState()
        data class Error(val message: String) : UiState()
    }
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private val searchChannel = Channel<String>(Channel.CONFLATED)

    val listData: Flow<PagingData<CharactersResDTO>> = searchQuery
        .flatMapLatest { query ->
            remoteRepositoryImpl.getCharactersStream(query) { error ->
                _uiState.value = UiState.Error(error.message ?: "Unknown error")
            }.cachedIn(viewModelScope)
        }

    private val _navigateToDetail = Channel<Int>()
    val navigateToDetail = _navigateToDetail.receiveAsFlow()

    private val remoteRepositoryImpl = RemoteRepositoryImpl()

    init {
        observeListData()
        viewModelScope.launch {
            searchChannel.consumeAsFlow()
                .debounce(500)
                .collect { query ->
                    searchQuery.value = query
                }
        }
    }

    private fun observeListData() {
        viewModelScope.launch {
            listData
                .debounce(1500)
                .collect { pagingData ->
                if (_uiState.value is UiState.Loading) {
                    _uiState.value = UiState.Success(pagingData)
                }
            }
        }
    }

    fun handleLoadState(loadState: CombinedLoadStates, adapterItemCount: Int) {
        when (loadState.refresh) {
            is LoadState.Loading -> {
                _isRefreshing.value = adapterItemCount > 0
            }
            is LoadState.NotLoading -> {
                _isRefreshing.value = false
            }
            is LoadState.Error -> {
                _isRefreshing.value = false
                val error = (loadState.refresh as LoadState.Error).error
                _uiState.value = UiState.Error(error.message ?: "Unknown error")
            }
        }
    }

    fun onCharacterClicked(characterId: Int) {
        viewModelScope.launch {
            _navigateToDetail.send(characterId)
        }
    }

    fun searchCharacters(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun searchCharactersDebounced(newQuery: String) {
        viewModelScope.launch {
            searchChannel.send(newQuery)
        }
    }
}