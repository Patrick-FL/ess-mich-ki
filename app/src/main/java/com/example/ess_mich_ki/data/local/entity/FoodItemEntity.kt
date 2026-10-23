package com.example.ess_mich_ki.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val calories: Double,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatGrams: Double,
    val sugarsGrams: Double = 0.0,
    val fiberGrams: Double = 0.0,
    val saltGrams: Double = 0.0,
    val satFatGrams: Double = 0.0,
    val servingName: String,
    val servingGrams: Double,
    val isFavorite: Boolean = false,
    val isVerified: Boolean = false
)
