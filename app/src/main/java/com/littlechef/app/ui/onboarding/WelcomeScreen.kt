package com.littlechef.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlechef.app.R
import com.littlechef.app.ui.util.rememberHapticFeedback

@Composable
fun WelcomeScreen(
    onContinue: () -> Unit
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
                .padding(horizontal = 32.dp)
        ) {
            // Fixed top spacing to pin icon position
            Spacer(modifier = Modifier.height(80.dp))
            
            // App icon - pinned at fixed position
            OnboardingAppIcon()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App name
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description
            Text(
                text = stringResource(R.string.onboarding_welcome_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Next button
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
