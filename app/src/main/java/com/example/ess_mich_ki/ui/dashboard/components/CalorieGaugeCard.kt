package com.example.ess_mich_ki.ui.dashboard.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ess_mich_ki.ui.theme.CarbsColor
import com.example.ess_mich_ki.ui.theme.EssmichkiTheme
import com.example.ess_mich_ki.ui.theme.FatColor
import com.example.ess_mich_ki.ui.theme.NothingRed
import com.example.ess_mich_ki.ui.theme.NothingSage
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
    onEditGoalsClick: (() -> Unit)? = null
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val activeGaugeColor = NothingRed
    val inRangeColor = NothingSage
    val targetBandColor = NothingSage.copy(alpha = 0.35f)

    val maxScale = maxOf(
        calorieMaxRange * 1.15f,
        calorieTarget * 1.25f,
        calorieConsumed.toFloat()
    ).coerceAtLeast(100f)

    val isInRange = calorieConsumed in calorieMinRange..calorieMaxRange
    val progressColor = if (isInRange) inRangeColor else activeGaugeColor

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (onEditGoalsClick != null) {
                IconButton(
                    onClick = onEditGoalsClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Ziele anpassen",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Header badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(NothingRed, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ENERGIE & MAKROS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.2.sp
                    )
                }

                // Semi-circle Arc Gauge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(165.dp)
                ) {
                    Canvas(
                        modifier = Modifier.size(210.dp, 165.dp)
                    ) {
                        val strokeWidth = 12.dp.toPx()
                        val arcDiameter = size.width - strokeWidth - 8.dp.toPx()
                        val topLeft = Offset(
                            x = (size.width - arcDiameter) / 2f,
                            y = (size.height - arcDiameter) / 2f + 4.dp.toPx()
                        )
                        val arcSize = Size(arcDiameter, arcDiameter)

                        val startAngle = 160f
                        val totalSweep = 220f

                        // 1. Background track arc
                        drawArc(
                            color = trackColor,
                            startAngle = startAngle,
                            sweepAngle = totalSweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // 2. Target range band
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

                        // 3. Calorie target tick mark
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
                                color = activeGaugeColor,
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = 2.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }

                        // 4. Consumed progress arc
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
                            text = "$calorieConsumed",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "/ $calorieTarget KCAL",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            color = if (isInRange) NothingSage else NothingRed,
                                            shape = CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ZIEL: $calorieMinRange - $calorieMaxRange",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Macro Rows
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MacroProgressRow(
                        label = "PROTEIN",
                        consumed = proteinConsumed,
                        target = proteinTarget,
                        unit = "G",
                        color = ProteinColor
                    )
                    MacroProgressRow(
                        label = "CARBS",
                        consumed = carbsConsumed,
                        target = carbsTarget,
                        unit = "G",
                        color = CarbsColor
                    )
                    MacroProgressRow(
                        label = "FETT",
                        consumed = fatConsumed,
                        target = fatTarget,
                        unit = "G",
                        color = FatColor
                    )
                }
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
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 0.8.sp
            )
            Text(
                text = "$consumed / $target $unit",
                style = MaterialTheme.typography.labelLarge,
                fontFamily = FontFamily.Monospace,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
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
