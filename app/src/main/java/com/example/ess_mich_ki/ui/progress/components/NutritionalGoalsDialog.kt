package com.example.ess_mich_ki.ui.progress.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ess_mich_ki.ui.theme.CarbsColor
import com.example.ess_mich_ki.ui.theme.EssmichkiTheme
import com.example.ess_mich_ki.ui.theme.FatColor
import com.example.ess_mich_ki.ui.theme.NothingRed
import com.example.ess_mich_ki.ui.theme.ProteinColor
import com.example.ess_mich_ki.util.NutritionalGoalMath
import com.example.ess_mich_ki.util.NutritionalGoalMath.NutritionalGoalState
import kotlin.math.roundToInt

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
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(NothingRed, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NÄHRWERTZIELE",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = 1.2.sp
                            )
                        }
                        Text(
                            text = "INTERAKTIVE SLIDER & SPERSYSTEM",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Schließen",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Fasting Day Toggle Card ("Heute ist Fastentag")
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (goalState.isFastingDay) {
                            NothingRed.copy(alpha = 0.15f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                    border = BorderStroke(1.dp, if (goalState.isFastingDay) NothingRed else MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(20.dp),
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
                                        if (goalState.isFastingDay) NothingRed else MaterialTheme.colorScheme.surface
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = if (goalState.isFastingDay) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "HEUTE IST FASTENTAG",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (goalState.isFastingDay) "Zielwerte temporär um 50% halbiert"
                                    else "Zielwerte um 50% reduzieren",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
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
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NothingRed,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                // Macro Split Percentage Bar Visualization
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "VERTEILUNG",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 0.8.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "1g P=4kcal | 1g C=4kcal | 1g F=9kcal",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Multi-colored macro bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        val pPct = goalState.proteinKcalPercentage.coerceAtLeast(0)
                        val cPct = goalState.carbsKcalPercentage.coerceAtLeast(0)
                        val fPct = goalState.fatKcalPercentage.coerceAtLeast(0)

                        if (pPct > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(pPct.toFloat().coerceAtLeast(0.001f))
                                    .height(10.dp)
                                    .background(ProteinColor)
                            )
                        }
                        if (cPct > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(cPct.toFloat().coerceAtLeast(0.001f))
                                    .height(10.dp)
                                    .background(CarbsColor)
                            )
                        }
                        if (fPct > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(fPct.toFloat().coerceAtLeast(0.001f))
                                    .height(10.dp)
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
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "C: ${goalState.carbsKcalPercentage}%",
                            color = CarbsColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "F: ${goalState.fatKcalPercentage}%",
                            color = FatColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // --- 1. TOTAL CALORIES SLIDER ---
                MacroSliderItem(
                    label = "GESAMTKALORIEN",
                    displayValue = "${goalState.effectiveKcal} KCAL",
                    subText = if (goalState.isFastingDay) "(Basis: ${goalState.totalKcal} kcal)" else null,
                    value = goalState.totalKcal.toFloat(),
                    valueRange = 800f..4000f,
                    accentColor = NothingRed,
                    isLocked = goalState.isKcalLocked,
                    isDisabled = goalState.isFastingDay,
                    onLockToggle = { goalState = NutritionalGoalMath.toggleLockKcal(goalState) },
                    onValueChange = { newKcal ->
                        goalState = NutritionalGoalMath.updateTotalKcal(goalState, newKcal.roundToInt())
                    }
                )

                // --- 2. PROTEIN SLIDER ---
                MacroSliderItem(
                    label = "PROTEIN",
                    displayValue = "${goalState.effectiveProteinGrams} G",
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
                    label = "CARBS",
                    displayValue = "${goalState.effectiveCarbsGrams} G",
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
                    label = "FETT",
                    displayValue = "${goalState.effectiveFatGrams} G",
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
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = CircleShape
                    ) {
                        Text("Abbrechen", color = MaterialTheme.colorScheme.onSurface)
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
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NothingRed,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Speichern", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
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
                        tint = if (isLocked) NothingRed else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 0.8.sp
                )

                if (isLocked) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(FIX)",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = NothingRed,
                        fontSize = 10.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = displayValue,
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                if (subText != null) {
                    Text(
                        text = subText,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
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
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
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
