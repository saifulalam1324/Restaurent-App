package com.example.restaurentapp.Models
data class CartItem(
    val foodItem: FoodItem,
    var quantity: Int = 1
)