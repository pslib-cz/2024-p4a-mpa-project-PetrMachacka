package com.example.todo

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.database.Item
import com.example.todo.database.ItemRepository
import kotlinx.coroutines.launch

class ItemViewModel(private val repository: ItemRepository) : ViewModel() {
    val items = mutableStateOf<List<Item>>(emptyList())

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            items.value = repository.getItems()
        }
    }

    fun addItem(item: Item) {
        viewModelScope.launch {
            repository.addItem(item)
            loadItems()
        }
    }

    fun checkItem(item: Item) {
        viewModelScope.launch {
            repository.checkItem(item)
            loadItems()
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.removeItem(item)
            loadItems()
        }
    }

    fun deleteCheckedItems() {
        viewModelScope.launch {
            for (item in items.value) {
                if (!item.isChecked) continue
                repository.removeItem(item)
            }
            loadItems()
        }
    }
}