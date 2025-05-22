package com.example.tobaccoalcoholshop

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.tobaccoalcoholshop.fragments.CatalogFragment
import com.example.tobaccoalcoholshop.fragments.FavoritesFragment
import com.example.tobaccoalcoholshop.fragments.CartFragment
import com.example.tobaccoalcoholshop.fragments.ProfileFragment
import com.example.tobaccoalcoholshop.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    // Фрагменты
    private val catalogFragment by lazy { CatalogFragment() }
    private val favoritesFragment by lazy { FavoritesFragment() }
    private val cartFragment by lazy { CartFragment() }
    private lateinit var profileFragment: ProfileFragment

    private var currentUsername: String? = null

    private val CREDENTIAL_SHARED_PREF = "our_shared_pref"
    private val LOGGED_IN_USER_KEY = "logged_in_username"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE)
        currentUsername = prefs.getString(LOGGED_IN_USER_KEY, null)

        if (currentUsername == null) {
            navigateToLogin()
            return
        }

        setContentView(R.layout.activity_main)

        profileFragment = ProfileFragment.newInstance(currentUsername!!)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        setupBottomNavigation()

        if (savedInstanceState == null) {
            switchToFragment(catalogFragment)
            bottomNavigationView.selectedItemId = R.id.navigation_catalog
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.navigation_catalog -> catalogFragment
                R.id.navigation_favorites -> favoritesFragment
                R.id.navigation_cart -> cartFragment
                R.id.navigation_profile -> profileFragment
                else -> catalogFragment
            }
            switchToFragment(selectedFragment)
            true
        }
        bottomNavigationView.setOnItemReselectedListener { }
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

    fun logoutUser() {
        val prefs = getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE)
        prefs.edit().remove(LOGGED_IN_USER_KEY).apply()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}