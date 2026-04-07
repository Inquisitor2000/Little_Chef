# Family Meal Planner

A modern, multilingual Android meal planning application built with Jetpack Compose that helps families organize recipes, plan meals, manage pantry inventory, and generate smart shopping lists.

## ✨ Features

### 🍳 Recipe Management
- **Extensive Recipe Library**: Browse 75+ pre-loaded recipes across multiple cuisines (Asian, Mediterranean, Mexican, Italian, French, Bread & Bakery, Desserts & Sweets)
- **Custom Recipes**: Add your own recipes manually or scrape from websites using OpenAI integration
- **Recipe Details**: View ingredients, instructions, prep/cook times, and allergen information
- **Smart Suggestions**: Get meal recommendations based on available pantry ingredients

### 📅 Meal Planning
- **Weekly Calendar**: Plan meals with an intuitive calendar interface
- **Meal Types**: Organize by breakfast, lunch, dinner, and snacks
- **Drag & Drop**: Easy meal scheduling
- **Cooking Mode**: Step-by-step cooking instructions with ingredient tracking

### 🛒 Smart Shopping
- **Auto-Generated Lists**: Create shopping lists from meal plans
- **Pantry Integration**: Only add items you don't have in stock
- **Ingredient Grouping**: Organized by category for efficient shopping
- **Check-off Items**: Track purchases in real-time
- **Custom Export**: Share lists with customizable headers

### 🏺 Pantry Management
- **Inventory Tracking**: Monitor ingredient quantities and availability
- **Reserved Quantities**: Track ingredients allocated to planned meals
- **Custom Ingredients**: Add items not in the catalog
- **Multi-language Support**: Ingredients created in any supported language
- **Allergen Management**: Track and filter by dietary restrictions

### 🎨 Customization & Accessibility
- **Theme Options**: Light and dark mode support
- **Font Selection**: Choose between Roboto and Rubik families (Light, Regular, Medium)
- **Accent Colors**: 3 preset color schemes per theme
- **Text Scaling**: Adjustable from 85% to 115% (default: 95%)
- **Responsive Design**: Optimized for various screen sizes

### 🌍 Multilingual
- **3 Languages**: English, Russian, and Romanian
- **Dynamic Translation**: Recipes and ingredients translated on-the-fly
- **Language Indicators**: See which language an ingredient was created in

### 🎯 Smart Features
- **Allergen Tracking**: Manage family allergens and get warnings
- **Ingredient Substitutions**: Suggest alternatives for missing items
- **Voice Input**: Add ingredients via voice (experimental)
- **Auto-Hide Navigation**: Collapsible navigation bar for distraction-free reading
- **Offline First**: All data stored locally for offline access

## 🏗️ Tech Stack

### Core Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Architecture**: MVVM with Clean Architecture principles
- **Database**: Room with SQLite
- **Dependency Injection**: Hilt/Dagger
- **Async**: Kotlin Coroutines & Flow
- **Build System**: Gradle with Kotlin DSL

### Key Libraries
- **Navigation**: Jetpack Navigation Compose
- **Image Loading**: Coil
- **Data Storage**: DataStore (Preferences)
- **JSON Parsing**: Kotlinx Serialization
- **AI Integration**: OpenAI API (optional, for recipe scraping)

### Architecture Highlights
- Clean separation of concerns (Data, Domain, UI layers)
- Repository pattern for data access
- Use cases for business logic
- Reactive UI with StateFlow
- Dependency injection throughout

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK with:
  - Minimum SDK: 26 (Android 8.0)
  - Target SDK: 34 (Android 14)
  - Compile SDK: 34

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/family-meal-planner.git
   cd family-meal-planner
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Sync Gradle**
   - Android Studio will automatically prompt to sync
   - Wait for dependencies to download

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press Shift+F10

### Optional: OpenAI Integration
To enable recipe scraping from websites:
1. Get an API key from [OpenAI](https://platform.openai.com/)
2. In the app, go to Settings → Recipe Gathering
3. Enter your API key

## 📱 App Structure

```
app/src/main/java/com/familymealplanner/
├── data/                    # Data layer
│   ├── local/              # Room database, DAOs, entities
│   ├── preferences/        # DataStore preferences
│   └── repository/         # Repository implementations
├── domain/                  # Domain layer
│   ├── model/              # Domain models
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Business logic use cases
├── ui/                      # UI layer
│   ├── navigation/         # Navigation setup
│   ├── onboarding/         # Onboarding screens
│   ├── screens/            # App screens
│   ├── theme/              # Theme configuration
│   └── util/               # UI utilities
└── MainActivity.kt          # Entry point
```

## 🎨 Design System

### Typography Scale
- **Display**: 36sp (decorative)
- **Headlines**: 24-28sp (screen titles)
- **Titles**: 16-22sp (section headers)
- **Body**: 14-16sp (content)
- **Labels**: 12-16sp (buttons, tags)

### Navigation
- **Bottom Navigation**: Auto-collapsing pill-shaped bar
- **4 Main Sections**: Plan, Meals, Groceries, Pantry
- **Settings**: Accessible from Pantry screen
- **Smooth Animations**: Spring-based transitions

### Color Scheme
- **Light Theme**: 3 warm accent options
- **Dark Theme**: 3 cool accent options
- **Semantic Colors**: Error, success, warning states
- **Accessibility**: WCAG AA compliant contrast ratios

## 🔧 Configuration

### Build Variants
- **Debug**: Development build with logging
- **Release**: Optimized production build with ProGuard

### Gradle Configuration
```kotlin
minSdk = 26
targetSdk = 34
compileSdk = 34
kotlinCompilerExtensionVersion = "1.5.8"
```

## 📊 Database Schema

### Core Tables
- **meals**: User recipes and scraped recipes
- **ingredients**: Ingredient catalog and custom items
- **pantry_items**: Inventory tracking
- **meal_plans**: Scheduled meals
- **grocery_items**: Shopping list items
- **allergens**: Dietary restrictions

### Relationships
- Meals ↔ Ingredients (many-to-many)
- Meal Plans → Meals (one-to-many)
- Pantry Items → Ingredients (one-to-one)
- Grocery Items → Ingredients (one-to-one)

## 🌟 Recent Updates

### Navigation Improvements
- Auto-collapsing navigation bar after 15 seconds of inactivity
- Smooth spring animations for expand/collapse
- Consistent icon sizing (26dp)
- Improved selection state tracking

### Typography Enhancements
- Increased button text to 16sp for better readability
- Updated small text sizes (labelSmall: 12sp, bodySmall: 14sp, titleSmall: 16sp)
- Default text scale set to 95% for optimal spacing
- User-adjustable scaling preserved in preferences

### Settings Access
- Moved settings button from navigation bar to Pantry screen
- Larger settings icon (26dp) for better visibility
- Streamlined navigation flow

## 🤝 Contributing

This is a personal/educational project, but suggestions and feedback are welcome! Feel free to:
- Report bugs via GitHub Issues
- Suggest features or improvements
- Submit pull requests

## 📄 License

This project is for educational and personal use.

## 🙏 Acknowledgments

- Recipe data sourced from various public recipe databases
- Icons from Material Design Icons
- Built with love using Jetpack Compose

---

**Note**: This app stores all data locally on your device. No data is sent to external servers except when using the optional OpenAI recipe scraping feature.

