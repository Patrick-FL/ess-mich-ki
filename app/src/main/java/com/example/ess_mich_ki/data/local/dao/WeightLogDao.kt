package com.example.ess_mich_ki.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ess_mich_ki.data.local.entity.WeightLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightLogDao {
    @Query("SELECT * FROM weight_logs ORDER BY dateString ASC")
    fun getAllWeightLogs(): Flow<List<WeightLogEntity>>

    @Query("SELECT * FROM weight_logs WHERE dateString BETWEEN :startDate AND :endDate ORDER BY dateString ASC")
    fun getWeightLogsByDateRange(startDate: String, endDate: String): Flow<List<WeightLogEntity>>

    @Query("SELECT * FROM weight_logs ORDER BY dateString DESC LIMIT 1")
    suspend fun getLatestWeightLog(): WeightLogEntity?

    @Query("SELECT * FROM weight_logs WHERE dateString = :dateString LIMIT 1")
    suspend fun getWeightLogForDate(dateString: String): WeightLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(weightLog: WeightLogEntity): Long

    @Update
    suspend fun updateWeightLog(weightLog: WeightLogEntity)

    @Delete
    suspend fun deleteWeightLog(weightLog: WeightLogEntity)

    @Query("DELETE FROM weight_logs WHERE dateString = :dateString")
    suspend fun deleteWeightLogByDate(dateString: String)
}
