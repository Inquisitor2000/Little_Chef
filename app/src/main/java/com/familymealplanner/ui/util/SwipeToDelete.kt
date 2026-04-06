package com.familymealplanner.ui.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.familymealplanner.ui.theme.error_color

/**
 * Wrapper composable that adds swipe-to-delete functionality to list items
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteContainer(
    @Suppress("UNUSED_PARAMETER") item: Any,
    onDelete: () -> Unit,
    enabled: Boolean = true,
    confirmationTitle: String = "Delete Item",
    confirmationMessage: String = "Are you sure you want to delete this item?",
    deleteButtonText: String = "Delete",
    cancelButtonText: String = "Cancel",
    content: @Composable () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleted by remember { mutableStateOf(false) }
    val dismissState = rememberDismissState(
        confirmValueChange = { dismissValue ->
            if (enabled && dismissValue == DismissValue.DismissedToStart) {
                showDeleteDialog = true
                false // Don't dismiss yet, wait for confirmation
            } else {
                false
            }
        }
    )

    AnimatedVisibility(
        visible = !isDeleted,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = 300),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        if (enabled) {
            SwipeToDismiss(
                state = dismissState,
                directions = setOf(DismissDirection.EndToStart),
                background = {
                    val color = when (dismissState.dismissDirection) {
                        DismissDirection.EndToStart -> MaterialTheme.colorScheme.errorContainer
                        else -> Color.Transparent
                    }
                    
                    val scale by androidx.compose.animation.core.animateFloatAsState(
                        targetValue = if (dismissState.targetValue == DismissValue.Default) 0.8f else 1.2f,
                        animationSpec = androidx.compose.animation.core.spring(
                            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                        ),
                        label = "scale"
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.background,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = error_color,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .scale(scale)
                                    )
                                }
                            }
                        }
                    }
                },
                dismissContent = {
                    content()
                }
            )
        } else {
            // If swipe is disabled, just show the content
            content()
        }
    }

    if (showDeleteDialog) {
        Dialog(
            onDismissRequest = {
                showDeleteDialog = false
            }
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    // Title
                    Text(
                        text = confirmationTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Message
                    Text(
                        text = confirmationMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Delete button
                        Button(
                            onClick = {
                                showDeleteDialog = false
                                isDeleted = true
                                onDelete()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = deleteButtonText,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        
                        // Cancel button
                        Button(
                            onClick = {
                                showDeleteDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text(
                                text = cancelButtonText,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
