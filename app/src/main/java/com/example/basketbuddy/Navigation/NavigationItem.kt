package com.example.basketbuddy.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// Available application navigation destinations
enum class NavigationItem(
    val label: String,
    val icon: ImageVector
) {

    // Home dashboard
    HOME(
        "Home",
        Icons.Filled.Home
    ),

    // Shopping list management
    SHOPPING(
        "Shopping",
        Icons.Filled.ShoppingCart
    ),

    // Pantry inventory management
    PANTRY(
        "Pantry",
        Icons.Filled.Inventory2
    ),

    // Application settings
    SETTINGS(
        "Settings",
        Icons.Filled.Settings
    )
}