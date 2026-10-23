package com.example.ess_mich_ki.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "logged_meal_items",
    foreignKeys = [
        ForeignKey(
            entity = LoggedMealEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mealId"]),
        Index(value = ["foodItemId"])
    ]
)
data class LoggedMealItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mealId: Long,
    val foodItemId: Long? = null,
    val name: String,
    val quantity: Double = 1.0,
    val servingName: String = "Serving",
    val servingGrams: Double = 100.0,
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val isChecked: Boolean = true
)
