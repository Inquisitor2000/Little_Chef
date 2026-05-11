@file:OptIn(ExperimentalMaterial3Api::class)

package com.littlechef.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlechef.app.R
import com.littlechef.app.domain.model.NutritionInfo

/**
 * A compact 4×1 nutrition row shown below the action buttons and above the ingredient/allergen line.
 * Displays Calories, Fats, Carbs, and Proteins per serving in a single centered row.
 * When [nutrition] is [NutritionInfo.EMPTY], nothing is rendered.
 */
@Composable
fun NutritionCard(
    nutrition: NutritionInfo,
    modifier: Modifier = Modifier
) {
    if (nutrition == NutritionInfo.EMPTY) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NutritionItem(
            value = formatNutritionValue(nutrition.calories),
            label = stringResource(R.string.nutrition_calories_short)
        )
        NutritionDivider()
        NutritionItem(
            value = formatNutritionValue(nutrition.fatsG),
            label = stringResource(R.string.nutrition_fats_short)
        )
        NutritionDivider()
        NutritionItem(
            value = formatNutritionValue(nutrition.carbsG),
            label = stringResource(R.string.nutrition_carbs_short)
        )
        NutritionDivider()
        NutritionItem(
            value = formatNutritionValue(nutrition.proteinG),
            label = stringResource(R.string.nutrition_protein_short)
        )
    }
}

@Composable
private fun NutritionItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NutritionDivider() {
    Surface(
        modifier = Modifier
            .width(1.dp)
            .height(28.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
    ) {}
}

fun formatNutritionValue(value: Double): String {
    return if (value >= 10) {
        "${kotlin.math.round(value).toInt()}"
    } else if (value >= 1) {
        String.format("%.1f", value)
    } else if (value > 0) {
        String.format("%.1f", value)
    } else {
        "0"
    }
}
