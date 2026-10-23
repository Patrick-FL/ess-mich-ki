package com.example.ess_mich_ki.ui.progress.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ess_mich_ki.ui.theme.CarbsColor
import com.example.ess_mich_ki.ui.theme.EssmichkiTheme
import com.example.ess_mich_ki.ui.theme.FatColor
import com.example.ess_mich_ki.ui.theme.ProteinColor
import com.example.ess_mich_ki.util.NutritionalGoalMath
import com.example.ess_mich_ki.util.NutritionalGoalMath.NutritionalGoalState
import kotlin.math.roundToInt

/**
 * Interactive Dialog for dynamically configuring daily nutritional goals.
 *
 * Core Features:
 * 1. 4 Interactive Sliders (Total Kcal, Protein, Carbohydrates, Fats).
 * 2. Locking / Pinning mechanism:
 *    - Adjusting Total Kcal proportionally scales all unlocked macros.
 *    - Adjusting a macro when Total Kcal is LOCKED redistributes the calorie delta across remaining unlocked macros.
 *    - Adjusting a macro when Total Kcal is UNLOCKED recalculates Total Kcal dynamically.
 * 3. Constraint Handling:
 *    - Automatically locks / disables the 3rd macro slider when Total Kcal and 2 macros are locked.
 *    - Guarantees non-negative values.
 * 4. "Heute ist Fastentag" (Fasting Day) Toggle:
 *    - Temporarily reduces effective target values by 50%.
 *    - Restores exact 100% baseline values without rounding drift when deactivated.
 */
@Composable
fun NutritionalGoalsDialog(
    initialKcal: Int = 1581,
    initialProtein: Int = 113,
    initialCarbs: Int = 163,
    initialFat: Int = 53,
    initialIsFastingDay: Boolean = false,
    onSaveGoals: (kcal: Int, protein: Int, carbs: Int, fat: Int, isFastingDay: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var goalState by remember {
        mutableStateOf(
            NutritionalGoalState(
                totalKcal = initialKcal,
                proteinGrams = initialProtein,
                carbsGrams = initialCarbs,
                fatGrams = initialFat,
                isFastingDay = initialIsFastingDay
            )
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Nährwertziele anpassen",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Interaktive Sliders & Sperren",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Schließen")
                    }
                }

                // Fasting Day Toggle Card ("Heute ist Fastentag")
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (goalState.isFastingDay) {
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (goalState.isFastingDay) MaterialTheme.colorScheme.tertiary
                                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = if (goalState.isFastingDay) MaterialTheme.colorScheme.onTertiary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Heute ist Fastentag",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = if (goalState.isFastingDay) "Zielwerte temporär um 50% halbiert"
                                    else "Zielwerte auf 50% reduzieren",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = goalState.isFastingDay,
                            onCheckedChange = {
                                goalState = NutritionalGoalMath.toggleFastingDay(goalState)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.tertiary,
                                checkedTrackColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        )
                    }
                }

                HorizontalDivider()

                // Macro Split Percentage Bar Visualization
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Makronährstoff-Verteilung",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "1g P=4kcal | 1g C=4kcal | 1g F=9kcal",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Multi-colored macro bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        val pPct = goalState.proteinKcalPercentage.coerceAtLeast(0)
                        val cPct = goalState.carbsKcalPercentage.coerceAtLeast(0)
                        val fPct = goalState.fatKcalPercentage.coerceAtLeast(0)

                        if (pPct > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(pPct.toFloat().coerceAtLeast(0.001f))
                                    .height(12.dp)
                                    .background(ProteinColor)
                            )
                        }
                        if (cPct > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(cPct.toFloat().coerceAtLeast(0.001f))
                                    .height(12.dp)
                                    .background(CarbsColor)
                            )
                        }
                        if (fPct > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(fPct.toFloat().coerceAtLeast(0.001f))
                                    .height(12.dp)
                                    .background(FatColor)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "P: ${goalState.proteinKcalPercentage}%",
                            color = ProteinColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "C: ${goalState.carbsKcalPercentage}%",
                            color = CarbsColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "F: ${goalState.fatKcalPercentage}%",
                            color = FatColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // --- 1. TOTAL CALORIES SLIDER ---
                MacroSliderItem(
                    label = "Gesamtkalorien",
                    displayValue = "${goalState.effectiveKcal} kcal",
                    subText = if (goalState.isFastingDay) "(Basis: ${goalState.totalKcal} kcal)" else null,
                    value = goalState.totalKcal.toFloat(),
                    valueRange = 800f..4000f,
                    accentColor = MaterialTheme.colorScheme.primary,
                    isLocked = goalState.isKcalLocked,
                    isDisabled = goalState.isFastingDay, // Sliders disabled during Fasting Day
                    onLockToggle = { goalState = NutritionalGoalMath.toggleLockKcal(goalState) },
                    onValueChange = { newKcal ->
                        goalState = NutritionalGoalMath.updateTotalKcal(goalState, newKcal.roundToInt())
                    }
                )

                // --- 2. PROTEIN SLIDER ---
                MacroSliderItem(
                    label = "Proteine",
                    displayValue = "${goalState.effectiveProteinGrams} g",
                    subText = "${goalState.effectiveProteinGrams * 4} kcal (${goalState.proteinKcalPercentage}%)",
                    value = goalState.proteinGrams.toFloat(),
                    valueRange = 0f..300f,
                    accentColor = ProteinColor,
                    isLocked = goalState.isProteinAutoLocked,
                    isDisabled = goalState.isFastingDay || goalState.isProteinAutoLocked,
                    onLockToggle = { goalState = NutritionalGoalMath.toggleLockProtein(goalState) },
                    onValueChange = { newProtein ->
                        goalState = NutritionalGoalMath.updateProtein(goalState, newProtein.roundToInt())
                    }
                )

                // --- 3. CARBOHYDRATES SLIDER ---
                MacroSliderItem(
                    label = "Kohlenhydrate",
                    displayValue = "${goalState.effectiveCarbsGrams} g",
                    subText = "${goalState.effectiveCarbsGrams * 4} kcal (${goalState.carbsKcalPercentage}%)",
                    value = goalState.carbsGrams.toFloat(),
                    valueRange = 0f..500f,
                    accentColor = CarbsColor,
                    isLocked = goalState.isCarbsAutoLocked,
                    isDisabled = goalState.isFastingDay || goalState.isCarbsAutoLocked,
                    onLockToggle = { goalState = NutritionalGoalMath.toggleLockCarbs(goalState) },
                    onValueChange = { newCarbs ->
                        goalState = NutritionalGoalMath.updateCarbs(goalState, newCarbs.roundToInt())
                    }
                )

                // --- 4. FATS SLIDER ---
                MacroSliderItem(
                    label = "Fette",
                    displayValue = "${goalState.effectiveFatGrams} g",
                    subText = "${goalState.effectiveFatGrams * 9} kcal (${goalState.fatKcalPercentage}%)",
                    value = goalState.fatGrams.toFloat(),
                    valueRange = 0f..200f,
                    accentColor = FatColor,
                    isLocked = goalState.isFatAutoLocked,
                    isDisabled = goalState.isFastingDay || goalState.isFatAutoLocked,
                    onLockToggle = { goalState = NutritionalGoalMath.toggleLockFat(goalState) },
                    onValueChange = { newFat ->
                        goalState = NutritionalGoalMath.updateFat(goalState, newFat.roundToInt())
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Abbrechen")
                    }

                    Button(
                        onClick = {
                            onSaveGoals(
                                goalState.totalKcal,
                                goalState.proteinGrams,
                                goalState.carbsGrams,
                                goalState.fatGrams,
                                goalState.isFastingDay
                            )
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Speichern", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroSliderItem(
    label: String,
    displayValue: String,
    subText: String?,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    accentColor: Color,
    isLocked: Boolean,
    isDisabled: Boolean,
    onLockToggle: () -> Unit,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onLockToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = if (isLocked) "$label entsperren" else "$label sperren",
                        tint = if (isLocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                if (isLocked) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(Fixiert)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = displayValue,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                if (subText != null) {
                    Text(
                        text = subText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            enabled = !isDisabled,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = accentColor.copy(alpha = 0.24f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NutritionalGoalsDialogPreview() {
    EssmichkiTheme {
        NutritionalGoalsDialog(
            initialKcal = 1581,
            initialProtein = 113,
            initialCarbs = 163,
            initialFat = 53,
            initialIsFastingDay = false,
            onSaveGoals = { _, _, _, _, _ -> },
            onDismiss = {}
        )
    }
}
