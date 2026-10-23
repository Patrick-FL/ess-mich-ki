package com.example.ess_mich_ki.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ess_mich_ki.data.network.openrouter.ParsedFoodItem
import com.example.ess_mich_ki.data.network.openrouter.ParsedMealAiResult
import com.example.ess_mich_ki.ui.theme.EssmichkiTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiFoodLoggingDialog(
    selectedMealType: String,
    inputText: String,
    isLoading: Boolean,
    parsedResult: ParsedMealAiResult?,
    errorMsg: String?,
    isApiKeyMissing: Boolean,
    onMealTypeSelected: (String) -> Unit,
    onInputTextChanged: (String) -> Unit,
    onAnalyzeClick: () -> Unit,
    onUpdateParsedItem: (Int, ParsedFoodItem) -> Unit,
    onDeleteParsedItem: (Int) -> Unit,
    onSaveToMealClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    val noDismissNestedScrollConnection = remember(sheetState.currentValue) {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return if (sheetState.currentValue == SheetValue.Expanded && available.y > 0) {
                    Offset(0f, available.y)
                } else {
                    Offset.Zero
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .nestedScroll(noDismissNestedScrollConnection)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Food Logger",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log Food with AI",
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

            Spacer(modifier = Modifier.height(12.dp))

            // Meal Type Selector Chips
            Text(
                text = "Target Meal",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                mealTypes.forEach { type ->
                    FilterChip(
                        selected = type == selectedMealType,
                        onClick = { onMealTypeSelected(type) },
                        label = { Text(type) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // API Key Missing Warning Card
            if (isApiKeyMissing) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "API Key Required",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "OpenRouter API Key Required",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please enter your free OpenRouter API key in Settings to enable AI meal parsing.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                onDismiss()
                                onOpenSettingsClick()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text("Open Settings")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                // Free text input
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputTextChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    placeholder = {
                        Text("e.g. 500g Soy Yogurt with raspberries and chocolate")
                    },
                    shape = RoundedCornerShape(16.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Analyze Button
                Button(
                    onClick = onAnalyzeClick,
                    enabled = inputText.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyzing with AI...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyze & Log")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Error Message Display
                if (errorMsg != null) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Parsed Results Review Section
                if (parsedResult != null && parsedResult.items.isNotEmpty()) {
                    Text(
                        text = "Extracted Food Items (${parsedResult.totalCalories.roundToInt()} kcal)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(parsedResult.items) { index, item ->
                            ParsedFoodItemRow(
                                item = item,
                                onUpdate = { updated -> onUpdateParsedItem(index, updated) },
                                onDelete = { onDeleteParsedItem(index) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Save to Meal Button
                    Button(
                        onClick = onSaveToMealClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(
                            text = "Save to $selectedMealType",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ParsedFoodItemRow(
    item: ParsedFoodItem,
    onUpdate: (ParsedFoodItem) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.servingName} • ${item.calories.roundToInt()} kcal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "P: ${item.proteinGrams.roundToInt()}g | C: ${item.carbsGrams.roundToInt()}g | F: ${item.fatGrams.roundToInt()}g",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Remove Item",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quantity adjust row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Menge: ",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(
                    onClick = {
                        val newQuantity = (item.quantity - 0.5).coerceAtLeast(0.1)
                        val factor = newQuantity / item.quantity.coerceAtLeast(0.1)
                        val updated = item.copy(
                            quantity = newQuantity,
                            calories = item.calories * factor,
                            proteinGrams = item.proteinGrams * factor,
                            carbsGrams = item.carbsGrams * factor,
                            fatGrams = item.fatGrams * factor,
                            sugarsGrams = item.sugarsGrams * factor,
                            fiberGrams = item.fiberGrams * factor,
                            saltGrams = item.saltGrams * factor,
                            satFatGrams = item.satFatGrams * factor
                        )
                        onUpdate(updated)
                    },
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = if (item.quantity % 1.0 == 0.0) "${item.quantity.toInt()}" else "%.1f".format(item.quantity),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                IconButton(
                    onClick = {
                        val newQuantity = item.quantity + 0.5
                        val factor = newQuantity / item.quantity.coerceAtLeast(0.1)
                        val updated = item.copy(
                            quantity = newQuantity,
                            calories = item.calories * factor,
                            proteinGrams = item.proteinGrams * factor,
                            carbsGrams = item.carbsGrams * factor,
                            fatGrams = item.fatGrams * factor,
                            sugarsGrams = item.sugarsGrams * factor,
                            fiberGrams = item.fiberGrams * factor,
                            saltGrams = item.saltGrams * factor,
                            satFatGrams = item.satFatGrams * factor
                        )
                        onUpdate(updated)
                    },
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AiFoodLoggingDialogPreview() {
    val sampleResult = ParsedMealAiResult(
        items = listOf(
            ParsedFoodItem(
                name = "Soy Yogurt",
                servingName = "500g",
                calories = 270.0,
                proteinGrams = 20.0,
                carbsGrams = 10.5,
                fatGrams = 11.5
            )
        ),
        totalCalories = 270.0
    )

    EssmichkiTheme {
        AiFoodLoggingDialog(
            selectedMealType = "Breakfast",
            inputText = "500g Soy Yogurt with raspberries",
            isLoading = false,
            parsedResult = sampleResult,
            errorMsg = null,
            isApiKeyMissing = false,
            onMealTypeSelected = {},
            onInputTextChanged = {},
            onAnalyzeClick = {},
            onUpdateParsedItem = { _, _ -> },
            onDeleteParsedItem = {},
            onSaveToMealClick = {},
            onOpenSettingsClick = {},
            onDismiss = {}
        )
    }
}
