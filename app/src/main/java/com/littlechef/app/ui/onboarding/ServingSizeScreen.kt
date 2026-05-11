package com.littlechef.app.ui.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlechef.app.R
import com.littlechef.app.ui.util.rememberHapticFeedback

@Composable
fun ServingSizeScreen(
    selectedServingSize: Int,
    onServingSizeChange: (Int) -> Unit,
    onComplete: () -> Unit,
    isLoading: Boolean
) {
    val haptic = rememberHapticFeedback()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Decorative background
        OnboardingBackground()

        // Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 32.dp)
        ) {
            // Fixed top spacing to pin icon position
            Spacer(modifier = Modifier.height(80.dp))

            // App icon - pinned at fixed position
            OnboardingAppIcon()

            Spacer(modifier = Modifier.height(32.dp))

            // Question text
            Text(
                text = stringResource(R.string.onboarding_serving_size_question),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // +/- Stepper for serving size selection
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Minus button
                val canDecrement = selectedServingSize > 1 && !isLoading
                Surface(
                    shape = CircleShape,
                    color = if (canDecrement)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(enabled = canDecrement) {
                            haptic.performLight()
                            onServingSizeChange(selectedServingSize - 1)
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "−",
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (canDecrement)
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Value display
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    modifier = Modifier.width(80.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = selectedServingSize.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (selectedServingSize == 1)
                                stringResource(R.string.recipe_servings_one)
                            else
                                stringResource(R.string.recipe_servings),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Plus button
                val canIncrement = selectedServingSize < 6 && !isLoading
                Surface(
                    shape = CircleShape,
                    color = if (canIncrement)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(enabled = canIncrement) {
                            haptic.performLight()
                            onServingSizeChange(selectedServingSize + 1)
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "+",
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (canIncrement)
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else
                                MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Helper text
            Text(
                text = stringResource(R.string.onboarding_serving_size_helper),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Next button
            Button(
                onClick = {
                    haptic.performLight()
                    onComplete()
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                ),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(48.dp)
            ) {
                Text(
                    text = stringResource(R.string.onboarding_next),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
