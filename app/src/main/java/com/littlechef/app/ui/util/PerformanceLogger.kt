package com.littlechef.app.ui.util

import android.util.Log

/**
 * Lightweight performance logging utility for debugging UI lag.
 * 
 * Usage:
 * ```
 * val perf = PerformanceLogger("ScreenName")
 * perf.start("operation")
 * // ... do work
 * perf.end("operation") // Logs if > 16ms (1 frame)
 * ```
 */
class PerformanceLogger(private val tag: String) {
    private val timings = mutableMapOf<String, Long>()
    
    companion object {
        const val FRAME_TIME_MS = 16L // One frame at 60fps
        const val SLOW_THRESHOLD_MS = 50L // Log if operation takes > 50ms
        
        /**
         * Quick timing for a block of code
         */
        inline fun <T> measure(tag: String, operation: String, block: () -> T): T {
            val start = System.currentTimeMillis()
            return block().also {
                val duration = System.currentTimeMillis() - start
                if (duration > FRAME_TIME_MS) {
                    Log.d(tag, "⏱️ $operation: ${duration}ms")
                }
            }
        }
        
        /**
         * Log slow operations only (> 50ms)
         */
        inline fun <T> measureSlow(tag: String, operation: String, block: () -> T): T {
            val start = System.currentTimeMillis()
            return block().also {
                // Logging disabled
            }
        }
    }
    
    /**
     * Start timing an operation
     */
    fun start(operation: String) {
        timings[operation] = System.currentTimeMillis()
    }
    
    /**
     * End timing and log if slow
     */
    fun end(operation: String, threshold: Long = PerformanceLogger.FRAME_TIME_MS) {
        val startTime = timings.remove(operation) ?: return
        val duration = System.currentTimeMillis() - startTime
        
        if (duration > threshold) {
            val emoji = when {
                duration > 100 -> "🔴"
                duration > 50 -> "🟡"
                else -> "⏱️"
            }
            Log.d(tag, "$emoji $operation: ${duration}ms")
        }
    }
    
    /**
     * Log a checkpoint with elapsed time since start
     */
    fun checkpoint(operation: String, label: String) {
        val startTime = timings[operation] ?: return
        val elapsed = System.currentTimeMillis() - startTime
        Log.d(tag, "  ↳ $label: ${elapsed}ms elapsed")
    }
}

/**
 * Composable performance tracking
 */
@androidx.compose.runtime.Composable
fun rememberPerformanceLogger(tag: String): PerformanceLogger {
    return androidx.compose.runtime.remember { PerformanceLogger(tag) }
}
