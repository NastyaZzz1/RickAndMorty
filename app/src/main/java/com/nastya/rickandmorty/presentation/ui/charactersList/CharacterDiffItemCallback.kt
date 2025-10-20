package com.nastya.rickandmorty.presentation.ui.charactersList

import androidx.recyclerview.widget.DiffUtil
import com.nastya.rickandmorty.domain.model.characters.CharactersResDTO

class CharacterDiffItemCallback: DiffUtil.ItemCallback<CharactersResDTO>() {
    override fun areItemsTheSame(oldItem: CharactersResDTO, newItem: CharactersResDTO)
            = (oldItem.id == newItem.id)

    override fun areContentsTheSame(oldItem: CharactersResDTO, newItem: CharactersResDTO) = (oldItem == newItem)
}