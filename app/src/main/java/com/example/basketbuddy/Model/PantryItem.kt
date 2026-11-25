package com.example.basketbuddy.data.model

// Represents an individual pantry inventory item
data class PantryItem(

    // Unique identifier for the pantry item
    val id: Long,

    // Name of the grocery item
    val name: String,

    // Current quantity available
    val quantity: Int = 1

) {

    // Determine whether the item is out of stock
    val isOutOfStock: Boolean
        get() = quantity == 0
}