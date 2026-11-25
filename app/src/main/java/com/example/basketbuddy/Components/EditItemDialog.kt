package com.example.basketbuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// Reusable dialog for editing shopping and pantry items
@Composable
fun EditItemDialog(
    initialName: String,
    initialQuantity: Int,
    minimumQuantity: Int,
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit
) {

    // Store the editable item name
    var itemName by rememberSaveable {
        mutableStateOf(initialName)
    }

    // Store the editable item quantity
    var quantityInput by rememberSaveable {
        mutableStateOf(initialQuantity.toString())
    }

    // Convert the quantity input into an integer
    val quantity = quantityInput.toIntOrNull()

    // Validate the item name
    val isNameValid = itemName.isNotBlank()

    // Validate the item quantity
    val isQuantityValid =
        quantity != null && quantity >= minimumQuantity

    AlertDialog(

        // Close the dialog without saving
        onDismissRequest = onDismiss,

        // Dialog title
        title = {
            Text("Edit Item")
        },

        // Editable item information
        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(16.dp)
            ) {

                // Item name input
                OutlinedTextField(
                    value = itemName,
                    onValueChange = {
                        itemName = it
                    },
                    label = {
                        Text("Item name")
                    },
                    singleLine = true,
                    isError = !isNameValid,
                    modifier = Modifier.fillMaxWidth(),

                    supportingText = {
                        if (!isNameValid) {
                            Text("Item name cannot be empty.")
                        }
                    }
                )

                // Item quantity input
                OutlinedTextField(
                    value = quantityInput,
                    onValueChange = {
                        quantityInput = it
                    },
                    label = {
                        Text("Quantity")
                    },
                    singleLine = true,
                    isError = !isQuantityValid,
                    modifier = Modifier.fillMaxWidth(),

                    // Display the numeric keyboard
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),

                    supportingText = {
                        if (!isQuantityValid) {
                            Text(
                                "Quantity must be at least " +
                                        "$minimumQuantity."
                            )
                        }
                    }
                )
            }
        },

        // Save valid changes
        confirmButton = {

            Button(
                enabled = isNameValid && isQuantityValid,
                onClick = {

                    quantity?.let { validQuantity ->

                        onSave(
                            itemName.trim(),
                            validQuantity
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },

        // Cancel without modifying the item
        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}