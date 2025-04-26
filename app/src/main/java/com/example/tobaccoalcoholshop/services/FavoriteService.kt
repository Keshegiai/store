package com.example.tobaccoalcoholshop.services

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoriteService(context: Context) {

    private val prefs: SharedPreferences
    private val gson = Gson()
    private val FAVORITES_KEY = "favorite_ids"

    private val CREDENTIAL_SHARED_PREF = "our_shared_pref"
    private val LOGGED_IN_USER_KEY = "logged_in_username"

    init {
        val credentialPrefs = context.getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE)
        val username = credentialPrefs.getString(LOGGED_IN_USER_KEY, null)

        if (username == null) {
            throw IllegalStateException("FavoriteService cannot be initialized without a logged-in user.")
        } else {
            val prefsName = "favorites_prefs_$username"
            prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        }
    }

    fun getFavoriteIds(): Set<Int> {
        val json = prefs.getString(FAVORITES_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<Set<Int>>() {}.type
            gson.fromJson(json, type) ?: emptySet()
        } else {
            emptySet()
        }
    }

    fun addToFavorites(productId: Int) {
        val favorites = getFavoriteIds().toMutableSet()
        favorites.add(productId)
        saveFavorites(favorites)
    }

    fun removeFromFavorites(productId: Int) {
        val favorites = getFavoriteIds().toMutableSet()
        favorites.remove(productId)
        saveFavorites(favorites)
    }

    fun isFavorite(productId: Int): Boolean {
        return getFavoriteIds().contains(productId)
    }

    private fun saveFavorites(favorites: Set<Int>) {
        val json = gson.toJson(favorites)
        prefs.edit().putString(FAVORITES_KEY, json).apply()
    }

    fun clearUserFavorites() {
        prefs.edit().clear().apply()
    }
}