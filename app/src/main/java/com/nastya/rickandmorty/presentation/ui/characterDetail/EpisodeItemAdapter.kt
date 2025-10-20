package com.nastya.rickandmorty.presentation.ui.characterDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nastya.rickandmorty.databinding.EpisodeItemBinding
import com.nastya.rickandmorty.domain.model.characters.EpisodeResDTO

class EpisodeItemAdapter:
    ListAdapter<EpisodeResDTO, EpisodeItemAdapter.EpisodeItemViewHolder>(
        EpisodeDiffItemCallback()
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EpisodeItemViewHolder {
        return EpisodeItemViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(holder: EpisodeItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class EpisodeItemViewHolder (val binding: EpisodeItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): EpisodeItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = EpisodeItemBinding.inflate(layoutInflater, parent, false)
                return EpisodeItemViewHolder(binding)
            }
        }

        fun bind(
            item: EpisodeResDTO
        ) {
            binding.episodeName.text = item.name
            binding.episodeAirDate.text = item.air_date
            binding.episodeNum.text = item.episode
        }
    }
}