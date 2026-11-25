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
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.basketbuddy.data.model.PantryItem
import com.example.basketbuddy.ui.components.EditItemDialog

// Main pantry inventory screen
@Composable
fun PantryScreen(
    pantryItems: List<PantryItem>,
    onAddItem: (String) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onQuantityChange: (Long, Int) -> Unit,
    onEditItem: (Long, String, Int) -> Unit
) {

    // Store the current item name input
    var itemName by rememberSaveable {
        mutableStateOf("")
    }

    // Store the item currently being edited
    var editingItem by remember {
        mutableStateOf<PantryItem?>(null)
    }

    // Display the edit dialog when an item is selected
    editingItem?.let { item ->

        EditItemDialog(
            initialName = item.name,
            initialQuantity = item.quantity,

            // Pantry items can have a quantity of zero
            minimumQuantity = 0,

            // Close the dialog without saving
            onDismiss = {
                editingItem = null
            },

            // Save the updated pantry item
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

    // Calculate current inventory totals
    val inStockCount = pantryItems.count {
        !it.isOutOfStock
    }

    val outOfStockCount = pantryItems.count {
        it.isOutOfStock
    }

    // Validate and submit a new pantry item
    val addItem = {

        if (itemName.isNotBlank()) {

            onAddItem(itemName.trim())

            // Clear the input after adding an item
            itemName = ""
        }
    }

    // Main pantry screen layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Screen heading
        Text(
            text = "Pantry",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        // Pantry inventory summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // In-stock inventory count
            PantrySummaryCard(
                title = "In Stock",
                count = inStockCount,
                isOutOfStock = false,
                modifier = Modifier.weight(1f)
            )

            // Out-of-stock inventory count
            PantrySummaryCard(
                title = "Out of Stock",
                count = outOfStockCount,
                isOutOfStock = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Pantry item input
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

            // Add pantry item button
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
                    contentDescription = "Add pantry item"
                )
            }
        }

        // Display an empty state if no pantry items exist
        if (pantryItems.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                PantryEmptyState()
            }

        } else {

            // Scrollable pantry inventory
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // Display each pantry item
                items(
                    items = pantryItems,
                    key = { it.id }
                ) { item ->

                    PantryItemCard(
                        item = item,

                        // Open the edit dialog for this item
                        onEdit = {
                            editingItem = item
                        },

                        // Delete the selected pantry item
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

                            // Prevent negative quantities
                            if (item.quantity > 0) {

                                onQuantityChange(
                                    item.id,
                                    item.quantity - 1
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

// Displays an inventory summary count
@Composable
private fun PantrySummaryCard(
    title: String,
    count: Int,
    isOutOfStock: Boolean,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,

        colors = CardDefaults.cardColors(
            containerColor = if (isOutOfStock) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            // Display the summary title
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,

                color = if (isOutOfStock) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                }
            )

            // Display the current inventory count
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineLarge,

                color = if (isOutOfStock) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                }
            )
        }
    }
}

// Displays an individual pantry inventory item
@Composable
private fun PantryItemCard(
    item: PantryItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit
) {

    // Pantry item card container
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

            // Item information, edit and delete buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Pantry item icon
                Icon(
                    imageVector = Icons.Filled.Inventory2,
                    contentDescription = null,

                    tint = if (item.isOutOfStock) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },

                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Item name and availability status
                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    // Display the pantry item name
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Display the current inventory status
                    Text(
                        text = if (item.isOutOfStock) {
                            "Out of stock"
                        } else {
                            "In stock"
                        },

                        style = MaterialTheme.typography.bodyMedium,

                        color = if (item.isOutOfStock) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }

                // Edit the existing pantry item
                IconButton(
                    onClick = onEdit
                ) {

                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit ${item.name}",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Permanently remove the pantry item
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

            // Pantry quantity controls
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

                // Decrease item quantity
                IconButton(
                    onClick = onDecreaseQuantity,

                    // Disable when the quantity reaches zero
                    enabled = item.quantity > 0
                ) {

                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Decrease quantity"
                    )
                }

                // Display current quantity
                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium
                )

                // Increase item quantity
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

// Displayed when the pantry is empty
@Composable
private fun PantryEmptyState() {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Empty pantry icon
        Icon(
            imageVector = Icons.Filled.Inventory2,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        // Empty pantry heading
        Text(
            text = "Your pantry is empty",
            style = MaterialTheme.typography.titleMedium
        )

        // Instructions for adding the first pantry item
        Text(
            text = "Add an item to start tracking your inventory.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}