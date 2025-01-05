package com.example.projekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.projekt.database.Item
import com.example.projekt.database.ItemRepository
import com.example.projekt.ui.theme.ShoppinglistTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = ItemRepository(MyApp.database.itemDao())
            val viewModel = ItemViewModel(repository)
            ShoppinglistTheme {
                ItemsScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(viewModel: ItemViewModel) {
    val showAddItemDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Seznam ingrediencí") }
            )
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = {
                        viewModel.deleteCheckedItems()
                    },
                    Modifier.padding(0.dp, 12.dp),
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Remove checked items")
                }
                FloatingActionButton(
                    onClick = {
                        showAddItemDialog.value = true
                    }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Item")
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).padding(8.dp, 0.dp).fillMaxWidth()) {
            items(viewModel.items.value.size) { index ->
                ItemCard(viewModel.items.value[index], itemVM = viewModel)
            }
        }

        if (showAddItemDialog.value) {
            AddItemDialog(
                onDismiss = { showAddItemDialog.value = false },
                onAddItem = { item ->
                    viewModel.addItem(item)
                }
            )
        }
    }
}

@Composable
fun ItemCard(item: Item, itemVM: ItemViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(0.dp, 4.dp),
        onClick = {
            itemVM.checkItem(item)
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.name, textDecoration = if (item.isChecked) TextDecoration.LineThrough else null)
            Text(text = item.quantity, textDecoration = if (item.isChecked) TextDecoration.LineThrough else null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onAddItem: (Item) -> Unit
) {
    var itemName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Přidat položku") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = {
                        itemName = it
                        isError = false
                    },
                    label = { Text("Název") },
                    isError = isError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        quantity = it
                        isError = false
                    },
                    label = { Text("Množství") },
                    isError = isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isError) {
                    Text(
                        text = "Vyplňte všechna pole",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (itemName.isNotBlank() && quantity.isNotBlank()) {
                        onAddItem(Item(
                            name = itemName,
                            quantity = quantity,
                            isChecked = false
                        ))
                        onDismiss()
                    } else {
                        isError = true
                    }
                }
            ) {
                Text("Přidat")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Zrušit")
            }
        }
    )
}
