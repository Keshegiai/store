package com.example.tobaccoalcoholshop

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    var isFavorite: Boolean = false
)