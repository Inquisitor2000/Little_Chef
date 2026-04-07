package com.familymealplanner.ui.screens

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.familymealplanner.R
import com.familymealplanner.data.preferences.OnboardingPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val isDarkTheme = isSystemInDarkTheme()
    val keyboardController = LocalSoftwareKeyboardController.current
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()
    val textScale by viewModel.textScale.collectAsState()
    val appFont by viewModel.appFont.collectAsState()
    val accentColorLight by viewModel.accentColorLight.collectAsState()
    val accentColorDark by viewModel.accentColorDark.collectAsState()
    
    val selectedAccentColor = if (isDarkTheme) accentColorDark else accentColorLight
    
    var apiKeyInput by remember(apiKey) { mutableStateOf(apiKey ?: "") }
    var showSaveConfirmation by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    var currentTextScale by remember(textScale) { mutableStateOf(textScale) }

    // Round to nearest 5%
    fun roundToNearest5Percent(value: Float): Float {
        val percentage = (value * 100).toInt()
        val rounded = ((percentage + 2.5f) / 5).toInt() * 5
        return rounded / 100f
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .padding(bottom = 80.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // OpenAI API Key Section
            Text(
                text = stringResource(R.string.settings_recipe_gathering),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = stringResource(R.string.settings_api_key_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = apiKeyInput,
                onValueChange = { apiKeyInput = it },
                label = { Text(stringResource(R.string.settings_openai_api_key)) },
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
                        isFocused && apiKeyInput.isBlank() -> {
                            TextButton(
                                onClick = {
                                    clipboardManager.getText()?.text?.let { pastedText ->
                                        apiKeyInput = pastedText
                                        coroutineScope.launch {
                                            viewModel.saveApiKey(pastedText.ifBlank { null })
                                        }
                                        showSaveConfirmation = true
                                    }
                                    focusManager.clearFocus()
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.settings_paste),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        apiKeyInput != (apiKey ?: "") -> {
                            IconButton(
                                onClick = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    coroutineScope.launch {
                                        viewModel.saveApiKey(apiKeyInput.ifBlank { null })
                                    }
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
                Text(
                    text = stringResource(R.string.settings_api_key_saved),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Grocery List Export Section
            Text(
                text = stringResource(R.string.settings_grocery_export),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            val customGroceryHeader by viewModel.customGroceryHeader.collectAsState()
            var customHeaderInput by remember(customGroceryHeader) { mutableStateOf(customGroceryHeader ?: "") }
            var showHeaderSaveConfirmation by remember { mutableStateOf(false) }
            
            Text(
                text = stringResource(R.string.settings_custom_header_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            OutlinedTextField(
                value = customHeaderInput,
                onValueChange = { 
                    if (it.length <= 100) {
                        customHeaderInput = it
                    }
                },
                label = { Text(stringResource(R.string.settings_custom_header_label)) },
                placeholder = { Text("🛒 ${stringResource(R.string.settings_custom_header_placeholder)}") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3,
                supportingText = {
                    Text(
                        text = "${customHeaderInput.length}/100",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (customHeaderInput.length >= 100) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                trailingIcon = {
                    if (customHeaderInput != (customGroceryHeader ?: "")) {
                        IconButton(
                            onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                coroutineScope.launch {
                                    viewModel.saveCustomGroceryHeader(customHeaderInput.ifBlank { null })
                                }
                                showHeaderSaveConfirmation = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save custom header",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
            
            if (showHeaderSaveConfirmation) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    showHeaderSaveConfirmation = false
                }
                Text(
                    text = stringResource(R.string.settings_custom_header_saved),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Appearance & Accessibility Section
            Text(
                text = stringResource(R.string.settings_appearance),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Font Selection
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.settings_font_family),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        val robotoFonts = com.familymealplanner.ui.theme.AppFont.values()
                            .filter { it.displayName.startsWith("Roboto") }
                        val rubikFonts = com.familymealplanner.ui.theme.AppFont.values()
                            .filter { it.displayName.startsWith("Rubik") }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FontFamilyCard(
                                familyName = "Roboto",
                                description = "Default system font",
                                emoji = "Aa",
                                fonts = robotoFonts,
                                selectedFont = appFont,
                                onFontSelect = { font ->
                                    coroutineScope.launch {
                                        viewModel.saveFont(font)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            
                            FontFamilyCard(
                                familyName = "Rubik",
                                description = "Rounded & friendly",
                                emoji = "Aa",
                                fonts = rubikFonts,
                                selectedFont = appFont,
                                onFontSelect = { font ->
                                    coroutineScope.launch {
                                        viewModel.saveFont(font)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    
                    // Accent Color Picker
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.settings_accent_color),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
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
                                0xFF2F803E  // Green
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
                                        .clickable {
                                            coroutineScope.launch {
                                                if (isDarkTheme) {
                                                    viewModel.saveAccentColorDark(color)
                                                } else {
                                                    viewModel.saveAccentColorLight(color)
                                                }
                                            }
                                        }
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.settings_text_size),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
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
                                coroutineScope.launch {
                                    viewModel.saveTextScale(rounded)
                                }
                            },
                            valueRange = 0.85f..1.15f,
                            steps = 5,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FontFamilyCard(
    familyName: String,
    @Suppress("UNUSED_PARAMETER") description: String,
    emoji: String,
    fonts: List<com.familymealplanner.ui.theme.AppFont>,
    selectedFont: String,
    onFontSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val previewFontFamily = when (familyName) {
        "Roboto" -> com.familymealplanner.ui.theme.AppFonts.getFontFamily("Roboto Regular")
        "Rubik" -> com.familymealplanner.ui.theme.AppFonts.getFontFamily("Rubik Regular")
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
                    fontWeight = FontWeight.SemiBold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.height(24.dp)
                )
            }
            
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
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}


