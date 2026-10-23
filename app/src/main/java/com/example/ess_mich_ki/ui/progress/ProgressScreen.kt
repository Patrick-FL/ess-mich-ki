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
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorWeight
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.ess_mich_ki.data.local.entity.UserGoalEntity
import com.example.ess_mich_ki.data.local.entity.WeightLogEntity
import com.example.ess_mich_ki.ui.progress.components.AddWeightDialog
import com.example.ess_mich_ki.ui.progress.components.EditGoalDialog
import com.example.ess_mich_ki.ui.progress.components.ManageWeightLogsDialog
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
        onOpenManageWeight = viewModel::openManageWeightDialog,
        onCloseManageWeight = viewModel::closeManageWeightDialog,
        onSaveGoal = viewModel::saveGoal,
        onSaveWeight = viewModel::saveWeight,
        onDeleteWeight = viewModel::deleteWeight,
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
    onOpenManageWeight: () -> Unit = {},
    onCloseManageWeight: () -> Unit = {},
    onSaveGoal: (UserGoalEntity) -> Unit,
    onSaveWeight: (Double, String) -> Unit,
    onDeleteWeight: (WeightLogEntity) -> Unit = {},
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

            // Scale Weight Line Graph Chart Card (Option B)
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
                        val logs = uiState.weightLogs

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MonitorWeight,
                                    contentDescription = "Scale Weight",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Scale Weight",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                // Net weight change badge
                                if (logs.size >= 2) {
                                    val diff = logs.last().weightKg - logs.first().weightKg
                                    val diffText = if (diff > 0) "+%.1f kg".format(diff) else "%.1f kg".format(diff)
                                    val isLoss = diff <= 0

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isLoss) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                                        contentColor = if (isLoss) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(start = 8.dp)
                                    ) {
                                        Text(
                                            text = diffText,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onOpenManageWeight,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                                        contentDescription = "Gewichtsverlauf verwalten",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

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
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (logs.size < 2) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MonitorWeight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (logs.isEmpty()) {
                                            "Noch keine Gewichtseinträge vorhanden."
                                        } else {
                                            "Aktuelles Gewicht: ${"%.1f kg".format(logs.first().weightKg)}"
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Trage mindestens 2 Gewichte ein, um deinen Verlauf zu sehen.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            // Enhanced Option B Line Chart Canvas
                            val textMeasurer = rememberTextMeasurer()
                            val primaryColor = MaterialTheme.colorScheme.primary
                            val surfaceColor = MaterialTheme.colorScheme.surface
                            val onSurfaceColor = MaterialTheme.colorScheme.onSurface
                            val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
                            val goalLineColor = MaterialTheme.colorScheme.secondary

                            val valueStyle = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = onSurfaceColor
                            )
                            val dateStyle = MaterialTheme.typography.labelSmall.copy(
                                color = onSurfaceVariantColor
                            )
                            val goalStyle = MaterialTheme.typography.labelSmall.copy(
                                color = goalLineColor,
                                fontWeight = FontWeight.Medium
                            )

                            val targetWeight = uiState.targetWeight
                            val logWeights = logs.map { it.weightKg }
                            val minLog = logWeights.minOrNull() ?: 60.0
                            val maxLog = logWeights.maxOrNull() ?: 70.0

                            val isGoalInRange = targetWeight in (minLog - 5.0)..(maxLog + 5.0)
                            val minW = if (isGoalInRange) minOf(minLog, targetWeight) - 0.6 else minLog - 0.6
                            val maxW = if (isGoalInRange) maxOf(maxLog, targetWeight) + 0.6 else maxLog + 0.6
                            val rangeW = (maxW - minW).coerceAtLeast(0.5)

                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            ) {
                                val paddingX = 36.dp.toPx()
                                val paddingTop = 28.dp.toPx()
                                val paddingBottom = 28.dp.toPx()
                                val chartWidth = size.width - 2 * paddingX
                                val chartHeight = size.height - paddingTop - paddingBottom

                                val points = logs.mapIndexed { index, log ->
                                    val x = paddingX + (chartWidth / (logs.size - 1)) * index
                                    val y = paddingTop + chartHeight - ((log.weightKg - minW) / rangeW * chartHeight).toFloat()
                                    Offset(x, y)
                                }

                                // 1. Goal reference line (dashed)
                                if (isGoalInRange) {
                                    val goalY = paddingTop + chartHeight - ((targetWeight - minW) / rangeW * chartHeight).toFloat()
                                    if (goalY in (paddingTop - 10f)..(paddingTop + chartHeight + 10f)) {
                                        drawLine(
                                            color = goalLineColor.copy(alpha = 0.5f),
                                            start = Offset(paddingX, goalY),
                                            end = Offset(size.width - paddingX, goalY),
                                            strokeWidth = 1.5.dp.toPx(),
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                        )
                                        val goalTextLayout = textMeasurer.measure(
                                            text = "Ziel: ${"%.1f kg".format(targetWeight)}",
                                            style = goalStyle
                                        )
                                        val gW = goalTextLayout.size.width.toFloat()
                                        val gH = goalTextLayout.size.height.toFloat()
                                        drawText(
                                            textLayoutResult = goalTextLayout,
                                            topLeft = Offset(
                                                x = size.width - paddingX - gW,
                                                y = goalY - gH - 2.dp.toPx()
                                            )
                                        )
                                    }
                                }

                                // 2. Smooth Bezier Curve Path & Fill
                                val strokePath = Path()
                                val fillPath = Path()

                                strokePath.moveTo(points.first().x, points.first().y)
                                fillPath.moveTo(points.first().x, points.first().y)

                                for (i in 0 until points.size - 1) {
                                    val p1 = points[i]
                                    val p2 = points[i + 1]
                                    val controlX1 = p1.x + (p2.x - p1.x) / 2f
                                    val controlY1 = p1.y
                                    val controlX2 = p1.x + (p2.x - p1.x) / 2f
                                    val controlY2 = p2.y

                                    strokePath.cubicTo(controlX1, controlY1, controlX2, controlY2, p2.x, p2.y)
                                    fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, p2.x, p2.y)
                                }

                                val bottomY = paddingTop + chartHeight
                                fillPath.lineTo(points.last().x, bottomY)
                                fillPath.lineTo(points.first().x, bottomY)
                                fillPath.close()

                                // Draw gradient fill below curve
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            primaryColor.copy(alpha = 0.25f),
                                            primaryColor.copy(alpha = 0.01f)
                                        ),
                                        startY = paddingTop,
                                        endY = bottomY
                                    )
                                )

                                // Draw curve stroke
                                drawPath(
                                    path = strokePath,
                                    color = primaryColor,
                                    style = Stroke(
                                        width = 3.dp.toPx(),
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )

                                // 3. Draw Points, Weight Labels & Date Labels
                                points.forEachIndexed { i, pt ->
                                    val log = logs[i]

                                    // Outer colored circle
                                    drawCircle(
                                        color = primaryColor,
                                        radius = 6.dp.toPx(),
                                        center = pt
                                    )
                                    // Inner surface circle
                                    drawCircle(
                                        color = surfaceColor,
                                        radius = 3.dp.toPx(),
                                        center = pt
                                    )

                                    // Weight label above point
                                    val weightText = "%.1f kg".format(log.weightKg)
                                    val weightLayout = textMeasurer.measure(
                                        text = weightText,
                                        style = valueStyle
                                    )
                                    val wW = weightLayout.size.width.toFloat()
                                    val wH = weightLayout.size.height.toFloat()
                                    drawText(
                                        textLayoutResult = weightLayout,
                                        topLeft = Offset(
                                            x = pt.x - wW / 2f,
                                            y = pt.y - wH - 4.dp.toPx()
                                        )
                                    )

                                    // Date label below point
                                    val dateText = formatWeightDate(log.dateString)
                                    val dateLayout = textMeasurer.measure(
                                        text = dateText,
                                        style = dateStyle
                                    )
                                    val dW = dateLayout.size.width.toFloat()
                                    drawText(
                                        textLayoutResult = dateLayout,
                                        topLeft = Offset(
                                            x = pt.x - dW / 2f,
                                            y = paddingTop + chartHeight + 6.dp.toPx()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialogs
        if (uiState.isManageWeightShowing) {
            ManageWeightLogsDialog(
                weightLogs = uiState.weightLogs,
                onDeleteWeight = onDeleteWeight,
                onOpenAddWeight = onOpenAddWeight,
                onDismiss = onCloseManageWeight
            )
        }

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

private fun formatWeightDate(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        val today = LocalDate.now()
        when {
            date == today -> "Heute"
            date == today.minusDays(1) -> "Gestern"
            else -> date.format(DateTimeFormatter.ofPattern("dd.MM."))
        }
    } catch (_: Exception) {
        dateString
    }
}



