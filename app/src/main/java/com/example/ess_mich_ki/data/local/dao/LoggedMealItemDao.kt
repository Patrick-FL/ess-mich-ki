package com.example.ess_mich_ki.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ess_mich_ki.data.local.entity.LoggedMealItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoggedMealItemDao {
    @Query("SELECT * FROM logged_meal_items WHERE mealId = :mealId ORDER BY id ASC")
    fun getItemsForMeal(mealId: Long): Flow<List<LoggedMealItemEntity>>

    @Query("SELECT * FROM logged_meal_items WHERE mealId = :mealId ORDER BY id ASC")
    suspend fun getItemsForMealSync(mealId: Long): List<LoggedMealItemEntity>

    @Query("SELECT * FROM logged_meal_items WHERE id = :id")
    suspend fun getItemById(id: Long): LoggedMealItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: LoggedMealItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<LoggedMealItemEntity>)

    @Update
    suspend fun updateItem(item: LoggedMealItemEntity)

    @Delete
    suspend fun deleteItem(item: LoggedMealItemEntity)

    @Query("DELETE FROM logged_meal_items WHERE mealId = :mealId")
    suspend fun deleteItemsForMeal(mealId: Long)
}
