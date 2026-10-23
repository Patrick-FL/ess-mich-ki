package com.example.ess_mich_ki.ui.progress.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ess_mich_ki.data.local.entity.UserGoalEntity
import kotlin.math.roundToInt

@Composable
fun EditGoalDialog(
    initialUserGoal: UserGoalEntity?,
    onSaveGoal: (UserGoalEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var initialWeightStr by remember { mutableStateOf(initialUserGoal?.initialWeight?.toString() ?: "64.2") }
    var currentWeightStr by remember { mutableStateOf(initialUserGoal?.currentWeight?.toString() ?: "63.0") }
    var goalWeightStr by remember { mutableStateOf(initialUserGoal?.goalWeight?.toString() ?: "59.0") }
    var selectedGoalType by remember { mutableStateOf(initialUserGoal?.goalType ?: "Fat Loss") }

    var paceWeeks by remember { mutableIntStateOf(initialUserGoal?.paceWeeks ?: 5) }

    val goalTypes = listOf("Fat Loss", "Weight Maintenance", "Muscle Gain")

    // Dynamic daily calorie calculation based on goal and pace
    val calculatedCalorieTarget = remember(goalWeightStr, currentWeightStr, paceWeeks, selectedGoalType) {
        val curr = currentWeightStr.toDoubleOrNull() ?: 63.0
        val target = goalWeightStr.toDoubleOrNull() ?: 59.0
        val diff = curr - target

        val bmr = 10 * curr + 6.25 * 170 - 5 * 28 + 5 // Mifflin-St Jeor estimate
        val tdee = bmr * 1.35

        when (selectedGoalType) {
            "Fat Loss" -> {
                val weeklyDeficit = (diff * 7700) / paceWeeks.coerceAtLeast(1)
                val dailyDeficit = weeklyDeficit / 7
                (tdee - dailyDeficit).roundToInt().coerceIn(1200, 3000)
            }
            "Muscle Gain" -> {
                (tdee + 300).roundToInt()
            }
            else -> tdee.roundToInt()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Goal & Pace",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Goal Type Selector
                Column {
                    Text(
                        text = "Goal Type",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        goalTypes.forEachIndexed { index, type ->
                            SegmentedButton(
                                selected = selectedGoalType == type,
                                onClick = { selectedGoalType = type },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = goalTypes.size)
                            ) {
                                Text(type, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                // Initial, Current & Goal Weight Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = initialWeightStr,
                        onValueChange = { initialWeightStr = it },
                        label = { Text("Initial") },
                        suffix = { Text("kg") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = currentWeightStr,
                        onValueChange = { currentWeightStr = it },
                        label = { Text("Current") },
                        suffix = { Text("kg") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = goalWeightStr,
                        onValueChange = { goalWeightStr = it },
                        label = { Text("Goal") },
                        suffix = { Text("kg") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Time to Goal Pace Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Time to Goal:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "$paceWeeks weeks",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Slider(
                        value = paceWeeks.toFloat(),
                        onValueChange = { paceWeeks = it.roundToInt() },
                        valueRange = 2f..24f,
                        steps = 22,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Fast (Strict)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Recommended", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Text("Slow (Easy)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Calculated Calorie Target Summary Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Target Daily Intake",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "$calculatedCalorieTarget kcal / day",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Save Goal Button
                Button(
                    onClick = {
                        val initW = initialWeightStr.toDoubleOrNull() ?: 64.2
                        val currW = currentWeightStr.toDoubleOrNull() ?: 63.0
                        val goalW = goalWeightStr.toDoubleOrNull() ?: 59.0

                        val updated = UserGoalEntity(
                            id = 1,
                            goalType = selectedGoalType,
                            initialWeight = initW,
                            currentWeight = currW,
                            goalWeight = goalW,
                            paceWeeks = paceWeeks,
                            calorieTarget = calculatedCalorieTarget,
                            proteinTarget = initialUserGoal?.proteinTarget ?: 113,
                            carbsTarget = initialUserGoal?.carbsTarget ?: 163,
                            fatTarget = initialUserGoal?.fatTarget ?: 53,
                            isFastingDay = initialUserGoal?.isFastingDay ?: false,
                            startDateString = initialUserGoal?.startDateString ?: "2026-10-01"
                        )
                        onSaveGoal(updated)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Goal", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
