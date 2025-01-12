package com.example.todo.database

class ItemRepository(private val dao: ItemDao) {
    suspend fun getItems(): List<Item> = dao.getAllItems()
    suspend fun addItem(item: Item) = dao.insertItem(item)
    suspend fun checkItem(item: Item) = dao.updateItem(item.id, true)
    suspend fun removeItem(item: Item) = dao.deleteItem(item.id)
}