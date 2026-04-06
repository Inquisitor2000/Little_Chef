package com.familymealplanner.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.familymealplanner.R

@Composable
fun ServingSizeScreen(
    selectedServingSize: Int,
    onServingSizeChange: (Int) -> Unit,
    onComplete: () -> Unit,
    isLoading: Boolean
) {
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
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Single filled button with 4 sections
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Me section
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(enabled = !isLoading) {
                                onServingSizeChange(1)
                                onComplete()
                            }
                            .then(
                                if (selectedServingSize == 1) {
                                    Modifier.background(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                        RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
                                    )
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_serving_size_me),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = if (selectedServingSize == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    
                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(32.dp)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
                    )
                    
                    // 2 section
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(enabled = !isLoading) {
                                onServingSizeChange(2)
                                onComplete()
                            }
                            .then(
                                if (selectedServingSize == 2) {
                                    Modifier.background(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                    )
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "2",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = if (selectedServingSize == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    
                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(32.dp)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
                    )
                    
                    // 4 section
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(enabled = !isLoading) {
                                onServingSizeChange(4)
                                onComplete()
                            }
                            .then(
                                if (selectedServingSize == 4) {
                                    Modifier.background(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                    )
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "4",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = if (selectedServingSize == 4) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    
                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(32.dp)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
                    )
                    
                    // 6 section
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(enabled = !isLoading) {
                                onServingSizeChange(6)
                                onComplete()
                            }
                            .then(
                                if (selectedServingSize == 6) {
                                    Modifier.background(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                        RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                                    )
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "6",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = if (selectedServingSize == 6) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Helper text
            Text(
                text = stringResource(R.string.onboarding_serving_size_helper),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
