package com.nastya.rickandmorty.presentation.ui.characterDetail

import androidx.recyclerview.widget.DiffUtil
import com.nastya.rickandmorty.domain.model.characters.EpisodeResDTO

class EpisodeDiffItemCallback: DiffUtil.ItemCallback<EpisodeResDTO>() {
    override fun areItemsTheSame(oldItem: EpisodeResDTO, newItem: EpisodeResDTO)
            = (oldItem.id == newItem.id)

    override fun areContentsTheSame(oldItem: EpisodeResDTO, newItem: EpisodeResDTO) = (oldItem == newItem)
}