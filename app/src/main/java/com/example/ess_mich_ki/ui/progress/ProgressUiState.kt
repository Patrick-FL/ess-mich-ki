package com.example.ess_mich_ki.ui.progress

import com.example.ess_mich_ki.data.local.entity.UserGoalEntity
import com.example.ess_mich_ki.data.local.entity.WeightLogEntity

data class DailyCalorieStat(
    val dayLabel: String,  // "M", "T", "W", "T", "F", "S", "S"
    val dateString: String,
    val calories: Int,
    val targetCalories: Int,
    val isCompliant: Boolean
)

data class ProgressUiState(
    val userGoal: UserGoalEntity? = null,
    val weeklyCalorieStats: List<DailyCalorieStat> = emptyList(),
    val averageWeeklyCalories: Int = 0,
    val weightLogs: List<WeightLogEntity> = emptyList(),
    val isEditGoalShowing: Boolean = false,
    val isAddWeightShowing: Boolean = false
) {
    val currentWeight: Double
        get() = weightLogs.lastOrNull()?.weightKg ?: userGoal?.currentWeight ?: 63.0

    val initialWeight: Double
        get() = userGoal?.initialWeight ?: 64.2

    val targetWeight: Double
        get() = userGoal?.goalWeight ?: 59.0

    val progressPercentage: Float
        get() {
            val totalToLose = initialWeight - targetWeight
            if (totalToLose <= 0) return 1f
            val currentLost = initialWeight - currentWeight
            return (currentLost / totalToLose).toFloat().coerceIn(0f, 1f)
        }
}
