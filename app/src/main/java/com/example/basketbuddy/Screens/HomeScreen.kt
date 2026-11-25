package com.example.basketbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

// Main Basket Buddy dashboard
@Composable
fun HomeScreen(
    shoppingItemCount: Int = 0,
    pantryItemCount: Int = 0,
    onShoppingClick: () -> Unit,
    onPantryClick: () -> Unit
) {

    // Scrollable dashboard content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Welcome message
        Text(
            text = "Welcome to Basket Buddy",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        // Application description
        Text(
            text = "Manage your groceries in one place.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Shopping list summary card
        DashboardCard(
            title = "Shopping List",
            itemCount = shoppingItemCount,
            description = "Items on your shopping list",
            actionText = "View shopping list",
            icon = Icons.Filled.ShoppingCart,
            highlighted = true,
            onClick = onShoppingClick
        )

        // Pantry summary card
        DashboardCard(
            title = "Pantry",
            itemCount = pantryItemCount,
            description = "Items currently in your pantry",
            actionText = "View pantry",
            icon = Icons.Filled.Inventory2,
            highlighted = false,
            onClick = onPantryClick
        )

        // Quick actions section
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium
        )

        // Open shopping list
        Button(
            onClick = onShoppingClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Open Shopping List")
        }

        // Open pantry
        OutlinedButton(
            onClick = onPantryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Inventory2,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Open Pantry")
        }
    }
}

// Reusable dashboard summary card
@Composable
private fun DashboardCard(
    title: String,
    itemCount: Int,
    description: String,
    actionText: String,
    icon: ImageVector,
    highlighted: Boolean,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),

        // Select the appropriate card background
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
            contentColor = if (highlighted) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Card icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Display the current number of items
            Text(
                text = "$itemCount ${if (itemCount == 1) "item" else "items"}",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // Card description
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            // Navigation action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = actionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}