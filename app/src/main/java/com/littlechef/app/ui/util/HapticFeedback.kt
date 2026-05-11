package com.littlechef.app.ui.util

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Utility class for providing haptic feedback for significant user actions
 */
class HapticFeedbackHelper(private val view: View) {
    
    /**
     * Provide haptic feedback for successful actions (e.g., item created, sync completed)
     */
    fun performSuccess() {
        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }
    
    /**
     * Provide haptic feedback for destructive actions (e.g., delete, abort)
     */
    fun performDestructive() {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
    
    /**
     * Provide haptic feedback for error states
     */
    fun performError() {
        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
    }
    
    /**
     * Provide light haptic feedback for general interactions
     */
    fun performLight() {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }
    
    /**
     * Provide haptic feedback for text handle movements and batch operations
     */
    fun performTextHandleMove() {
        view.performHapticFeedback(HapticFeedbackConstants.TEXT_HANDLE_MOVE)
    }
}

/**
 * Composable function to remember a HapticFeedbackHelper instance
 */
@Composable
fun rememberHapticFeedback(): HapticFeedbackHelper {
    val view = LocalView.current
    return remember(view) { HapticFeedbackHelper(view) }
}
