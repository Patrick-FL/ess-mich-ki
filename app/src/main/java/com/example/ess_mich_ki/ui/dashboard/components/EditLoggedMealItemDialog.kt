package com.example.ess_mich_ki.ui.dashboard.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ess_mich_ki.data.local.entity.LoggedMealItemEntity
import com.example.ess_mich_ki.ui.detail.MacroStatCard
import com.example.ess_mich_ki.ui.theme.CarbsColor
import com.example.ess_mich_ki.ui.theme.FatColor
import com.example.ess_mich_ki.ui.theme.ProteinColor
import com.example.ess_mich_ki.util.ServingUnitUtils
import kotlin.math.roundToInt

data class PureUnitOption(
    val unitKey: String,
    val displayLabel: String,
    val gramWeightPerUnit: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLoggedMealItemDialog(
    item: LoggedMealItemEntity,
    onSave: (LoggedMealItemEntity, Double, String, Double) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var quantity by remember(item) { mutableDoubleStateOf(item.quantity) }
    var quantityInputText by remember(item) {
        mutableStateOf(ServingUnitUtils.formatQuantity(item.quantity))
    }

    val initialCleanUnit = remember(item) { ServingUnitUtils.sanitizeServingName(item.servingName) }
    val initialGramWeight = remember(item) {
        if (item.servingGrams > 0.0) item.servingGrams else ServingUnitUtils.getStandardGramWeightForUnit(initialCleanUnit)
    }

    var selectedUnitKey by remember(item) { mutableStateOf(initialCleanUnit) }
    var selectedUnitGramWeight by remember(item) { mutableDoubleStateOf(initialGramWeight) }
    var showServingDropdown by remember { mutableStateOf(false) }

    val presetUnits = remember(item) {
        listOf(
            PureUnitOption(initialCleanUnit, initialCleanUnit, initialGramWeight),
            PureUnitOption("piece", "piece / Stück", 15.0),
            PureUnitOption("g", "g / Gramm", 1.0),
            PureUnitOption("ml", "ml / Milliliter", 1.0),
            PureUnitOption("tsp", "tsp / Teelöffel", 5.0),
            PureUnitOption("tbsp", "tbsp / Esslöffel", 15.0),
            PureUnitOption("cup", "cup / Tasse", 200.0),
            PureUnitOption("portion", "portion / Portion", 100.0)
        ).distinctBy { it.unitKey }
    }

    // Base calculations
    val oldTotalGrams = (item.quantity * item.servingGrams).coerceAtLeast(0.1)
    val newTotalGrams = (quantity * selectedUnitGramWeight).coerceAtLeast(0.0)
    val scaleFactor = newTotalGrams / oldTotalGrams

    val calculatedCalories = (item.calories * scaleFactor).coerceAtLeast(0.0)
    val calculatedProtein = (item.protein * scaleFactor).coerceAtLeast(0.0)
    val calculatedCarbs = (item.carbs * scaleFactor).coerceAtLeast(0.0)
    val calculatedFat = (item.fat * scaleFactor).coerceAtLeast(0.0)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Logged Item",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Edit ${item.name}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }

            // Macro Stats Preview Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroStatCard(
                    label = "Calories",
                    value = "${calculatedCalories.roundToInt()} kcal",
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                MacroStatCard(
                    label = "Protein",
                    value = "${calculatedProtein.roundToInt()} g",
                    accentColor = ProteinColor,
                    modifier = Modifier.weight(1f)
                )
                MacroStatCard(
                    label = "Carbs",
                    value = "${calculatedCarbs.roundToInt()} g",
                    accentColor = CarbsColor,
                    modifier = Modifier.weight(1f)
                )
                MacroStatCard(
                    label = "Fat",
                    value = "${calculatedFat.roundToInt()} g",
                    accentColor = FatColor,
                    modifier = Modifier.weight(1f)
                )
            }

            // Quantity & Portion Selection Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Portion & Quantity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Quantity Stepper + Direct Text Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Quantity:",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    val newQ = (quantity - 0.5).coerceAtLeast(0.1)
                                    quantity = newQ
                                    quantityInputText = if (newQ % 1.0 == 0.0) newQ.toInt().toString() else "%.1f".format(newQ)
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease")
                            }

                            OutlinedTextField(
                                value = quantityInputText,
                                onValueChange = { input ->
                                    quantityInputText = input
                                    val parsed = input.toDoubleOrNull()
                                    if (parsed != null && parsed > 0.0) {
                                        quantity = parsed
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(52.dp)
                            )

                            IconButton(
                                onClick = {
                                    val newQ = quantity + 0.5
                                    quantity = newQ
                                    quantityInputText = if (newQ % 1.0 == 0.0) newQ.toInt().toString() else "%.1f".format(newQ)
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Increase")
                            }
                        }
                    }

                    // Serving / Unit Dropdown Selector
                    Box {
                        OutlinedButton(
                            onClick = { showServingDropdown = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val totalG = (quantity * selectedUnitGramWeight).roundToInt()
                                Text("Unit: $selectedUnitKey (~${totalG}g total)")
                                Icon(Icons.Default.ExpandMore, contentDescription = "Select unit")
                            }
                        }

                        DropdownMenu(
                            expanded = showServingDropdown,
                            onDismissRequest = { showServingDropdown = false }
                        ) {
                            presetUnits.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.displayLabel) },
                                    onClick = {
                                        selectedUnitKey = option.unitKey
                                        selectedUnitGramWeight = option.gramWeightPerUnit
                                        showServingDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Save Changes Button
            Button(
                onClick = {
                    onSave(item, quantity, selectedUnitKey, selectedUnitGramWeight)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC107), // Yellow theme
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Save Changes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
