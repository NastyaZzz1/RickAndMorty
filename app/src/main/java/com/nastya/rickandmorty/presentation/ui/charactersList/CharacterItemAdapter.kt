package com.nastya.rickandmorty.presentation.ui.charactersList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.nastya.rickandmorty.databinding.CharacterItemBinding
import com.nastya.rickandmorty.domain.model.characters.CharactersResDTO

class CharacterItemAdapter(val clickListener: (characterId: Int) -> Unit):
    PagingDataAdapter<CharactersResDTO, CharacterItemAdapter.CharacterItemViewHolder>(
    CharacterDiffItemCallback()
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CharacterItemViewHolder {
        return CharacterItemViewHolder.inflateFrom(parent)
    }

    override fun onBindViewHolder(
        holder: CharacterItemViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.bind(
            item!!,
            clickListener)
    }

    class CharacterItemViewHolder (val binding: CharacterItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

            companion object {
            fun inflateFrom(parent: ViewGroup): CharacterItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CharacterItemBinding.inflate(layoutInflater, parent, false)
                return CharacterItemViewHolder(binding)
            }
        }
        fun bind(
            item: CharactersResDTO,
            clickListener: (characterId: Int) -> Unit
        ) {
            binding.name.text = item.name
            binding.characterImage.load(item.image) {
                crossfade(true)
            }
            binding.root.setOnClickListener { clickListener(item.id) }
            binding.status.text = item.status
            binding.genderAndSpecies.text = "${item.gender} | ${item.species}"
        }
    }
}