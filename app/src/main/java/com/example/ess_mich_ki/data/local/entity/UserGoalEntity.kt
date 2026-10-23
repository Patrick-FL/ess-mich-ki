package com.example.ess_mich_ki.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_goals")
data class UserGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 1,
    val goalType: String, // e.g. "Fat Loss", "Weight Maintenance", "Muscle Gain"
    val initialWeight: Double,
    val currentWeight: Double,
    val goalWeight: Double,
    val paceWeeks: Int = 12,
    val calorieTarget: Int = 2000,
    val startDateString: String
)
