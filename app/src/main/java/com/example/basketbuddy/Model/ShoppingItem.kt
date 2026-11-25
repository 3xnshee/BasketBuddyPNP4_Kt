package com.example.basketbuddy.data.model

// Represents an individual shopping list item
data class ShoppingItem(

    // Unique identifier for the item
    val id: Long,

    // Name of the grocery item
    val name: String,

    // Number of items needed
    val quantity: Int = 1,

    // Indicates whether the item has been purchased
    val isPurchased: Boolean = false
)