package com.example.restaurentapp.Models

data class Order(
    val items: List<Pair<String, Int>>,
    val totalPrice: Double,
    val status: Boolean
)

