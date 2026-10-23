package com.example.ess_mich_ki.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.ess_mich_ki.data.local.entity.LoggedMealEntity
import com.example.ess_mich_ki.data.local.entity.LoggedMealItemEntity

data class MealWithItems(
    @Embedded val meal: LoggedMealEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "mealId"
    )
    val items: List<LoggedMealItemEntity>
)
