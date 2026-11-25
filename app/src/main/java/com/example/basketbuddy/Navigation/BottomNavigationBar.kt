package com.example.basketbuddy.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

// Main application navigation bar
@Composable
fun BottomNavigationBar(
    selectedItem: NavigationItem,
    onItemSelected: (NavigationItem) -> Unit
) {

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        // Create a navigation button for each destination
        NavigationItem.entries.forEach { item ->

            NavigationBarItem(

                // Determine whether this destination is selected
                selected = selectedItem == item,

                // Update the selected destination
                onClick = {
                    onItemSelected(item)
                },

                // Display the destination icon
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },

                // Display the destination name
                label = {
                    Text(text = item.label)
                },

                // Always display navigation labels
                alwaysShowLabel = true,

                // Apply application theme colors
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor =
                        MaterialTheme.colorScheme.primary,

                    selectedTextColor =
                        MaterialTheme.colorScheme.primary,

                    indicatorColor =
                        MaterialTheme.colorScheme.primaryContainer,

                    unselectedIconColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,

                    unselectedTextColor =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}