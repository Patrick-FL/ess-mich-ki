package com.example.ess_mich_ki.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ess_mich_ki.data.local.entity.UserGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserGoalDao {
    @Query("SELECT * FROM user_goals WHERE id = 1 LIMIT 1")
    fun getUserGoal(): Flow<UserGoalEntity?>

    @Query("SELECT * FROM user_goals WHERE id = 1 LIMIT 1")
    suspend fun getUserGoalSync(): UserGoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserGoal(userGoal: UserGoalEntity)

    @Delete
    suspend fun deleteUserGoal(userGoal: UserGoalEntity)
}
