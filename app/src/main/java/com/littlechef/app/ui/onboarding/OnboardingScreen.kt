package com.littlechef.app.ui.onboarding

import android.app.Activity
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Locale

private const val TAG = "OnboardingScreen"

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val activity = remember { context as? Activity }
    
    // Handle completion - recreate activity if language changed
    LaunchedEffect(state.currentStep) {
        if (state.currentStep == OnboardingStep.Complete) {
            if (viewModel.shouldRecreateActivity()) {
                Log.d(TAG, "Language changed, recreating activity to apply locale")
                // Longer delay to show loading screen and reduce perceived flash
                kotlinx.coroutines.delay(800)
                activity?.recreate()
            } else {
                Log.d(TAG, "Language unchanged (English), no recreation needed")
                // For English, MainActivity's Crossfade will handle transition smoothly
            }
        }
    }

    // Wrap content with locale-aware context
    LocaleAwareContent(
        locale = Locale(state.selectedLanguage),
        localeVersion = state.localeVersion
    ) {
        Crossfade(
            targetState = state.currentStep,
            animationSpec = tween(durationMillis = 300),
            label = "onboarding_crossfade"
        ) { step ->
            when (step) {
                OnboardingStep.LanguageSelection -> {
                    LanguageSelectionScreen(
                        selectedLanguage = state.selectedLanguage,
                        onLanguageSelect = viewModel::setLanguage,
                        onContinue = viewModel::nextStep
                    )
                }
                OnboardingStep.Welcome -> {
                    WelcomeScreen(
                        onContinue = viewModel::nextStep
                    )
                }
                OnboardingStep.ServingSize -> {
                    ServingSizeScreen(
                        selectedServingSize = state.selectedServingSize,
                        onServingSizeChange = viewModel::setServingSize,
                        onComplete = viewModel::completeOnboarding,
                        isLoading = state.isLoading
                    )
                }
                OnboardingStep.Complete -> {
                    // Show loading screen during activity recreation
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LocaleAwareContent(
    locale: Locale,
    localeVersion: Int,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    
    val localeContext = remember(locale, localeVersion) {
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }
    
    CompositionLocalProvider(
        LocalContext provides localeContext
    ) {
        content()
    }
}
