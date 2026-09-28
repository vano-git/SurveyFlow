package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Answer
import com.example.model.SurveyQuestion

@Composable
fun SingleChoiceSelector(
    options: List<String>,
    currentAnswer: Answer.SingleChoice?,
    onAnswerSelected: (Answer.SingleChoice) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = currentAnswer?.selectedOption == option
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onAnswerSelected(Answer.SingleChoice(option)) }
                    .testTag("single_choice_$index"),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onAnswerSelected(Answer.SingleChoice(option)) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MultiChoiceSelector(
    options: List<String>,
    currentAnswer: Answer.MultiChoice?,
    onAnswerSelected: (Answer.MultiChoice) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedList = currentAnswer?.selectedOptions ?: emptyList()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = selectedList.contains(option)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        val updated = if (isSelected) {
                            selectedList - option
                        } else {
                            selectedList + option
                        }
                        onAnswerSelected(Answer.MultiChoice(updated))
                    }
                    .testTag("multi_choice_$index"),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { checked ->
                            val updated = if (checked) selectedList + option else selectedList - option
                            onAnswerSelected(Answer.MultiChoice(updated))
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun RatingSelector(
    minRating: Int,
    maxRating: Int,
    currentAnswer: Answer.Rating?,
    onAnswerSelected: (Answer.Rating) -> Unit,
    modifier: Modifier = Modifier
) {
    val current = currentAnswer?.rating ?: 0

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            (minRating..maxRating).forEach { star ->
                val filled = star <= current
                IconButton(
                    onClick = { onAnswerSelected(Answer.Rating(star, maxRating)) },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("rating_star_$star")
                ) {
                    Icon(
                        imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Rate $star of $maxRating",
                        tint = if (filled) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (current > 0) "$current of $maxRating Stars" else "Tap a star to rate",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SliderSelector(
    min: Float,
    max: Float,
    step: Float,
    unit: String,
    currentAnswer: Answer.Slider?,
    onAnswerSelected: (Answer.Slider) -> Unit,
    modifier: Modifier = Modifier
) {
    val value = currentAnswer?.value ?: ((min + max) / 2f)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${min.toInt()} $unit",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "${value.toInt()} $unit",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            Text(
                text = "${max.toInt()} $unit",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        val stepsCount = if (step > 0) {
            ((max - min) / step).toInt() - 1
        } else 0

        Slider(
            value = value,
            onValueChange = { newVal ->
                onAnswerSelected(Answer.Slider(newVal, unit))
            },
            valueRange = min..max,
            steps = stepsCount.coerceAtLeast(0),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("slider_input")
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BooleanSelector(
    currentAnswer: Answer.BooleanAnswer?,
    onAnswerSelected: (Answer.BooleanAnswer) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val isYes = currentAnswer?.value == true
        val isNo = currentAnswer?.value == false

        Surface(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onAnswerSelected(Answer.BooleanAnswer(true)) }
                .testTag("boolean_yes"),
            shape = RoundedCornerShape(12.dp),
            color = if (isYes) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = if (isYes) 2.dp else 1.dp,
                color = if (isYes) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isYes) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = "Yes",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (isYes) FontWeight.Bold else FontWeight.Normal,
                        color = if (isYes) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onAnswerSelected(Answer.BooleanAnswer(false)) }
                .testTag("boolean_no"),
            shape = RoundedCornerShape(12.dp),
            color = if (isNo) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = if (isNo) 2.dp else 1.dp,
                color = if (isNo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isNo) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = "No",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (isNo) FontWeight.Bold else FontWeight.Normal,
                        color = if (isNo) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

@Composable
fun TextInputSelector(
    placeholder: String?,
    currentAnswer: Answer.Text?,
    onAnswerSelected: (Answer.Text) -> Unit,
    modifier: Modifier = Modifier
) {
    val text = currentAnswer?.text ?: ""

    OutlinedTextField(
        value = text,
        onValueChange = { onAnswerSelected(Answer.Text(it)) },
        placeholder = {
            Text(text = placeholder ?: "Type your response here...")
        },
        modifier = modifier
            .fillMaxWidth()
            .testTag("text_input_field"),
        minLines = 3,
        maxLines = 6,
        shape = RoundedCornerShape(12.dp)
    )
}
