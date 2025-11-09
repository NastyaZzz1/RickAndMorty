package com.nastya.rickandmorty.presentation.ui.charactersList

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.nastya.rickandmorty.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.ChipGroup
import com.nastya.rickandmorty.data.local.database.RickAndMortyDatabase
import com.nastya.rickandmorty.data.remote.api.ApiService
import com.nastya.rickandmorty.data.repository.CharacterRepository
import com.nastya.rickandmorty.databinding.FragmentCharacterListBinding
import com.nastya.rickandmorty.presentation.ui.main.MainActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CharacterListFragment : Fragment(), MainActivity.SearchListener {
    private var _binding: FragmentCharacterListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CharacterListViewModel
    private lateinit var adapter: CharacterItemAdapter
    private lateinit var dismissButton: Button
    private lateinit var chipsGroupStatus: ChipGroup
    private lateinit var chipsGroupGender: ChipGroup
    private lateinit var chipsGroupSpecies: ChipGroup
    private val apiService: ApiService by lazy {
        ApiService.getApiService()
    }
    private val database: RickAndMortyDatabase by lazy {
        RickAndMortyDatabase.getInstance(requireContext())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).setSearchListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        val view = binding.root
        val repository = CharacterRepository(apiService, database)
        val viewModelFactory = CharacterListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(CharacterListViewModel::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(binding.fabFilter) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right + 15
            }
            WindowInsetsCompat.CONSUMED
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupList()
        setupDataCollection()
        observerUiState()
        observerNavigate()
        setupRefresh()
        observeLoadState()
        observeRefreshingState()
        setupFabFilter()
    }

    private fun setupDataCollection() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listData.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }

    fun setupFabFilter() {
        binding.fabFilter.setOnClickListener {
            val viewDialog = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            dismissButton = viewDialog.findViewById(R.id.dismissButton)
            chipsGroupStatus = viewDialog.findViewById(R.id.chip_group_status)
            chipsGroupGender = viewDialog.findViewById(R.id.chip_group_gender)
            chipsGroupSpecies = viewDialog.findViewById(R.id.chip_group_species)
            viewModel.setupInitialChipSelection(chipsGroupStatus, chipsGroupGender, chipsGroupSpecies)
            val dialog = BottomSheetDialog(requireContext())

            dismissButton.setOnClickListener {
                viewModel.setupFilterChips(
                    chipsGroupStatus,
                    chipsGroupGender,
                    chipsGroupSpecies
                )
                dialog.dismiss()
            }
            dialog.setCancelable(false)
            dialog.setContentView(viewDialog)
            dialog.show()
        }
    }

    fun setupRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun observeRefreshingState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isRefreshing.collect { isRefreshing ->
                    binding.swipeRefreshLayout.isRefreshing = isRefreshing
                }
            }
        }
    }

    private fun observeLoadState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collect { loadState ->
                    viewModel.handleLoadState(loadState, adapter.itemCount)
                }
            }
        }
    }

    private fun setupList() {
        adapter = CharacterItemAdapter { characterId ->
            viewModel.onCharacterClicked(characterId)
        }
        binding.characterList.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                Log.d("Adapter", "Items inserted: $itemCount at position $positionStart")
                Log.d("Adapter", "Total items: ${adapter.itemCount}")
            }

            override fun onChanged() {
                super.onChanged()
                Log.d("Adapter", "Adapter data changed, total items: ${adapter.itemCount}")
            }
        })
    }

    fun observerUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when(state) {
                    is CharacterListViewModel.UiState.Loading -> showLoading()
                    is CharacterListViewModel.UiState.Success -> showData()
                    is CharacterListViewModel.UiState.Error -> showError()
                }
            }
        }
    }

    fun observerNavigate() {
        lifecycleScope.launch {
            viewModel.navigateToDetail.collect { characterId ->
                characterId.let {
                    val action = CharacterListFragmentDirections.
                    actionCharacterListFragmentToDetailFragment(characterId)
                    findNavController().navigate(action)
                }
            }
        }
    }

    fun showLoading() {
        binding.progressIndicator.visibility = View.VISIBLE
        binding.characterList.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
    }

    fun showData() {
        binding.progressIndicator.visibility = View.GONE
        binding.characterList.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE
    }

    fun showError() {
        binding.progressIndicator.visibility = View.GONE
        binding.characterList.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSearch(query: String) {
        viewModel.searchCharacters(query)
    }

    override fun onSearchQueryChanged(query: String) {
        viewModel.searchCharactersDebounced(query)
    }
}