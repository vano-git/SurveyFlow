package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Answer
import com.example.model.LikertScaleConfig
import com.example.ui.theme.LikertGreen
import com.example.ui.theme.LikertGreenBg
import com.example.ui.theme.LikertOrange
import com.example.ui.theme.LikertOrangeBg
import com.example.ui.theme.LikertRed
import com.example.ui.theme.LikertRedBg
import com.example.ui.theme.LikertTeal
import com.example.ui.theme.LikertTealBg
import com.example.ui.theme.LikertYellow
import com.example.ui.theme.LikertYellowBg

@Composable
fun LikertSelector(
    config: LikertScaleConfig,
    currentAnswer: Answer.Likert?,
    onAnswerSelected: (Answer.Likert) -> Unit,
    modifier: Modifier = Modifier
) {
    val points = config.points
    val labels = config.labels

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("likert_scale_container"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        (1..points).forEach { score ->
            val label = labels.getOrElse(score - 1) { "Score $score" }
            val isSelected = currentAnswer?.score == score

            val (activeBorder, activeBg, activeText) = getLikertColors(score, points)

            val animatedBg by animateColorAsState(
                targetValue = if (isSelected) activeBg else MaterialTheme.colorScheme.surface,
                label = "likert_bg_$score"
            )
            val animatedBorder by animateColorAsState(
                targetValue = if (isSelected) activeBorder else MaterialTheme.colorScheme.outlineVariant,
                label = "likert_border_$score"
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onAnswerSelected(Answer.Likert(score = score, label = label))
                    }
                    .testTag("likert_option_$score"),
                shape = RoundedCornerShape(12.dp),
                color = animatedBg,
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = animatedBorder
                ),
                shadowElevation = if (isSelected) 2.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Numeric circle badge
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) activeBorder else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = score.toString(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Label description
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) activeText else MaterialTheme.colorScheme.onSurface
                            ),
                            fontSize = 15.sp
                        )
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(activeBorder),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Min/Max helper labels if specified
        if (!config.minLabel.isNullOrBlank() || !config.maxLabel.isNullOrBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = config.minLabel ?: "1: Disagree",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = config.maxLabel ?: "$points: Agree",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getLikertColors(score: Int, total: Int): Triple<Color, Color, Color> {
    return when {
        total <= 3 -> {
            when (score) {
                1 -> Triple(LikertRed, LikertRedBg, Color(0xFF991B1B))
                2 -> Triple(LikertYellow, LikertYellowBg, Color(0xFF854D0E))
                else -> Triple(LikertGreen, LikertGreenBg, Color(0xFF065F46))
            }
        }
        total == 5 -> {
            when (score) {
                1 -> Triple(LikertRed, LikertRedBg, Color(0xFF991B1B))
                2 -> Triple(LikertOrange, LikertOrangeBg, Color(0xFF9A3412))
                3 -> Triple(Color(0xFF64748B), Color(0xFFF1F5F9), Color(0xFF334155))
                4 -> Triple(LikertTeal, LikertTealBg, Color(0xFF115E59))
                else -> Triple(LikertGreen, LikertGreenBg, Color(0xFF065F46))
            }
        }
        total == 7 -> {
            when (score) {
                1 -> Triple(LikertRed, LikertRedBg, Color(0xFF991B1B))
                2 -> Triple(Color(0xFFF87171), LikertRedBg, Color(0xFF991B1B))
                3 -> Triple(LikertOrange, LikertOrangeBg, Color(0xFF9A3412))
                4 -> Triple(Color(0xFF64748B), Color(0xFFF1F5F9), Color(0xFF334155))
                5 -> Triple(Color(0xFF2DD4BF), LikertTealBg, Color(0xFF115E59))
                6 -> Triple(LikertTeal, LikertTealBg, Color(0xFF115E59))
                else -> Triple(LikertGreen, LikertGreenBg, Color(0xFF065F46))
            }
        }
        else -> {
            val ratio = (score - 1f) / (total - 1f).coerceAtLeast(1f)
            when {
                ratio < 0.25f -> Triple(LikertRed, LikertRedBg, Color(0xFF991B1B))
                ratio < 0.5f -> Triple(LikertOrange, LikertOrangeBg, Color(0xFF9A3412))
                ratio < 0.75f -> Triple(LikertTeal, LikertTealBg, Color(0xFF115E59))
                else -> Triple(LikertGreen, LikertGreenBg, Color(0xFF065F46))
            }
        }
    }
}
