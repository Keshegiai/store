package com.example.tobaccoalcoholshop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.tobaccoalcoholshop.R

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    private val catalogFragment = CatalogFragment()
    private val favoritesFragment = FavoritesFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomNavigation()

        if (savedInstanceState == null) {
            switchToFragment(catalogFragment)
            bottomNavigationView.selectedItemId = R.id.navigation_catalog
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_catalog -> {
                    switchToFragment(catalogFragment)
                    true
                }
                R.id.navigation_favorites -> {
                    switchToFragment(favoritesFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun switchToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun refreshFavorites() {
        if (favoritesFragment.isAdded) {
            favoritesFragment.loadFavoriteProducts()
        }
    }

    fun refreshCatalog() {
        if (catalogFragment.isAdded && catalogFragment.isVisible) {
            catalogFragment.refreshDataAndView()
        }
    }

    override fun onBackPressed() {
        if (bottomNavigationView.selectedItemId != R.id.navigation_catalog) {
            bottomNavigationView.selectedItemId = R.id.navigation_catalog
        } else {
            super.onBackPressed()
        }
    }
}

