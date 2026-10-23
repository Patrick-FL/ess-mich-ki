package com.example.ess_mich_ki.ui.dashboard.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ess_mich_ki.ui.theme.CarbsColor
import com.example.ess_mich_ki.ui.theme.EssmichkiTheme
import com.example.ess_mich_ki.ui.theme.FatColor
import com.example.ess_mich_ki.ui.theme.ProteinColor
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CalorieGaugeCard(
    calorieConsumed: Int,
    calorieTarget: Int,
    calorieMinRange: Int,
    calorieMaxRange: Int,
    proteinConsumed: Int,
    proteinTarget: Int,
    carbsConsumed: Int,
    carbsTarget: Int,
    fatConsumed: Int,
    fatTarget: Int,
    modifier: Modifier = Modifier,
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary
    val inRangeColor = Color(0xFF2E7D32) // Rich green for in-range / target zone
    val targetBandColor = Color(0xFF81C784).copy(alpha = 0.4f) // Target range band highlight

    // Maximum value for gauge scale (220 degree total sweep)
    val maxScale = maxOf(
        calorieMaxRange * 1.15f,
        calorieTarget * 1.25f,
        calorieConsumed.toFloat()
    ).coerceAtLeast(100f)

    // Check if consumed calories land in target range
    val isInRange = calorieConsumed in calorieMinRange..calorieMaxRange
    val progressColor = if (isInRange) inRangeColor else primaryColor

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Semi-circle Arc Gauge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(
                    modifier = Modifier.size(200.dp, 160.dp)
                ) {
                    val strokeWidth = 14.dp.toPx()
                    // Create a perfectly square arc diameter so drawArc renders a true circle (no distortion)
                    val arcDiameter = size.width - strokeWidth - 8.dp.toPx()
                    val topLeft = Offset(
                        x = (size.width - arcDiameter) / 2f,
                        y = (size.height - arcDiameter) / 2f + 4.dp.toPx()
                    )
                    val arcSize = Size(arcDiameter, arcDiameter)

                    val startAngle = 160f
                    val totalSweep = 220f

                    // 1. Draw background track arc
                    drawArc(
                        color = trackColor,
                        startAngle = startAngle,
                        sweepAngle = totalSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // 2. Draw target range band [calorieMinRange .. calorieMaxRange]
                    if (calorieMinRange > 0 && calorieMaxRange > calorieMinRange) {
                        val minFraction = (calorieMinRange.toFloat() / maxScale).coerceIn(0f, 1f)
                        val maxFraction = (calorieMaxRange.toFloat() / maxScale).coerceIn(0f, 1f)
                        val rangeStartAngle = startAngle + totalSweep * minFraction
                        val rangeSweepAngle = totalSweep * (maxFraction - minFraction)

                        drawArc(
                            color = targetBandColor,
                            startAngle = rangeStartAngle,
                            sweepAngle = rangeSweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth + 4.dp.toPx(), cap = StrokeCap.Butt)
                        )
                    }

                    // 3. Draw calorie target tick mark
                    if (calorieTarget > 0) {
                        val targetFraction = (calorieTarget.toFloat() / maxScale).coerceIn(0f, 1f)
                        val targetAngle = startAngle + totalSweep * targetFraction
                        val angleRad = Math.toRadians(targetAngle.toDouble())

                        val radius = arcDiameter / 2f
                        val centerX = topLeft.x + radius
                        val centerY = topLeft.y + radius

                        val innerR = radius - strokeWidth / 2 - 4.dp.toPx()
                        val outerR = radius + strokeWidth / 2 + 4.dp.toPx()

                        val startX = (centerX + innerR * cos(angleRad)).toFloat()
                        val startY = (centerY + innerR * sin(angleRad)).toFloat()
                        val endX = (centerX + outerR * cos(angleRad)).toFloat()
                        val endY = (centerY + outerR * sin(angleRad)).toFloat()

                        drawLine(
                            color = primaryColor,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // 4. Draw consumed progress arc
                    val progressFraction = (calorieConsumed.toFloat() / maxScale).coerceIn(0f, 1f)
                    val progressSweep = totalSweep * progressFraction
                    if (progressSweep > 0) {
                        drawArc(
                            color = progressColor,
                            startAngle = startAngle,
                            sweepAngle = progressSweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }

                // Text inside the gauge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 18.dp)
                ) {
                    Text(
                        text = "$calorieConsumed / $calorieTarget",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "kcal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (isInRange) inRangeColor else Color(0xFF81C784),
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ziel: $calorieMinRange - $calorieMaxRange kcal",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Macro Rows
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MacroProgressRow(
                    label = "Proteins >",
                    consumed = proteinConsumed,
                    target = proteinTarget,
                    unit = "g",
                    color = ProteinColor
                )
                MacroProgressRow(
                    label = "Carbs >",
                    consumed = carbsConsumed,
                    target = carbsTarget,
                    unit = "g",
                    color = CarbsColor
                )
                MacroProgressRow(
                    label = "Fats >",
                    consumed = fatConsumed,
                    target = fatTarget,
                    unit = "g",
                    color = FatColor
                )
            }
        }
    }
}

@Composable
private fun MacroProgressRow(
    label: String,
    consumed: Int,
    target: Int,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (target > 0) (consumed.toFloat() / target.toFloat()).coerceIn(0f, 1f) else 0f

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$consumed / $target $unit",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f),
            strokeCap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalorieGaugeCardPreview() {
    EssmichkiTheme {
        CalorieGaugeCard(
            calorieConsumed = 632,
            calorieTarget = 1581,
            calorieMinRange = 1423,
            calorieMaxRange = 1739,
            proteinConsumed = 27,
            proteinTarget = 113,
            carbsConsumed = 84,
            carbsTarget = 163,
            fatConsumed = 22,
            fatTarget = 53,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalorieGaugeCardInRangePreview() {
    EssmichkiTheme {
        CalorieGaugeCard(
            calorieConsumed = 1520,
            calorieTarget = 1581,
            calorieMinRange = 1423,
            calorieMaxRange = 1739,
            proteinConsumed = 98,
            proteinTarget = 113,
            carbsConsumed = 140,
            carbsTarget = 163,
            fatConsumed = 45,
            fatTarget = 53,
            modifier = Modifier.padding(16.dp)
        )
    }
}
