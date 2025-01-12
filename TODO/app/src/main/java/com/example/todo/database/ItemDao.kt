package com.example.todo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDao {
    @Insert
    suspend fun insertItem(item: Item): Long

    @Query("SELECT * FROM items")
    suspend fun getAllItems(): List<Item>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getUserById(id: Int): Item?

    @Query("UPDATE items SET isChecked = :isChecked WHERE id = :id")
    suspend fun updateItem(id: Int, isChecked: Boolean)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteItem(id: Int)
}