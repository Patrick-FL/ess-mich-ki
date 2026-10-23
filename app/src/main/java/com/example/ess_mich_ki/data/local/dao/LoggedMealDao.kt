package com.example.ess_mich_ki.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.ess_mich_ki.data.local.entity.LoggedMealEntity
import com.example.ess_mich_ki.data.local.model.MealWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface LoggedMealDao {
    @Query("SELECT * FROM logged_meals WHERE dateString = :dateString ORDER BY id ASC")
    fun getMealsByDate(dateString: String): Flow<List<LoggedMealEntity>>

    @Query("SELECT * FROM logged_meals WHERE dateString = :dateString ORDER BY id ASC")
    suspend fun getMealsByDateSync(dateString: String): List<LoggedMealEntity>

    @Transaction
    @Query("SELECT * FROM logged_meals WHERE dateString = :dateString ORDER BY id ASC")
    fun getMealsWithItemsByDate(dateString: String): Flow<List<MealWithItems>>

    @Transaction
    @Query("SELECT * FROM logged_meals WHERE dateString = :dateString AND mealType = :mealType LIMIT 1")
    suspend fun getMealWithItemsByDateAndType(dateString: String, mealType: String): MealWithItems?

    @Query("SELECT * FROM logged_meals ORDER BY dateString DESC")
    fun getAllMeals(): Flow<List<LoggedMealEntity>>

    @Query("SELECT * FROM logged_meals WHERE id = :id")
    suspend fun getMealById(id: Long): LoggedMealEntity?

    @Transaction
    @Query("SELECT * FROM logged_meals WHERE id = :id")
    suspend fun getMealWithItemsById(id: Long): MealWithItems?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: LoggedMealEntity): Long

    @Update
    suspend fun updateMeal(meal: LoggedMealEntity)

    @Delete
    suspend fun deleteMeal(meal: LoggedMealEntity)

    @Query("DELETE FROM logged_meals WHERE id = :id")
    suspend fun deleteMealById(id: Long)
}
