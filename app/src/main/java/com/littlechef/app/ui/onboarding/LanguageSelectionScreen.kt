package com.littlechef.app.ui.onboarding

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlechef.app.R
import com.littlechef.app.ui.util.rememberHapticFeedback

private const val TAG = "LanguageSelectionScreen"

@Composable
fun LanguageSelectionScreen(
    selectedLanguage: String,
    onLanguageSelect: (String) -> Unit,
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    
    val currentLocale = context.resources.configuration.locales[0]
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
                .padding(horizontal = 32.dp)
        ) {
            // Fixed top spacing to pin icon position
            Spacer(modifier = Modifier.height(80.dp))
            
            // App icon - pinned at fixed position
            OnboardingAppIcon()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Title
            Text(
                text = stringResource(R.string.language_selection_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Language options
            Column(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LanguageOption(
                    languageName = "English",
                    isSelected = selectedLanguage == "en",
                    onClick = { 
                        if (selectedLanguage != "en") {
                            haptic.performLight()
                            Log.d(TAG, "EN selected")
                            onLanguageSelect("en")
                        }
                    }
                )
                
                LanguageOption(
                    languageName = "Русский",
                    isSelected = selectedLanguage == "ru",
                    onClick = { 
                        if (selectedLanguage != "ru") {
                            haptic.performLight()
                            Log.d(TAG, "RU selected")
                            onLanguageSelect("ru")
                        }
                    }
                )
                
                LanguageOption(
                    languageName = "Română",
                    isSelected = selectedLanguage == "ro",
                    onClick = { 
                        if (selectedLanguage != "ro") {
                            haptic.performLight()
                            Log.d(TAG, "RO selected")
                            onLanguageSelect("ro")
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Continue button
            Button(
                onClick = {
                    haptic.performLight()
                    onContinue()
                },
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

@Composable
private fun LanguageOption(
    languageName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = languageName,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
