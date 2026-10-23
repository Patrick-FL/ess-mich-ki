package com.example.ess_mich_ki.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ess_mich_ki.data.local.entity.FoodItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {
    @Query("SELECT * FROM food_items ORDER BY name ASC")
    fun getAllFoods(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchFoods(query: String): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteFoods(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getFoodById(id: Long): FoodItemEntity?

    @Query("SELECT COUNT(*) FROM food_items")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoods(foods: List<FoodItemEntity>)

    @Update
    suspend fun updateFood(food: FoodItemEntity)

    @Delete
    suspend fun deleteFood(food: FoodItemEntity)
}
