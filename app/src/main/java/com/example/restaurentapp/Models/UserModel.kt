package com.example.restaurentapp.Models

data class UserModel(
    val email: String = "",
    val userId: String = "",
    val isAdmin: Boolean = false,
    val isUser: Boolean = false
)