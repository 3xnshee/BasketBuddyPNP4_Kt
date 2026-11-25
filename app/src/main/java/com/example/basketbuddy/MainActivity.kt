package com.example.basketbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.basketbuddy.data.model.ShoppingItem
import com.example.basketbuddy.data.model.PantryItem
import com.example.basketbuddy.navigation.BottomNavigationBar
import com.example.basketbuddy.navigation.NavigationItem
import com.example.basketbuddy.ui.screens.HomeScreen
import com.example.basketbuddy.ui.screens.ShoppingListScreen
import com.example.basketbuddy.ui.screens.PantryScreen
import com.example.basketbuddy.ui.screens.SettingsScreen
import com.example.basketbuddy.ui.theme.BasketBuddyTheme

// Main entry point for Basket Buddy
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Load the application interface
        setContent {
            BasketBuddyApp()
        }
    }
}

// Base application layout
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasketBuddyApp() {

    // Store the current application theme preference
    var darkTheme by rememberSaveable {
        mutableStateOf(false)
    }

    // Store the currently selected navigation destination
    var selectedItem by rememberSaveable {
        mutableStateOf(NavigationItem.HOME)
    }

    // Temporarily store shopping list items
    val shoppingItems = remember {
        mutableStateListOf<ShoppingItem>()
    }

    // Generate unique shopping item identifiers
    var nextShoppingItemId by remember {
        mutableLongStateOf(1L)
    }

    // Temporarily store pantry inventory items
    val pantryItems = remember {
        mutableStateListOf<PantryItem>()
    }

    // Generate unique pantry item identifiers
    var nextPantryItemId by remember {
        mutableLongStateOf(1L)
    }

    // Transfer purchased shopping items into the pantry
    fun transferPurchasedItems() {

        // Retrieve all purchased shopping items
        val purchasedItems = shoppingItems.filter {
            it.isPurchased
        }

        // Track successfully transferred items
        val transferredItemIds = mutableSetOf<Long>()

        // Process each purchased shopping item
        purchasedItems.forEach { shoppingItem ->

            // Check whether the item already exists in the pantry
            val pantryIndex = pantryItems.indexOfFirst {
                it.name.equals(
                    shoppingItem.name,
                    ignoreCase = true
                )
            }

            if (pantryIndex != -1) {

                // Retrieve the existing pantry item
                val existingItem = pantryItems[pantryIndex]

                // Calculate the combined quantity safely
                val updatedQuantity =
                    existingItem.quantity.toLong() +
                            shoppingItem.quantity.toLong()

                // Prevent integer overflow and inventory loss
                if (updatedQuantity <= Int.MAX_VALUE) {

                    // Update the existing pantry item
                    pantryItems[pantryIndex] =
                        existingItem.copy(
                            quantity = updatedQuantity.toInt()
                        )

                    // Mark the shopping item as transferred
                    transferredItemIds.add(shoppingItem.id)
                }

            } else {

                // Create a new pantry item
                pantryItems.add(
                    PantryItem(
                        id = nextPantryItemId++,
                        name = shoppingItem.name,
                        quantity = shoppingItem.quantity
                    )
                )

                // Mark the shopping item as transferred
                transferredItemIds.add(shoppingItem.id)
            }
        }

        // Remove only successfully transferred shopping items
        shoppingItems.removeAll {
            it.id in transferredItemIds
        }
    }

    // Apply the selected application theme
    BasketBuddyTheme(
        darkTheme = darkTheme
    ) {

        Scaffold(

            // Application header
            topBar = {

                TopAppBar(
                    title = {
                        Text(
                            text = "Basket Buddy",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },

                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            MaterialTheme.colorScheme.primary,

                        titleContentColor =
                            MaterialTheme.colorScheme.onPrimary
                    )
                )
            },

            // Bottom navigation bar
            bottomBar = {

                BottomNavigationBar(
                    selectedItem = selectedItem,

                    onItemSelected = { item ->
                        selectedItem = item
                    }
                )
            },

            // Application background
            containerColor = MaterialTheme.colorScheme.background

        ) { innerPadding ->

            // Main content area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),

                contentAlignment = Alignment.Center
            ) {

                // Display content based on selected destination
                when (selectedItem) {

                    // Home dashboard
                    NavigationItem.HOME -> {

                        HomeScreen(

                            // Count shopping items not yet purchased
                            shoppingItemCount = shoppingItems.count {
                                !it.isPurchased
                            },

                            // Count pantry items currently in stock
                            pantryItemCount = pantryItems.count {
                                !it.isOutOfStock
                            },

                            // Navigate to the shopping list
                            onShoppingClick = {
                                selectedItem = NavigationItem.SHOPPING
                            },

                            // Navigate to the pantry
                            onPantryClick = {
                                selectedItem = NavigationItem.PANTRY
                            }
                        )
                    }

                    // Shopping List screen
                    NavigationItem.SHOPPING -> {

                        ShoppingListScreen(
                            shoppingItems = shoppingItems,

                            // Add a new shopping item
                            onAddItem = { name ->

                                // Prevent blank item names
                                if (name.isNotBlank()) {

                                    shoppingItems.add(
                                        ShoppingItem(
                                            id = nextShoppingItemId++,
                                            name = name.trim()
                                        )
                                    )
                                }
                            },

                            // Delete an existing shopping item
                            onDeleteItem = { id ->

                                shoppingItems.removeAll {
                                    it.id == id
                                }
                            },

                            // Update an item's quantity
                            onQuantityChange = { id, quantity ->

                                val index =
                                    shoppingItems.indexOfFirst {
                                        it.id == id
                                    }

                                // Prevent quantities below one
                                if (index != -1 && quantity >= 1) {

                                    shoppingItems[index] =
                                        shoppingItems[index].copy(
                                            quantity = quantity
                                        )
                                }
                            },

                            // Update an item's purchase status
                            onPurchasedChange = { id, purchased ->

                                val index =
                                    shoppingItems.indexOfFirst {
                                        it.id == id
                                    }

                                if (index != -1) {

                                    shoppingItems[index] =
                                        shoppingItems[index].copy(
                                            isPurchased = purchased
                                        )
                                }
                            },

                            // Transfer purchased items into the pantry
                            onTransferToPantry = {
                                transferPurchasedItems()
                            },

                            // Update an existing shopping item
                            onEditItem = { id, name, quantity ->

                                // Locate the shopping item being edited
                                val index =
                                    shoppingItems.indexOfFirst {
                                        it.id == id
                                    }

                                // Validate the updated item information
                                if (
                                    index != -1 &&
                                    name.isNotBlank() &&
                                    quantity >= 1
                                ) {

                                    // Preserve the item ID and purchase status
                                    shoppingItems[index] =
                                        shoppingItems[index].copy(
                                            name = name.trim(),
                                            quantity = quantity
                                        )
                                }
                            }
                        )
                    }

                    // Pantry screen
                    NavigationItem.PANTRY -> {

                        PantryScreen(
                            pantryItems = pantryItems,

                            // Add a new pantry item
                            onAddItem = { name ->

                                // Prevent blank item names
                                if (name.isNotBlank()) {

                                    pantryItems.add(
                                        PantryItem(
                                            id = nextPantryItemId++,
                                            name = name.trim()
                                        )
                                    )
                                }
                            },

                            // Permanently delete a pantry item
                            onDeleteItem = { id ->

                                pantryItems.removeAll {
                                    it.id == id
                                }
                            },

                            // Update the current pantry quantity
                            onQuantityChange = { id, quantity ->

                                val index =
                                    pantryItems.indexOfFirst {
                                        it.id == id
                                    }

                                // Prevent negative pantry quantities
                                if (index != -1 && quantity >= 0) {

                                    // Retain items at zero quantity
                                    pantryItems[index] =
                                        pantryItems[index].copy(
                                            quantity = quantity
                                        )
                                }
                            },

                            // Update an existing pantry item
                            onEditItem = { id, name, quantity ->

                                // Locate the pantry item being edited
                                val index =
                                    pantryItems.indexOfFirst {
                                        it.id == id
                                    }

                                // Validate the updated item information
                                if (
                                    index != -1 &&
                                    name.isNotBlank() &&
                                    quantity >= 0
                                ) {

                                    // Preserve the item's unique ID
                                    pantryItems[index] =
                                        pantryItems[index].copy(
                                            name = name.trim(),
                                            quantity = quantity
                                        )
                                }
                            }
                        )
                    }

                    // Settings screen
                    NavigationItem.SETTINGS -> {

                        SettingsScreen(
                            darkTheme = darkTheme,

                            // Update the application theme
                            onDarkThemeChange = { enabled ->
                                darkTheme = enabled
                            }
                        )
                    }
                }
            }
        }
    }
}

// Android Studio design preview
@Preview(showBackground = true)
@Composable
fun BasketBuddyPreview() {
    BasketBuddyApp()
}