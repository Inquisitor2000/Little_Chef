package com.familymealplanner.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.familymealplanner.R
import com.familymealplanner.domain.model.EnrichedIngredient
import com.familymealplanner.ui.util.rememberHapticFeedback
import kotlinx.coroutines.launch

/**
 * Bottom sheet for voice input with live transcription.
 * 
 * Features:
 * - Permission handling with rationale
 * - Pulsing microphone animation while recording
 * - Live transcription display with auto-scroll
 * - Recording timer (MM:SS format)
 * - Stop and Cancel buttons
 * - Error display with retry option
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceInputBottomSheet(
    viewModel: VoiceInputViewModel,
    visible: Boolean,
    onDismiss: () -> Unit,
    onNavigateToReview: (List<com.familymealplanner.domain.model.VoiceIngredientItem>) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val haptic = rememberHapticFeedback()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Permission state
    var permissionGranted by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    var permissionPermanentlyDenied by remember { mutableStateOf(false) }
    
    // Check permission on composition
    LaunchedEffect(visible) {
        if (visible) {
            permissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            // Start recording immediately after permission granted
            viewModel.startRecording()
            haptic.performDestructive()
        } else {
            // Check if permanently denied
            permissionPermanentlyDenied = !androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(
                context as androidx.activity.ComponentActivity,
                Manifest.permission.RECORD_AUDIO
            )
        }
    }
    
    // Show error in snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            coroutineScope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = error,
                    actionLabel = context.getString(R.string.voice_error_retry),
                    duration = SnackbarDuration.Long
                )
                if (result == SnackbarResult.ActionPerformed) {
                    // Retry recording
                    if (permissionGranted) {
                        viewModel.startRecording()
                        haptic.performDestructive()
                    } else {
                        showPermissionRationale = true
                    }
                }
            }
        }
    }
    
    // Auto-start recording when permission is already granted
    LaunchedEffect(visible, permissionGranted) {
        if (visible && permissionGranted && !uiState.isRecording) {
            viewModel.startRecording()
            haptic.performDestructive()
        }
    }
    
    // Reset state when drawer closes
    LaunchedEffect(visible) {
        if (!visible) {
            viewModel.cancelRecording()
            showPermissionRationale = false
            permissionPermanentlyDenied = false
        }
    }
    
    // Bottom sheet
    if (visible) {
        // Show tutorial dialog if needed
        if (uiState.showTutorial) {
            VoiceInputTutorialDialog(
                onDismiss = {
                    viewModel.dismissTutorial()
                },
                onDontShowAgain = {
                    viewModel.dismissTutorialPermanently()
                }
            )
        }
        
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.cancelRecording()
                onDismiss()
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .width(32.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    )
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Permission rationale or recording UI
                    if (!permissionGranted) {
                        if (permissionPermanentlyDenied) {
                            PermissionPermanentlyDeniedContent(
                                onOpenSettings = {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                    context.startActivity(intent)
                                },
                                onDismiss = onDismiss
                            )
                        } else {
                            PermissionRationaleContent(
                                onRequestPermission = {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                },
                                onDismiss = onDismiss
                            )
                        }
                    } else {
                        // Recording UI
                        RecordingContent(
                            uiState = uiState,
                            onDone = {
                                viewModel.stopRecording()
                                haptic.performDestructive()
                                
                                // Process transcription and navigate to review
                                val voiceIngredients = viewModel.processTranscription()
                                if (voiceIngredients.isNotEmpty()) {
                                    onNavigateToReview(voiceIngredients)
                                    onDismiss()
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.voice_no_ingredients),
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
                
                // Snackbar host at bottom
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}

/**
 * Permission rationale content.
 */
@Composable
private fun PermissionRationaleContent(
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = stringResource(R.string.voice_permission_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = stringResource(R.string.voice_permission_message),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(stringResource(R.string.voice_button_cancel))
            }
            
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.voice_button_allow))
            }
        }
    }
}

/**
 * Permission permanently denied content.
 */
@Composable
private fun PermissionPermanentlyDeniedContent(
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Text(
            text = stringResource(R.string.voice_permission_denied_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = stringResource(R.string.voice_permission_denied_message),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(stringResource(R.string.voice_button_cancel))
            }
            
            Button(
                onClick = onOpenSettings,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.voice_button_open_settings))
            }
        }
    }
}

/**
 * Recording content with microphone animation, transcription, and controls.
 */
@Composable
private fun RecordingContent(
    uiState: VoiceInputViewModel.VoiceInputState,
    onDone: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    // Auto-scroll to bottom when transcription updates
    LaunchedEffect(uiState.transcription) {
        if (uiState.transcription.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 380.dp, max = 450.dp)
    ) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, bottom = if (uiState.transcription.isNotEmpty()) 70.dp else 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pulsing microphone icon
            PulsingMicrophoneIcon(isRecording = uiState.isRecording)
            
            // Timer
            Text(
                text = formatTime(uiState.elapsedTime),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Transcription display
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    if (uiState.transcription.isEmpty()) {
                        Text(
                            text = if (uiState.isRecording) stringResource(R.string.voice_listening) else stringResource(R.string.voice_start_speaking),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        Text(
                            text = uiState.transcription,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                        )
                    }
                }
            }
            
            // Hint text
            if (uiState.transcription.isEmpty()) {
                Text(
                    text = stringResource(R.string.voice_pull_down_cancel),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 60.dp)
                )
            }
        }
        
        // Done button at bottom (only show when there's transcription)
        if (uiState.transcription.isNotEmpty()) {
            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                enabled = !uiState.isProcessing
            ) {
                if (uiState.isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.voice_processing))
                } else {
                    Text(stringResource(R.string.voice_button_done))
                }
            }
        }
    }
}

/**
 * Pulsing microphone icon animation.
 */
@Composable
private fun PulsingMicrophoneIcon(isRecording: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Box(
        modifier = Modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pulsing background circle
        if (isRecording) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            )
        }
        
        // Microphone icon
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = if (isRecording) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = if (isRecording) stringResource(R.string.voice_recording) else stringResource(R.string.voice_not_recording),
                    modifier = Modifier.size(28.dp),
                    tint = if (isRecording) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Format elapsed time in MM:SS format.
 */
private fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

/**
 * Tutorial dialog for first-time voice input users.
 * 
 * Shows usage instructions and examples with a "Don't show again" option.
 * 
 * @param onDismiss Callback when dialog is dismissed
 * @param onDontShowAgain Callback when "Don't show again" is checked and dismissed
 */
@Composable
fun VoiceInputTutorialDialog(
    onDismiss: () -> Unit,
    onDontShowAgain: () -> Unit
) {
    var dontShowAgain by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = {
            if (dontShowAgain) {
                onDontShowAgain()
            } else {
                onDismiss()
            }
        },
        title = {
            Text(
                text = stringResource(R.string.voice_tutorial_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.voice_tutorial_intro),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                // Instructions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TutorialStep(
                        number = "1",
                        text = stringResource(R.string.voice_tutorial_step1)
                    )
                    
                    TutorialStep(
                        number = "2",
                        text = stringResource(R.string.voice_tutorial_step2)
                    )
                    
                    TutorialStep(
                        number = "3",
                        text = stringResource(R.string.voice_tutorial_step3)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Examples section
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.voice_tutorial_examples_title),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Text(
                            text = stringResource(R.string.voice_tutorial_example1),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = stringResource(R.string.voice_tutorial_example2),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = stringResource(R.string.voice_tutorial_example3),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Don't show again checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            dontShowAgain = !dontShowAgain
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(
                        checked = dontShowAgain,
                        onCheckedChange = { dontShowAgain = it }
                    )
                    
                    Text(
                        text = stringResource(R.string.voice_tutorial_dont_show),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (dontShowAgain) {
                        onDontShowAgain()
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text(stringResource(R.string.voice_tutorial_got_it))
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Tutorial step item with number and text.
 */
@Composable
private fun TutorialStep(
    number: String,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(24.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
