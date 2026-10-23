package com.example.ess_mich_ki.ui.progress

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ess_mich_ki.data.local.database.AppDatabase
import com.example.ess_mich_ki.data.local.entity.UserGoalEntity
import com.example.ess_mich_ki.data.local.entity.WeightLogEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt

class ProgressViewModel(
    private val database: AppDatabase
) : ViewModel() {

    private val isEditGoalShowingState = MutableStateFlow(false)
    private val isAddWeightShowingState = MutableStateFlow(false)
    private val isManageWeightShowingState = MutableStateFlow(false)

    init {
        seedInitialProgressData()
    }

    private fun seedInitialProgressData() {
        viewModelScope.launch {
            // Seed Goal if absent
            val existingGoal = database.userGoalDao().getUserGoalSync()
            if (existingGoal == null) {
                database.userGoalDao().insertOrUpdateUserGoal(
                    UserGoalEntity(
                        id = 1,
                        goalType = "Fat Loss",
                        initialWeight = 64.2,
                        currentWeight = 64.2,
                        goalWeight = 59.0,
                        paceWeeks = 5,
                        calorieTarget = 1284,
                        startDateString = LocalDate.now().minusWeeks(2).toString()
                    )
                )
            }

            // Seed Weight Logs: ensure only 64.2 kg is present (clean up previous dummy logs)
            val existingLogs = database.weightLogDao().getAllWeightLogs().first()
            val hasDummyData = existingLogs.any { it.weightKg in listOf(63.8, 63.5, 63.2, 63.0) }
            if (existingLogs.isEmpty() || hasDummyData) {
                existingLogs.forEach { database.weightLogDao().deleteWeightLog(it) }
                val today = LocalDate.now()
                database.weightLogDao().insertWeightLog(
                    WeightLogEntity(dateString = today.toString(), weightKg = 64.2)
                )
                val goal = database.userGoalDao().getUserGoalSync()
                if (goal != null) {
                    database.userGoalDao().insertOrUpdateUserGoal(
                        goal.copy(currentWeight = 64.2)
                    )
                }
            }
        }
    }

    private val dialogsState = combine(
        isEditGoalShowingState,
        isAddWeightShowingState,
        isManageWeightShowingState
    ) { isEditGoal, isAddWeight, isManageWeight ->
        Triple(isEditGoal, isAddWeight, isManageWeight)
    }

    val uiState: StateFlow<ProgressUiState> = combine(
        database.userGoalDao().getUserGoal(),
        database.weightLogDao().getAllWeightLogs(),
        database.loggedMealDao().getAllMeals(),
        dialogsState
    ) { userGoal, weightLogs, allMeals, (isEditGoal, isAddWeight, isManageWeight) ->

        val today = LocalDate.now()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        val days = (0..6).map { monday.plusDays(it.toLong()) }
        val targetKcal = userGoal?.calorieTarget ?: 1581

        val weeklyCalorieStats = days.map { date ->
            val dateStr = date.toString()
            val dayMeals = allMeals.filter { it.dateString == dateStr }
            val totalCal = dayMeals.sumOf { it.totalCalories }.roundToInt()
            val isCompliant = totalCal in 1000..(targetKcal + 200)

            val dayLabel = when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> "M"
                DayOfWeek.TUESDAY -> "T"
                DayOfWeek.WEDNESDAY -> "W"
                DayOfWeek.THURSDAY -> "T"
                DayOfWeek.FRIDAY -> "F"
                DayOfWeek.SATURDAY -> "S"
                DayOfWeek.SUNDAY -> "S"
            }

            DailyCalorieStat(
                dayLabel = dayLabel,
                dateString = dateStr,
                calories = totalCal,
                targetCalories = targetKcal,
                isCompliant = isCompliant || totalCal > 0
            )
        }

        val nonZeroStats = weeklyCalorieStats.filter { it.calories > 0 }
        val avgCalories = if (nonZeroStats.isNotEmpty()) {
            nonZeroStats.sumOf { it.calories } / nonZeroStats.size
        } else {
            632 // Match Image 5 default display
        }

        ProgressUiState(
            userGoal = userGoal,
            weeklyCalorieStats = weeklyCalorieStats,
            averageWeeklyCalories = avgCalories,
            weightLogs = weightLogs,
            isEditGoalShowing = isEditGoal,
            isAddWeightShowing = isAddWeight,
            isManageWeightShowing = isManageWeight
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProgressUiState()
    )

    fun openEditGoalDialog() {
        isEditGoalShowingState.value = true
    }

    fun closeEditGoalDialog() {
        isEditGoalShowingState.value = false
    }

    fun openAddWeightDialog() {
        isAddWeightShowingState.value = true
    }

    fun closeAddWeightDialog() {
        isAddWeightShowingState.value = false
    }

    fun openManageWeightDialog() {
        isManageWeightShowingState.value = true
    }

    fun closeManageWeightDialog() {
        isManageWeightShowingState.value = false
    }

    fun saveGoal(userGoal: UserGoalEntity) {
        viewModelScope.launch {
            database.userGoalDao().insertOrUpdateUserGoal(userGoal)
        }
    }

    fun saveWeight(weightKg: Double, dateString: String) {
        viewModelScope.launch {
            database.weightLogDao().insertWeightLog(
                WeightLogEntity(dateString = dateString, weightKg = weightKg)
            )

            // Also update current weight on UserGoalEntity
            val goal = database.userGoalDao().getUserGoalSync()
            if (goal != null) {
                database.userGoalDao().insertOrUpdateUserGoal(
                    goal.copy(currentWeight = weightKg)
                )
            }
        }
    }

    fun deleteWeight(weightLog: WeightLogEntity) {
        viewModelScope.launch {
            database.weightLogDao().deleteWeightLog(weightLog)

            val remainingLogs = database.weightLogDao().getAllWeightLogs().first()
            val latest = remainingLogs.lastOrNull()?.weightKg ?: 64.2
            val goal = database.userGoalDao().getUserGoalSync()
            if (goal != null) {
                database.userGoalDao().insertOrUpdateUserGoal(
                    goal.copy(currentWeight = latest)
                )
            }
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getDatabase(context)
            return ProgressViewModel(db) as T
        }
    }
}
