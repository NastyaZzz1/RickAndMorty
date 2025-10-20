package com.nastya.rickandmorty.presentation.ui.characterDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.nastya.rickandmorty.databinding.FragmentDetailBinding
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DetailViewModel
//    private lateinit var adapter: EpisodeItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        adapter = EpisodeItemAdapter()
//        binding.episodeList.adapter = adapter

        observerCharacter()
        observerLocation()
//        observerEpisode()
    }

    fun observerCharacter() {
        lifecycleScope.launch {
            viewModel.character.collect { characterItem ->
                characterItem?.let {
                    viewModel.requestLocation(characterItem.location.url.substringAfterLast('/').toInt())
//                    viewModel.requestEpisode(characterItem.episode)

                    binding.charName.text = characterItem.name
                    binding.charGender.text = "Gender: ${characterItem.gender}"
                    binding.charSpecies.text = "Species: ${characterItem.species}"
                    binding.charStatus.text = "Status: ${characterItem.status}"
                    binding.charImage.load(characterItem.image) {
                        crossfade(false)
                    }
                }
            }
        }
    }

    fun observerLocation() {
        lifecycleScope.launch {
            viewModel.location.collect { locationItem ->
                locationItem?.let {
                    binding.charLocationName.text = "Name: ${locationItem.name}"
                    binding.charLocationType.text = "Type: ${locationItem.type}"
                    binding.charLocationDimension.text = "Dimension: ${locationItem.dimension}"
                }
            }
        }
    }

//    fun observerEpisode() {
//        lifecycleScope.launch {
//            viewModel.episodes.collect { episodesList ->
//                adapter.submitList(episodesList)
//            }
//        }
//    }
}