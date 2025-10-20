package com.nastya.rickandmorty.presentation.ui.main

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.nastya.rickandmorty.R
import com.nastya.rickandmorty.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var searchListener: SearchListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.characterListFragment -> {
                    showSearchView()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    supportActionBar?.setDisplayShowHomeEnabled(false)
                }
                R.id.detailFragment -> {
                    hideSearchView()
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    supportActionBar?.setDisplayShowHomeEnabled(true)
                }
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setContentView(binding.root)

        binding.idSV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchListener?.onSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchListener?.onSearchQueryChanged(newText ?: "")
                return true
            }
        })

        binding.idSV.setOnCloseListener {
            clearSearch()
            false
        }
        setSearchColor()
        setupSearchCloseButton()
    }

    fun hideSearchView() {
        binding.idSV.visibility = View.GONE
    }

    fun showSearchView() {
        binding.idSV.visibility = View.VISIBLE
    }

    private fun setupSearchCloseButton() {
        try {
            val searchCloseButtonId = resources.getIdentifier("android:id/search_close_btn", null, null)
            val searchIcon = binding.idSV.findViewById<ImageView>(searchCloseButtonId)
            searchIcon?.setOnClickListener {
                clearSearch()
            }
            searchIcon?.setColorFilter(
                ContextCompat.getColor(this, R.color.light_grey),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setSearchColor() {
        val searchIconId = resources.getIdentifier("android:id/search_mag_icon", null, null)
        val searchIcon = binding.idSV.findViewById<ImageView>(searchIconId)
        searchIcon?.setColorFilter(
            ContextCompat.getColor(this, R.color.light_grey),
            android.graphics.PorterDuff.Mode.SRC_IN
        )

        val searchTextId = resources.getIdentifier("android:id/search_src_text", null, null)
        val searchEditText = binding.idSV.findViewById<EditText>(searchTextId)
        searchEditText?.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    fun clearSearch() {
        binding.idSV.setQuery("", false)
        binding.idSV.clearFocus()
        searchListener?.onSearch("")
    }

    fun setSearchListener(listener: SearchListener) {
        this.searchListener = listener
    }

    interface SearchListener {
        fun onSearch(query: String)
        fun onSearchQueryChanged(query: String)
    }
}