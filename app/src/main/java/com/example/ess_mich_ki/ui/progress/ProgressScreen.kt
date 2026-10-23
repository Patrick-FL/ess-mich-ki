package com.example.ess_mich_ki.ui.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ess_mich_ki.data.local.entity.UserGoalEntity
import com.example.ess_mich_ki.data.local.entity.WeightLogEntity
import com.example.ess_mich_ki.ui.progress.components.AddWeightDialog
import com.example.ess_mich_ki.ui.progress.components.EditGoalDialog
import com.example.ess_mich_ki.ui.theme.CheckedGreen
import com.example.ess_mich_ki.ui.theme.EssmichkiTheme

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    ProgressContent(
        uiState = uiState,
        onOpenEditGoal = viewModel::openEditGoalDialog,
        onCloseEditGoal = viewModel::closeEditGoalDialog,
        onOpenAddWeight = viewModel::openAddWeightDialog,
        onCloseAddWeight = viewModel::closeAddWeightDialog,
        onSaveGoal = viewModel::saveGoal,
        onSaveWeight = viewModel::saveWeight,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressContent(
    uiState: ProgressUiState,
    onOpenEditGoal: () -> Unit,
    onCloseEditGoal: () -> Unit,
    onOpenAddWeight: () -> Unit,
    onCloseAddWeight: () -> Unit,
    onSaveGoal: (UserGoalEntity) -> Unit,
    onSaveWeight: (Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Progress & Goals",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. "Nutrition Score" Section
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Nutrition Score",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CheckedGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Great Compliance",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CheckedGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Weekly Calendar Row (M, T, W, T, F, S, S)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            uiState.weeklyCalorieStats.forEach { stat ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = stat.dayLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (stat.isCompliant) CheckedGreen else MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (stat.isCompliant) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Compliant",
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "-",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. "Goal" Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFEBEE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Goal",
                                        tint = Color.Red,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Goal: ${uiState.userGoal?.goalType ?: "Fat Loss"}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${uiState.userGoal?.calorieTarget ?: 1284} kcal / day target",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(onClick = onOpenEditGoal) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Goal",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Weight Progress Bar / Range Slider Bar
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Target: ${uiState.targetWeight} kg",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = CheckedGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Current: ${uiState.currentWeight} kg",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(fraction = uiState.progressPercentage.coerceIn(0.1f, 1f))
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = onOpenEditGoal,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+ Update Progress / Edit Goal", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. "Stats" Section Header
            item {
                Text(
                    text = "STATS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            // Calories Mini Bar Chart Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = "Calories Stat",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Calories",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "${uiState.averageWeeklyCalories} kcal avg",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Mini 7-Day Bar Chart
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            uiState.weeklyCalorieStats.forEach { stat ->
                                val maxKcal = (stat.targetCalories * 1.2).coerceAtLeast(1500.0)
                                val fillRatio = (stat.calories / maxKcal).toFloat().coerceIn(0.1f, 1f)

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(16.dp)
                                            .fillMaxHeight(fraction = fillRatio)
                                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                            .background(
                                                if (stat.calories > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stat.dayLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Scale Weight Line Graph Chart Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = "Scale Weight",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Scale Weight",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = onOpenAddWeight,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Weight",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Weight", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Line Chart Canvas
                        val primaryColor = MaterialTheme.colorScheme.primary
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        ) {
                            val logs = uiState.weightLogs
                            if (logs.size >= 2) {
                                val minW = (logs.minOf { it.weightKg } - 1.0).coerceAtLeast(40.0)
                                val maxW = (logs.maxOf { it.weightKg } + 1.0).coerceAtMost(150.0)
                                val rangeW = (maxW - minW).coerceAtLeast(1.0)

                                val points = logs.mapIndexed { index, weightLog ->
                                    val x = (size.width / (logs.size - 1)) * index
                                    val y = size.height - ((weightLog.weightKg - minW) / rangeW * size.height).toFloat()
                                    Offset(x, y)
                                }

                                val path = Path()
                                path.moveTo(points.first().x, points.first().y)
                                for (i in 1 until points.size) {
                                    path.lineTo(points[i].x, points[i].y)
                                }

                                drawPath(
                                    path = path,
                                    color = primaryColor,
                                    style = Stroke(width = 4.dp.toPx())
                                )

                                points.forEach { pt ->
                                    drawCircle(
                                        color = primaryColor,
                                        radius = 6.dp.toPx(),
                                        center = pt
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialogs
        if (uiState.isEditGoalShowing) {
            EditGoalDialog(
                initialUserGoal = uiState.userGoal,
                onSaveGoal = onSaveGoal,
                onDismiss = onCloseEditGoal
            )
        }

        if (uiState.isAddWeightShowing) {
            AddWeightDialog(
                currentWeight = uiState.currentWeight,
                onSaveWeight = onSaveWeight,
                onDismiss = onCloseAddWeight
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressScreenPreview() {
    val sampleState = ProgressUiState(
        userGoal = UserGoalEntity(
            id = 1,
            goalType = "Fat Loss",
            initialWeight = 64.2,
            currentWeight = 63.0,
            goalWeight = 59.0,
            paceWeeks = 5,
            calorieTarget = 1284,
            startDateString = "2026-10-01"
        ),
        weeklyCalorieStats = listOf(
            DailyCalorieStat("M", "2026-10-19", 632, 1284, true),
            DailyCalorieStat("T", "2026-10-20", 1150, 1284, true),
            DailyCalorieStat("W", "2026-10-21", 1200, 1284, true),
            DailyCalorieStat("T", "2026-10-22", 632, 1284, true),
            DailyCalorieStat("F", "2026-10-23", 0, 1284, false),
            DailyCalorieStat("S", "2026-10-24", 0, 1284, false),
            DailyCalorieStat("S", "2026-10-25", 0, 1284, false)
        ),
        averageWeeklyCalories = 632,
        weightLogs = listOf(
            WeightLogEntity(dateString = "2026-10-01", weightKg = 64.2),
            WeightLogEntity(dateString = "2026-10-10", weightKg = 63.8),
            WeightLogEntity(dateString = "2026-10-22", weightKg = 63.0)
        )
    )

    EssmichkiTheme {
        ProgressContent(
            uiState = sampleState,
            onOpenEditGoal = {},
            onCloseEditGoal = {},
            onOpenAddWeight = {},
            onCloseAddWeight = {},
            onSaveGoal = {},
            onSaveWeight = { _, _ -> }
        )
    }
}
