package com.familymealplanner.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * A quantity input field with +/- stepper buttons.
 * 
 * Layout: [ - ] [ input ] [ + ]     unit (or dropdown)
 * 
 * Features:
 * - Click to increment/decrement by 1
 * - Supports rapid clicking
 * - Integer-only input (no decimals) for easier user input
 * - Optional unit dropdown for editable units
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuantityStepper(
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    modifier: Modifier = Modifier,
    minValue: Int = 0,
    availableUnits: List<String>? = null,
    onUnitChange: ((String) -> Unit)? = null
) {
    val haptic = rememberHapticFeedback()
    var unitDropdownExpanded by remember { mutableStateOf(false) }
    val isUnitEditable = availableUnits != null && onUnitChange != null

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Minus button
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable {
                    val current = value.toIntOrNull() ?: 0
                    if (current > minValue) {
                        onValueChange((current - 1).coerceAtLeast(minValue).toString())
                        haptic.performLight()
                    }
                }
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "−",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Quantity input field (narrower) - integer only
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // Only allow digits (no decimals)
                val filtered = newValue.filter { it.isDigit() }
                onValueChange(filtered)
            },
            modifier = Modifier.width(80.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Plus button
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable {
                    val current = value.toIntOrNull() ?: 0
                    onValueChange((current + 1).toString())
                    haptic.performLight()
                }
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        // Spacer to push unit to the right
        Spacer(modifier = Modifier.weight(1f))
        
        // Unit label or dropdown (aligned to right)
        if (isUnitEditable) {
            // Clickable unit with dropdown
            Box {
                Surface(
                    onClick = { unitDropdownExpanded = true },
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = unit,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Select unit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                DropdownMenu(
                    expanded = unitDropdownExpanded,
                    onDismissRequest = { unitDropdownExpanded = false },
                    modifier = Modifier.widthIn(min = 100.dp)
                ) {
                    availableUnits!!.forEach { unitOption ->
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = unitOption,
                                    style = MaterialTheme.typography.bodyMedium
                                ) 
                            },
                            onClick = {
                                onUnitChange!!(unitOption)
                                unitDropdownExpanded = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        } else {
            // Static unit label
            Text(
                text = unit,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
