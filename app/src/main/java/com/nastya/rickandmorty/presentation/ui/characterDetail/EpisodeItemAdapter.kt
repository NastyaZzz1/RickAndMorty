//package com.nastya.rickandmorty.presentation.ui.characterDetail
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.nastya.rickandmorty.databinding.FragmentDetailBinding
//import com.nastya.rickandmorty.domain.model.characters.EpisodeResDTO
//
//class EpisodeItemAdapter:
//    ListAdapter<EpisodeResDTO, EpisodeItemAdapter.EpisodeItemViewHolder>(
//        EpisodeDiffItemCallback()
//    ) {
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): EpisodeItemViewHolder {
//        return EpisodeItemViewHolder.inflateFrom(parent)
//    }
//
//    override fun onBindViewHolder(holder: EpisodeItemViewHolder, position: Int) {
//        val item = getItem(position)
//        holder.bind(item)
//    }
//
//    class EpisodeItemViewHolder (val binding: FragmentDetailBinding)
//        : RecyclerView.ViewHolder(binding.root) {
//        companion object {
//            fun inflateFrom(parent: ViewGroup): EpisodeItemViewHolder {
//                val layoutInflater = LayoutInflater.from(parent.context)
//                val binding = FragmentDetailBinding.inflate(layoutInflater, parent, false)
//                return EpisodeItemViewHolder(binding)
//            }
//        }
//
//        fun bind(
//            item: EpisodeResDTO
//        ) {
//
//        }
//    }
//}