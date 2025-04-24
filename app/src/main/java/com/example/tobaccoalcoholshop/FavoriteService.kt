package com.example.tobaccoalcoholshop
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoriteService(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val FAVORITES_KEY = "favorite_ids"

    fun getFavoriteIds(): Set<Int> {
        val json = sharedPreferences.getString(FAVORITES_KEY, null) ?: return emptySet()
        val type = object : TypeToken<Set<Int>>() {}.type
        return gson.fromJson(json, type) ?: emptySet()
    }

    fun addToFavorites(productId: Int) {
        val currentFavorites = getFavoriteIds().toMutableSet()
        currentFavorites.add(productId)
        saveFavorites(currentFavorites)
    }

    fun removeFromFavorites(productId: Int) {
        val currentFavorites = getFavoriteIds().toMutableSet()
        currentFavorites.remove(productId)
        saveFavorites(currentFavorites)
    }

    fun isFavorite(productId: Int): Boolean {
        return getFavoriteIds().contains(productId)
    }

    private fun saveFavorites(favorites: Set<Int>) {
        val json = gson.toJson(favorites)
        sharedPreferences.edit().putString(FAVORITES_KEY, json).apply()
    }
}

