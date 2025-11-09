package com.nastya.rickandmorty.presentation.ui.charactersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.nastya.rickandmorty.R
import com.nastya.rickandmorty.data.local.entity.CharacterEntity
import com.nastya.rickandmorty.data.repository.CharacterRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CharacterListViewModel(
    private val repository: CharacterRepository
): ViewModel() {
    sealed class UiState() {
        object Loading : UiState()
        data class Success(val data: PagingData<CharacterEntity>) : UiState()
        data class Error(val message: String) : UiState()
    }
    data class CharacterFilters(
        val name: String = "",
        val status: String = "",
        val gender: String = "",
        val species: String = ""
    )
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private val searchChannel = Channel<String>(Channel.CONFLATED)

    private val queryFilterStatus = MutableStateFlow("")
    private val queryFilterGender = MutableStateFlow("")
    private val queryFilterSpecies = MutableStateFlow("")

    val listData: Flow<PagingData<CharacterEntity>> =
        combine(
            searchQuery,
        queryFilterStatus,
        queryFilterGender,
            queryFilterSpecies
        )
        { name, status, gender, species ->
            CharacterFilters(name, status, gender, species)
        }.flatMapLatest { filters ->
            repository.refreshData()

            repository.getCharactersStream(
                filters.name,
                filters.gender,
                filters.status,
                filters.species
            ).cachedIn(viewModelScope)
        }

    private val _navigateToDetail = Channel<Int>()
    val navigateToDetail = _navigateToDetail.receiveAsFlow()

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

    fun setupFilterChips(
        chipsGroupStatus: ChipGroup,
        chipsGroupGender: ChipGroup,
        chipsGroupSpecies: ChipGroup,
    ) {
        val selectedStatusId = chipsGroupStatus.checkedChipId
        queryFilterStatus.value = when (selectedStatusId) {
            R.id.chip_status_alive -> "alive"
            R.id.chip_status_dead -> "dead"
            R.id.chip_status_unknown-> "unknown"
            else -> ""
        }

        val selectedGenderId = chipsGroupGender.checkedChipId
        queryFilterGender.value = when (selectedGenderId) {
            R.id.chip_gender_unknown -> "unknown"
            R.id.chip_gender_male -> "male"
            R.id.chip_gender_female -> "female"
            R.id.chip_gender_genderless -> "genderless"
            else -> ""
        }

        val selectedSpeciesId = chipsGroupSpecies.checkedChipId
        queryFilterSpecies.value = when (selectedSpeciesId) {
            R.id.chip_alien -> "alien"
            R.id.chip_animal -> "animal"
            R.id.chip_cronenberg -> "cronenberg"
            R.id.chip_disease -> "disease"
            R.id.chip_human -> "human"
            R.id.chip_humanoid -> "humanoid"
            R.id.chip_robot -> "robot"
            R.id.chip_mythological_creature -> "mythological Creature"
            R.id.chip_unknown -> "unknown"
            R.id.chip_poopybutthole -> "poopybutthole"
            else -> ""
        }
    }

    fun setupInitialChipSelection(
        chipsGroupStatus: ChipGroup,
        chipsGroupGender: ChipGroup,
        chipsGroupSpecies: ChipGroup,
    ) {
        chipsGroupStatus.clearCheck()
        chipsGroupGender.clearCheck()
        chipsGroupSpecies.clearCheck()

        when (queryFilterStatus.value) {
            "alive" -> chipsGroupStatus.findViewById<Chip>(R.id.chip_status_alive).isChecked = true
            "dead" -> chipsGroupStatus.findViewById<Chip>(R.id.chip_status_dead).isChecked = true
            "unknown" -> chipsGroupStatus.findViewById<Chip>(R.id.chip_status_unknown).isChecked = true
        }
        when (queryFilterGender.value) {
            "male" -> chipsGroupGender.findViewById<Chip>(R.id.chip_gender_male).isChecked = true
            "female" -> chipsGroupGender.findViewById<Chip>(R.id.chip_gender_female).isChecked = true
            "genderless" -> chipsGroupGender.findViewById<Chip>(R.id.chip_gender_genderless).isChecked = true
            "unknown" -> chipsGroupGender.findViewById<Chip>(R.id.chip_gender_unknown).isChecked = true
        }
        when (queryFilterSpecies.value) {
            "alien" -> chipsGroupSpecies.findViewById<Chip>(R.id.chip_alien).isChecked = true
            "animal" -> chipsGroupSpecies.findViewById<Chip>(R.id.chip_animal).isChecked = true
            "cronenberg" -> chipsGroupSpecies.findViewById<Chip>(R.id.chip_cronenberg).isChecked = true
            "disease" -> chipsGroupSpecies.findViewById<Chip>(R.id.chip_disease).isChecked = true
            "human" -> chipsGroupSpecies.findViewById<Chip>(R.id.chip_human).isChecked = true
            "humanoid" -> chipsGroupSpecies.findViewById<Chip>(R.id.chip_humanoid).isChecked = true
            "robot" -> chipsGroupSpecies.findViewById<Chip>(R.id.chip_robot).isChecked = true
            "mythological Creature" -> chipsGroupSpecies.findViewById<Chip>(R.id.chip_mythological_creature).isChecked = true
            "unknown" -> chipsGroupSpecies.findViewById<Chip>(R.id.chip_unknown).isChecked = true
            "poopybutthole" -> chipsGroupSpecies.findViewById<Chip>(R.id.chip_poopybutthole).isChecked = true
        }
    }
}