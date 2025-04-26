package com.example.tobaccoalcoholshop.services

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.tobaccoalcoholshop.data.CartItem
import com.example.tobaccoalcoholshop.data.Product

class CartService(context: Context) {

    private val prefs: SharedPreferences
    private val gson = Gson()
    private val CART_KEY = "cart_items"

    private val CREDENTIAL_SHARED_PREF = "our_shared_pref"
    private val LOGGED_IN_USER_KEY = "logged_in_username"

    init {
        val credentialPrefs = context.getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE)
        val username = credentialPrefs.getString(LOGGED_IN_USER_KEY, null)

        if (username == null) {
            throw IllegalStateException("CartService cannot be initialized without a logged-in user.")
        } else {
            val prefsName = "cart_prefs_$username"
            prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        }
    }

    fun getCartItems(): MutableList<CartItem> {
        val json = prefs.getString(CART_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<CartItem>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } else {
            mutableListOf()
        }
    }

    fun addItem(product: Product) {
        val items = getCartItems()
        val existingItem = items.find { it.product.id == product.id }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            items.add(CartItem(product = product, quantity = 1))
        }
        saveCart(items)
    }

    fun removeItem(productId: Int) {
        val items = getCartItems()
        items.removeAll { it.product.id == productId }
        saveCart(items)
    }

    fun incrementQuantity(productId: Int) {
        val items = getCartItems()
        items.find { it.product.id == productId }?.let {
            it.quantity++
            saveCart(items)
        }
    }

    fun decrementQuantity(productId: Int) {
        val items = getCartItems()
        val item = items.find { it.product.id == productId }
        if (item != null) {
            if (item.quantity > 1) {
                item.quantity--
                saveCart(items)
            } else {
                removeItem(productId)
            }
        }
    }

    fun clearCart() {
        saveCart(mutableListOf())
    }

    fun getTotalPrice(): Double {
        return getCartItems().sumOf { it.product.price * it.quantity }
    }

    private fun saveCart(items: List<CartItem>) {
        val json = gson.toJson(items)
        prefs.edit().putString(CART_KEY, json).apply()
    }

    fun clearUserCart() {
        prefs.edit().clear().apply()
    }
}