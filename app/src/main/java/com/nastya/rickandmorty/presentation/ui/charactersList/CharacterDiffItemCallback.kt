package com.nastya.rickandmorty.presentation.ui.charactersList

import androidx.recyclerview.widget.DiffUtil
import com.nastya.rickandmorty.data.local.entity.CharacterEntity
import com.nastya.rickandmorty.domain.model.characters.CharactersResDTO

class CharacterDiffItemCallback: DiffUtil.ItemCallback<CharacterEntity>() {
    override fun areItemsTheSame(oldItem: CharacterEntity, newItem: CharacterEntity)
            = (oldItem.id == newItem.id)

    override fun areContentsTheSame(oldItem: CharacterEntity, newItem: CharacterEntity) = (oldItem == newItem)
}