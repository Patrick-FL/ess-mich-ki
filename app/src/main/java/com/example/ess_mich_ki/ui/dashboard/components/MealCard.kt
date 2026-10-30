package com.example.ess_mich_ki.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ess_mich_ki.data.local.entity.LoggedMealItemEntity
import com.example.ess_mich_ki.data.local.model.MealWithItems
import com.example.ess_mich_ki.ui.theme.EssmichkiTheme
import com.example.ess_mich_ki.ui.theme.NothingRed
import com.example.ess_mich_ki.util.ServingUnitUtils
import kotlin.math.roundToInt

@Composable
fun MealCard(
    mealType: String,
    mealWithItems: MealWithItems?,
    onDeleteItem: (LoggedMealItemEntity) -> Unit,
    onEditItem: ((LoggedMealItemEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val items = mealWithItems?.items ?: emptyList()

    val totalKcal = items.sumOf { it.calories }.roundToInt()
    val totalP = items.sumOf { it.protein }.roundToInt()
    val totalC = items.sumOf { it.carbs }.roundToInt()
    val totalF = items.sumOf { it.fat }.roundToInt()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Title & Macros Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(NothingRed, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = mealType.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.2.sp
                    )
                }

                Text(
                    text = "$totalKcal KCAL • ${totalP}P | ${totalC}C | ${totalF}F",
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // List of Food Items
            if (items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items.forEach { item ->
                        FoodItemRow(
                            item = item,
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
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val emoji = getEmojiForFood(item.name)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Emoji Icon Circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onEdit() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Food Name & Serving
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onEdit() }
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${ServingUnitUtils.formatServingDisplay(item.quantity, item.servingName, item.servingGrams)} ✏️",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Calories
            Text(
                text = "${item.calories.roundToInt()} KCAL",
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(30.dp)
                    .background(NothingRed.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Eintrag löschen",
                    tint = NothingRed,
                    modifier = Modifier.size(16.dp)
                )
            }
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
            mealType = "Frühstück",
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
            )
        )
    )

    EssmichkiTheme {
        MealCard(
            mealType = "Frühstück",
            mealWithItems = sampleMeal,
            onDeleteItem = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
