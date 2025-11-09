package com.nastya.rickandmorty.presentation.ui.charactersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nastya.rickandmorty.data.repository.CharacterRepository

class CharacterListViewModelFactory(private val repository: CharacterRepository)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacterListViewModel::class.java)) {
            return CharacterListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}