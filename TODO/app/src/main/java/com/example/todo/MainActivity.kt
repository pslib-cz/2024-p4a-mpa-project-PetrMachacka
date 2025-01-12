package com.example.todo

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
import com.example.todo.database.Item
import com.example.todo.database.ItemRepository
import com.example.todo.ui.theme.TodoListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = ItemRepository(MyApp.database.itemDao())
            val viewModel = ItemViewModel(repository)
            TodoListTheme {
                TasksScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(viewModel: ItemViewModel) {
    val showAddTaskDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("To-Do List") }
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
                    Icon(Icons.Filled.Delete, contentDescription = "Remove completed tasks")
                }
                FloatingActionButton(
                    onClick = {
                        showAddTaskDialog.value = true
                    }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Task")
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).padding(8.dp, 0.dp).fillMaxWidth()) {
            items(viewModel.items.value.size) { index ->
                TaskCard(viewModel.items.value[index], taskVM = viewModel)
            }
        }

        if (showAddTaskDialog.value) {
            AddTaskDialog(
                onDismiss = { showAddTaskDialog.value = false },
                onAddTask = { task ->
                    viewModel.addItem(task)
                }
            )
        }
    }
}

@Composable
fun TaskCard(task: Item, taskVM: ItemViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(0.dp, 4.dp),
        onClick = {
            taskVM.checkItem(task)
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.name, textDecoration = if (task.isChecked) TextDecoration.LineThrough else null)
            Text(text = task.quantity, textDecoration = if (task.isChecked) TextDecoration.LineThrough else null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (Item) -> Unit
) {
    var taskName by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = taskName,
                    onValueChange = {
                        taskName = it
                        isError = false
                    },
                    label = { Text("Task Name") },
                    isError = isError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = details,
                    onValueChange = {
                        details = it
                        isError = false
                    },
                    label = { Text("Details") },
                    isError = isError,
                    keyboardOptions = KeyboardOptions.Default,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isError) {
                    Text(
                        text = "Please fill in all fields",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (taskName.isNotBlank() && details.isNotBlank()) {
                        onAddTask(Item(
                            name = taskName,
                            quantity = details,
                            isChecked = false
                        ))
                        onDismiss()
                    } else {
                        isError = true
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
