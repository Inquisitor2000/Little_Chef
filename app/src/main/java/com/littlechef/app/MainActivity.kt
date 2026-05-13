package com.littlechef.app

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.littlechef.app.data.preferences.LocaleManager
import com.littlechef.app.data.preferences.OnboardingPreferences
import com.littlechef.app.ui.navigation.AppNavHost
import com.littlechef.app.ui.navigation.NavDestination
import com.littlechef.app.ui.onboarding.OnboardingScreen
import com.littlechef.app.ui.theme.LittleChefTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    @Inject
    lateinit var translationSystem: com.littlechef.app.data.local.TranslationSystem

    @Inject
    lateinit var localeManager: LocaleManager

    @Inject
    lateinit var preloadCuisineAllergensUseCase: com.littlechef.app.domain.usecase.PreloadCuisineAllergensUseCase
    
    override fun attachBaseContext(newBase: Context) {
        val localeManager = LocaleManager(newBase)
        val savedLanguage = localeManager.getLanguage()
        
        val contextWithLocale = localeManager.applyLocale(newBase)
        
        super.attachBaseContext(contextWithLocale)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        val currentLocale = resources.configuration.locales[0].language
        
        super.onCreate(savedInstanceState)
        
        // Check if we have pending onboarding completion (after recreation)
        lifecycleScope.launch {
            val onboardingPrefs = OnboardingPreferences(applicationContext)
            if (onboardingPrefs.hasPendingOnboardingCompletion.first()) {
                onboardingPrefs.setOnboardingCompleted()
            }
        }
        
        // Sync language from saved preference — needed after activity recreation post-onboarding
        // (Application.onCreate() already ran with the default language, so TranslationSystem
        // needs to pick up the language the user selected during onboarding).
        val currentLanguage = localeManager.getLanguage()
        translationSystem.setLanguage(currentLanguage)

        // Load translation data (ingredient/category name maps) off the main thread.
        // This is ~29KB I/O + JSON parsing for non-English users; English users skip it.
        lifecycleScope.launch(Dispatchers.IO) {
            translationSystem.loadTranslationData(currentLanguage)
        }
        
        // Preload allergen cache in background to prevent frame drops when opening cuisine screens
        preloadCuisineAllergensUseCase.preload()
        
        
        // Pre-load accent colors synchronously to avoid flash on startup
        val onboardingPrefs = OnboardingPreferences(applicationContext)
        val initialAccentColorLight = kotlinx.coroutines.runBlocking {
            onboardingPrefs.accentColorLight.first()
        }
        val initialAccentColorDark = kotlinx.coroutines.runBlocking {
            onboardingPrefs.accentColorDark.first()
        }
        val initialTextScale = kotlinx.coroutines.runBlocking {
            onboardingPrefs.textScale.first()
        }
        val initialAppFont = kotlinx.coroutines.runBlocking {
            onboardingPrefs.appFont.first()
        }
        
        setContent {
            // Force recomposition when configuration changes
            val configuration = androidx.compose.ui.platform.LocalConfiguration.current
            
            val viewModel: MainViewModel = hiltViewModel()
            val textScale by viewModel.textScale.collectAsState()
            val appFont by viewModel.appFont.collectAsState()
            val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
            val accentColorLight by viewModel.accentColorLight.collectAsState()
            val accentColorDark by viewModel.accentColorDark.collectAsState()
            
            // Use pre-loaded values on first render, then switch to reactive values
            val effectiveTextScale = if (textScale == 0.94f) initialTextScale else textScale
            val effectiveAppFont = if (appFont == "Roboto Medium") initialAppFont else appFont
            val effectiveAccentColorLight = if (accentColorLight == 0xFFD68C45L) initialAccentColorLight else accentColorLight
            val effectiveAccentColorDark = if (accentColorDark == 0xFF5398beL) initialAccentColorDark else accentColorDark
            
            val accentColor = if (isDarkTheme) effectiveAccentColorDark else effectiveAccentColorLight
            val fontFamily = com.littlechef.app.ui.theme.AppFonts.getFontFamily(effectiveAppFont)
            
            LittleChefTheme(
                textScale = effectiveTextScale, 
                fontFamily = fontFamily,
                accentColor = androidx.compose.ui.graphics.Color(accentColor)
            ) {
                val focusManager = LocalFocusManager.current
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { focusManager.clearFocus() },
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences
) : ViewModel() {
    // Use nullable Boolean to track loading state
    val isOnboardingComplete: StateFlow<Boolean?> = onboardingPreferences.hasCompletedOnboarding
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null // null means we're still loading
        )
    
    val textScale: StateFlow<Float> = onboardingPreferences.textScale.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 1.0f
    )
    
    val appFont: StateFlow<String> = onboardingPreferences.appFont.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = "Roboto Medium"
    )
    
    val accentColorLight: StateFlow<Long> = onboardingPreferences.accentColorLight.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0xFFD68C45 // Toasted Almond
    )
    
    val accentColorDark: StateFlow<Long> = onboardingPreferences.accentColorDark.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0xFF5398be // Blue Bell
    )
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val isOnboardingComplete by viewModel.isOnboardingComplete.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        androidx.compose.animation.Crossfade(
            targetState = when {
                isOnboardingComplete == null -> "loading"
                isOnboardingComplete == true -> "main"
                else -> "onboarding"
            },
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 300,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            ),
            label = "main_screen_transition"
        ) { state ->
            when (state) {
                "loading" -> {
                    // Show blank screen while loading preferences
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
                "main" -> {
                    MainAppScreen()
                }
                "onboarding" -> {
                    OnboardingScreen()
                }
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    val isDarkTheme = isSystemInDarkTheme()
    val haptic = com.littlechef.app.ui.util.rememberHapticFeedback()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Helper function to determine which bottom nav item should be selected
    fun getSelectedBottomNavRoute(currentRoute: String?): String? {
        return when {
            currentRoute == null -> null
            // Recipe detail screens belong to Meals
            currentRoute.startsWith("recipe_detail/") -> NavDestination.Meals.route
            currentRoute.startsWith("bundled_recipe/") -> NavDestination.Meals.route
            currentRoute.startsWith("cuisine_meals/") -> NavDestination.Meals.route
            currentRoute.startsWith("scrape_recipe") -> NavDestination.Meals.route
            currentRoute.startsWith("manual_recipe") -> NavDestination.Meals.route
            currentRoute.startsWith("edit_recipe/") -> NavDestination.Meals.route
            // Meal plan detail belongs to Plan
            currentRoute.startsWith("meal_plan_detail/") -> NavDestination.Plan.route
            currentRoute.startsWith("suggestion") -> NavDestination.Plan.route
            // Custom ingredient screens belong to their parent
            currentRoute.startsWith("add_custom_ingredient_grocery") -> NavDestination.Groceries.route
            currentRoute.startsWith("add_custom_ingredient") -> NavDestination.Pantry.route
            // Settings screen
            currentRoute.startsWith("settings") -> null // No selection for settings
            // Default: check if it's a bottom nav route
            else -> {
                NavDestination.bottomNavItems.find { it.route == currentRoute }?.route
            }
        }
    }
    
    // Auto-collapse navigation bar after 15 seconds of inactivity
    var isNavBarExpanded by remember { mutableStateOf(true) }
    var lastInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }
    
    val navBarWidth by animateDpAsState(
        targetValue = if (isNavBarExpanded) 0.dp else 56.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "navBarWidth"
    )
    val navBarHorizontalPadding by animateDpAsState(
        targetValue = if (isNavBarExpanded) 48.dp else 16.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "navBarHorizontalPadding"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (isNavBarExpanded) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = if (isNavBarExpanded) 100 else 0,
            easing = FastOutSlowInEasing
        ),
        label = "contentAlpha"
    )
    val menuIconAlpha by animateFloatAsState(
        targetValue = if (isNavBarExpanded) 0f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = if (isNavBarExpanded) 0 else 100,
            easing = FastOutSlowInEasing
        ),
        label = "menuIconAlpha"
    )
    
    // Monitor for inactivity
    LaunchedEffect(lastInteractionTime) {
        isNavBarExpanded = true
        delay(15000) // Wait 15 seconds
        isNavBarExpanded = false // Collapse
    }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = navBarHorizontalPadding, vertical = 24.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .then(
                            if (isNavBarExpanded) {
                                Modifier.fillMaxWidth()
                            } else {
                                Modifier.width(50.dp)
                            }
                        )
                        .height(50.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isNavBarExpanded) {
                                haptic.performLight()
                                isNavBarExpanded = true
                                lastInteractionTime = System.currentTimeMillis()
                            }
                        }
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    if (isNavBarExpanded) {
                        NavigationBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                                .alpha(contentAlpha),
                            containerColor = Color.Transparent,
                            tonalElevation = 0.dp
                        ) {
                            NavDestination.bottomNavItems.forEach { destination ->
                                val selectedRoute = getSelectedBottomNavRoute(currentDestination?.route)
                                val selected = selectedRoute == destination.route

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            if (!selected) {
                                                haptic.performLight()
                                                lastInteractionTime = System.currentTimeMillis()
                                                navController.navigate(destination.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        inclusive = false
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = false
                                                }
                                            } else {
                                                lastInteractionTime = System.currentTimeMillis()
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selected) {
                                        Surface(
                                            modifier = Modifier
                                                .height(42.dp)
                                                .fillMaxWidth(1f),
                                            shape = RoundedCornerShape(21.dp),
                                            color = if (isDarkTheme) {
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                            } else {
                                                MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
                                            }
                                        ) {}
                                    }
                                    Icon(
                                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                        contentDescription = androidx.compose.ui.res.stringResource(destination.titleRes),
                                        modifier = Modifier.size(26.dp),
                                        tint = if (isDarkTheme) {
                                            // Dark mode: icons use screen background color
                                            MaterialTheme.colorScheme.background
                                        } else {
                                            // Light mode: icons use text color
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        // Collapsed state - show menu icon
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(menuIconAlpha),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Expand navigation",
                                modifier = Modifier.size(26.dp),
                                tint = if (isDarkTheme) {
                                    MaterialTheme.colorScheme.background
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }
}
