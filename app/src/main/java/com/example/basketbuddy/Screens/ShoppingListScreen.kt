package com.example.basketbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.basketbuddy.data.model.ShoppingItem
import com.example.basketbuddy.ui.components.EditItemDialog

// Main shopping list screen
@Composable
fun ShoppingListScreen(
    shoppingItems: List<ShoppingItem>,
    onAddItem: (String) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onQuantityChange: (Long, Int) -> Unit,
    onPurchasedChange: (Long, Boolean) -> Unit,
    onTransferToPantry: () -> Unit,
    onEditItem: (Long, String, Int) -> Unit
) {

    // Store the current text input
    var itemName by rememberSaveable {
        mutableStateOf("")
    }

    // Store the item currently being edited
    var editingItem by remember {
        mutableStateOf<ShoppingItem?>(null)
    }

    // Display the edit dialog when an item is selected
    editingItem?.let { item ->

        EditItemDialog(
            initialName = item.name,
            initialQuantity = item.quantity,
            minimumQuantity = 1,

            // Close the dialog without saving
            onDismiss = {
                editingItem = null
            },

            // Save the updated item information
            onSave = { name, quantity ->

                onEditItem(
                    item.id,
                    name,
                    quantity
                )

                editingItem = null
            }
        )
    }

    // Count items that have not been purchased
    val remainingItems = shoppingItems.count {
        !it.isPurchased
    }

    // Count items that are ready to transfer
    val purchasedItems = shoppingItems.count {
        it.isPurchased
    }

    // Determine whether items can be transferred
    val canTransferItems = purchasedItems > 0

    // Validate and submit a new shopping item
    val addItem = {

        if (itemName.isNotBlank()) {

            onAddItem(itemName.trim())

            // Clear the input after adding an item
            itemName = ""
        }
    }

    // Main shopping list layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Screen heading
        Text(
            text = "Shopping List",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        // Display remaining shopping items
        Text(
            text = "$remainingItems ${
                if (remainingItems == 1) "item" else "items"
            } remaining",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // New shopping item input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Item name input field
            OutlinedTextField(
                value = itemName,

                onValueChange = {
                    itemName = it
                },

                label = {
                    Text("Item name")
                },

                singleLine = true,
                modifier = Modifier.weight(1f),

                // Allow submission using the keyboard
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),

                keyboardActions = KeyboardActions(
                    onDone = {
                        addItem()
                    }
                )
            )

            // Add item button
            FilledIconButton(
                onClick = {
                    addItem()
                },

                // Prevent blank items from being added
                enabled = itemName.isNotBlank(),

                modifier = Modifier.size(48.dp)
            ) {

                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add shopping item"
                )
            }
        }

        // Transfer purchased shopping items into the pantry
        Button(
            onClick = onTransferToPantry,
            enabled = canTransferItems,

            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {

            // Pantry transfer icon
            Icon(
                imageVector = Icons.Filled.Inventory2,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Display the number of items ready to transfer
            Text(
                text = if (canTransferItems) {

                    "Transfer $purchasedItems to Pantry"

                } else {

                    "Transfer to Pantry"
                }
            )
        }

        // Display an empty state when no items exist
        if (shoppingItems.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                contentAlignment = Alignment.Center
            ) {

                ShoppingListEmptyState()
            }

        } else {

            // Scrollable shopping list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // Display each shopping item
                items(
                    items = shoppingItems,
                    key = { it.id }
                ) { item ->

                    ShoppingItemCard(
                        item = item,

                        // Open the edit dialog for this item
                        onEdit = {
                            editingItem = item
                        },

                        // Delete the selected shopping item
                        onDelete = {
                            onDeleteItem(item.id)
                        },

                        // Increase the item quantity
                        onIncreaseQuantity = {

                            // Prevent integer overflow
                            if (item.quantity < Int.MAX_VALUE) {

                                onQuantityChange(
                                    item.id,
                                    item.quantity + 1
                                )
                            }
                        },

                        // Decrease the item quantity
                        onDecreaseQuantity = {

                            // Prevent quantities below one
                            if (item.quantity > 1) {

                                onQuantityChange(
                                    item.id,
                                    item.quantity - 1
                                )
                            }
                        },

                        // Update the purchase status
                        onPurchasedChange = { purchased ->

                            onPurchasedChange(
                                item.id,
                                purchased
                            )
                        }
                    )
                }
            }
        }
    }
}

// Individual shopping list item card
@Composable
private fun ShoppingItemCard(
    item: ShoppingItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onPurchasedChange: (Boolean) -> Unit
) {

    // Shopping item card container
    Card(
        modifier = Modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            // Item name, purchase status, edit and delete buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Purchased item checkbox
                Checkbox(
                    checked = item.isPurchased,
                    onCheckedChange = onPurchasedChange
                )

                // Display the shopping item name
                Text(
                    text = item.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,

                    // Cross out purchased items
                    textDecoration = if (item.isPurchased) {

                        TextDecoration.LineThrough

                    } else {

                        TextDecoration.None
                    },

                    // Change the text color for purchased items
                    color = if (item.isPurchased) {

                        MaterialTheme.colorScheme.onSurfaceVariant

                    } else {

                        MaterialTheme.colorScheme.onSurface
                    }
                )

                // Edit the existing shopping item
                IconButton(
                    onClick = onEdit
                ) {

                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit ${item.name}",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Delete the existing shopping item
                IconButton(
                    onClick = onDelete
                ) {

                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete ${item.name}",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Quantity controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Quantity label
                Text(
                    text = "Quantity:",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Decrease quantity button
                IconButton(
                    onClick = onDecreaseQuantity,

                    // Disable when the quantity reaches one
                    enabled = item.quantity > 1
                ) {

                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Decrease quantity"
                    )
                }

                // Display the current item quantity
                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium
                )

                // Increase quantity button
                IconButton(
                    onClick = onIncreaseQuantity,

                    // Prevent integer overflow
                    enabled = item.quantity < Int.MAX_VALUE
                ) {

                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Increase quantity"
                    )
                }
            }
        }
    }
}

// Displayed when the shopping list is empty
@Composable
private fun ShoppingListEmptyState() {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Empty shopping list icon
        Icon(
            imageVector = Icons.Filled.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        // Empty shopping list heading
        Text(
            text = "Your shopping list is empty",
            style = MaterialTheme.typography.titleMedium
        )

        // Instructions for adding the first item
        Text(
            text = "Add an item to get started.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}