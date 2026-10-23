package com.example.ess_mich_ki.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ess_mich_ki.data.local.entity.LoggedMealItemEntity
import com.example.ess_mich_ki.data.local.model.MealWithItems
import com.example.ess_mich_ki.ui.theme.CheckedGreen
import com.example.ess_mich_ki.ui.theme.EssmichkiTheme
import kotlin.math.roundToInt

@Composable
fun MealCard(
    mealType: String,
    mealWithItems: MealWithItems?,
    onToggleCheck: (LoggedMealItemEntity, Boolean) -> Unit,
    onDeleteItem: (LoggedMealItemEntity) -> Unit,
    onEditItem: ((LoggedMealItemEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val items = mealWithItems?.items ?: emptyList()
    val checkedItems = items.filter { it.isChecked }

    val totalKcal = checkedItems.sumOf { it.calories }.roundToInt()
    val totalP = checkedItems.sumOf { it.protein }.roundToInt()
    val totalC = checkedItems.sumOf { it.carbs }.roundToInt()
    val totalF = checkedItems.sumOf { it.fat }.roundToInt()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title & Macros Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mealType,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "$totalKcal kcal • $totalP P | $totalC C | $totalF F",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            // List of Food Items
            if (items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items.forEach { item ->
                        FoodItemRow(
                            item = item,
                            onToggleCheck = { isChecked -> onToggleCheck(item, isChecked) },
                            onDelete = { onDeleteItem(item) },
                            onEdit = { onEditItem?.invoke(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodItemRow(
    item: LoggedMealItemEntity,
    onToggleCheck: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val emoji = getEmojiForFood(item.name)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Food Emoji Icon Box
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .clickable { onEdit() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Food Name & Serving (Clickable to edit)
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onEdit() }
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (item.isChecked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                textDecoration = if (!item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${item.servingName} ✏️",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }

        // Calories
        Text(
            text = "${item.calories.roundToInt()} kcal",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Interactive Green Checkmark Button
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (item.isChecked) CheckedGreen else Color.Transparent
                )
                .clickable { onToggleCheck(!item.isChecked) },
            contentAlignment = Alignment.Center
        ) {
            if (item.isChecked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Checked",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }

        // Delete button
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Delete item",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun getEmojiForFood(name: String): String {
    val lower = name.lowercase()
    return when {
        lower.contains("yogurt") || lower.contains("milk") || lower.contains("cheese") -> "🥛"
        lower.contains("chocolate") || lower.contains("sweet") -> "🍫"
        lower.contains("raspberry") || lower.contains("berry") || lower.contains("himbeere") -> "🫐"
        lower.contains("chicken") || lower.contains("meat") || lower.contains("turkey") -> "🍗"
        lower.contains("bread") || lower.contains("toast") -> "🍞"
        lower.contains("banana") || lower.contains("apple") || lower.contains("fruit") -> "🍌"
        lower.contains("almond") || lower.contains("nut") -> "🥜"
        lower.contains("coffee") || lower.contains("tea") -> "☕"
        lower.contains("egg") -> "🥚"
        lower.contains("salad") || lower.contains("vegetable") -> "🥗"
        else -> "🍽️"
    }
}

@Preview(showBackground = true)
@Composable
fun MealCardPreview() {
    val sampleMeal = MealWithItems(
        meal = com.example.ess_mich_ki.data.local.entity.LoggedMealEntity(
            id = 1,
            dateString = "2026-10-22",
            mealType = "Breakfast",
            totalCalories = 280.0
        ),
        items = listOf(
            LoggedMealItemEntity(
                id = 1,
                mealId = 1,
                name = "Soy Yogurt",
                servingName = "1 Cup (200g)",
                calories = 108.0,
                protein = 8.0,
                carbs = 4.2,
                fat = 4.6,
                isChecked = true
            ),
            LoggedMealItemEntity(
                id = 2,
                mealId = 1,
                name = "Chocolate Without Sugar",
                servingName = "25g",
                calories = 120.0,
                protein = 1.8,
                carbs = 7.5,
                fat = 9.5,
                isChecked = true
            ),
            LoggedMealItemEntity(
                id = 3,
                mealId = 1,
                name = "Himbeere Raspberry",
                servingName = "1 Portion (100g)",
                calories = 52.0,
                protein = 1.2,
                carbs = 12.0,
                fat = 0.6,
                isChecked = false
            )
        )
    )

    EssmichkiTheme {
        MealCard(
            mealType = "Breakfast",
            mealWithItems = sampleMeal,
            onToggleCheck = { _, _ -> },
            onDeleteItem = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
