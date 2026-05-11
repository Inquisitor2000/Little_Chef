package com.littlechef.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlechef.app.ui.util.BottomDrawer

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SettingsDrawer(
    visible: Boolean,
    onDismiss: () -> Unit,
    apiKey: String?,
    onSaveApiKey: (String?) -> Unit,
    textScale: Float,
    onTextScaleChange: (Float) -> Unit,
    selectedFont: String,
    onFontChange: (String) -> Unit,
    selectedAccentColorLight: Long,
    selectedAccentColorDark: Long,
    onAccentColorLightChange: (Long) -> Unit,
    onAccentColorDarkChange: (Long) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val selectedAccentColor = if (isDarkTheme) selectedAccentColorDark else selectedAccentColorLight
    val onAccentColorChange: (Long) -> Unit = if (isDarkTheme) onAccentColorDarkChange else onAccentColorLightChange
    
    val keyboardController = LocalSoftwareKeyboardController.current
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current
    var apiKeyInput by remember(apiKey) { mutableStateOf(apiKey ?: "") }
    var showSaveConfirmation by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    var currentTextScale by remember(textScale) { mutableStateOf(textScale) }

    // Reset state when drawer closes
    LaunchedEffect(visible) {
        if (!visible) {
            showSaveConfirmation = false
        }
    }
    
    // Round to nearest 5%
    fun roundToNearest5Percent(value: Float): Float {
        val percentage = (value * 100).toInt()
        val rounded = ((percentage + 2.5f) / 5).toInt() * 5 // Round to nearest 5
        return rounded / 100f
    }

    BottomDrawer(
        visible = visible,
        onDismiss = onDismiss,
        title = "Settings"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // OpenAI API Key Section
            Text(
                text = "Recipe Gathering",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Add your API key to enable recipe scraping.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("OpenAI API Key") },
                        placeholder = { Text("sk-...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                                if (focusState.isFocused) {
                                    keyboardController?.hide()
                                }
                            },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            when {
                                // Show paste button when focused and empty
                                isFocused && apiKeyInput.isBlank() -> {
                                    TextButton(
                                        onClick = {
                                            clipboardManager.getText()?.text?.let { pastedText ->
                                                apiKeyInput = pastedText
                                                onSaveApiKey(pastedText.ifBlank { null })
                                                showSaveConfirmation = true
                                            }
                                            focusManager.clearFocus()
                                        }
                                    ) {
                                        Text(
                                            text = "Paste",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                // Show check button when value changed
                                apiKeyInput != (apiKey ?: "") -> {
                                    IconButton(
                                        onClick = {
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                            onSaveApiKey(apiKeyInput.ifBlank { null })
                                            showSaveConfirmation = true
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Save API key",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    )
                    
                    if (showSaveConfirmation) {
                        LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(2000)
                            showSaveConfirmation = false
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✓ API key saved",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Appearance & Accessibility Section (Combined)
            Text(
                text = "Appearance & Accessibility",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Font Selection (Cuisine-style cards with weight switches)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Group fonts by family
                        val robotoFonts = com.littlechef.app.ui.theme.AppFont.values()
                            .filter { it.displayName.startsWith("Roboto") }
                        val rubikFonts = com.littlechef.app.ui.theme.AppFont.values()
                            .filter { it.displayName.startsWith("Rubik") }
                        
                        // 2-column grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Roboto Card
                            FontFamilyCard(
                                familyName = "Roboto",
                                description = "Default system font",
                                emoji = "Aa",
                                fonts = robotoFonts,
                                selectedFont = selectedFont,
                                onFontSelect = onFontChange,
                                modifier = Modifier.weight(1f)
                            )
                            
                            // Rubik Card
                            FontFamilyCard(
                                familyName = "Rubik",
                                description = "Rounded & friendly",
                                emoji = "Aa",
                                fonts = rubikFonts,
                                selectedFont = selectedFont,
                                onFontSelect = onFontChange,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    
                    // Accent Color Picker
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Accent Color",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Preset color bubbles - different for light and dark mode
                        val presetColors = if (isDarkTheme) {
                            listOf(
                                0xFF5398be, // Blue Bell (default)
                                0xFFe65f5c, // Lobster Pink
                                0xFFAF4287  // Plum Purple
                            )
                        } else {
                            listOf(
                                0xFFD68C45, // Toasted Almond (default)
                                0xFFD3594B, // Coral Red
                                0xFFA33DC5  // Purple
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            presetColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            color = androidx.compose.ui.graphics.Color(color),
                                            shape = CircleShape
                                        )
                                        .clickable { onAccentColorChange(color) }
                                        .then(
                                            if (selectedAccentColor == color) {
                                                Modifier.border(
                                                    width = 3.dp,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    shape = CircleShape
                                                )
                                            } else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selectedAccentColor == color) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = androidx.compose.ui.graphics.Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    
                    // Text Size Slider
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Text Size",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(roundToNearest5Percent(currentTextScale) * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Slider(
                            value = currentTextScale,
                            onValueChange = { 
                                val rounded = roundToNearest5Percent(it)
                                currentTextScale = rounded
                                onTextScaleChange(rounded)
                            },
                            valueRange = 0.85f..1.15f,
                            steps = 5,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun FontFamilyCard(
    familyName: String,
    @Suppress("UNUSED_PARAMETER") description: String,
    emoji: String,
    fonts: List<com.littlechef.app.ui.theme.AppFont>,
    selectedFont: String,
    onFontSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Get the font family for preview (always use Regular weight for preview)
    val previewFontFamily = when (familyName) {
        "Roboto" -> com.littlechef.app.ui.theme.AppFonts.getFontFamily("Roboto Regular")
        "Rubik" -> com.littlechef.app.ui.theme.AppFonts.getFontFamily("Rubik Regular")
        else -> androidx.compose.ui.text.font.FontFamily.Default
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.1f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section - Font preview and name
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(Alignment.CenterVertically)
            ) {
                Text(
                    text = emoji,
                    fontSize = 36.sp,
                    fontFamily = previewFontFamily,
                    modifier = Modifier.height(44.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = familyName,
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = previewFontFamily),
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.height(24.dp)
                )
            }
            
            // Bottom section - Weight switches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                fonts.forEach { font ->
                    val isSelected = selectedFont == font.displayName
                    val weightName = font.displayName.substringAfter(" ")
                    
                    Button(
                        onClick = { onFontSelect(font.displayName) },
                        modifier = Modifier.weight(1f),
                        colors = if (isSelected) {
                            ButtonDefaults.buttonColors()
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        },
                        border = if (!isSelected) {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        } else null,
                        contentPadding = PaddingValues(vertical = 6.dp, horizontal = 2.dp)
                    ) {
                        Text(
                            text = weightName.first().toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FontCard(
    font: com.littlechef.app.ui.theme.AppFont,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Extract weight name (Light, Regular, Medium)
            val weightName = font.displayName.substringAfter(" ")
            
            Text(
                text = weightName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
