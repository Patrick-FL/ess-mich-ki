package com.example.ess_mich_ki.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logged_meals")
data class LoggedMealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateString: String, // e.g. "2026-10-22"
    val mealType: String,   // e.g. "Breakfast", "Lunch", "Dinner", "Snack"
    val totalCalories: Double = 0.0,
    val totalProtein: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val totalFat: Double = 0.0
)
