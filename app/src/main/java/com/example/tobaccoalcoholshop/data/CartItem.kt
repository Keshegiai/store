package com.example.tobaccoalcoholshop.data

data class CartItem(
    val product: Product,
    var quantity: Int = 1
)