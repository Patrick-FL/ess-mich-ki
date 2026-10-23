# Project Plan

Build a Food Tracking Android App ("ess-mich-ki") step by step based on provided design mockups and specific functional requirements:

Key Requirements:
1. Local & Offline First: No account required. All data (meals, food database, goals, streak, weight tracking, settings) saved locally (e.g. Room database / DataStore).
2. AI Natural Language Logging & OpenRouter Integration:
   - Free text input for food logging (e.g., "I ate 500g soy yogurt with raspberries and chocolate").
   - Uses OpenRouter API. User enters OpenRouter API Key in app settings, stored securely/locally.
   - User selectable OpenRouter Base URL routing in Settings:
     * Standard: https://openrouter.ai/api/v1
     * EU: https://eu.openrouter.ai/api/v1
     * US: https://us.openrouter.ai/api/v1
   - AI converts free text input into structured food entries mapped to/stored in local database.
3. Screens & Features based on Mockups:
   - Today / Dashboard Screen (Image 2):
     * Day selector calendar header (M T W T F S S), streak count & avatar.
     * Calorie gauge (e.g. 632 / 1,581 kcal) with range markers and macro progress indicators (Proteins, Carbs, Fats).
     * "End Day" button.
     * Meal sections (e.g. Breakfast, Lunch, Dinner, Snack) with itemized items, calorie/macro summary, checkmarks, and (+) add food button.
   - Food Search / Log Screen (Image 3):
     * Search bar with country/language flag selector.
     * Tabs: Database, Favorites, Created.
     * Sections: Recently Entered foods (with verified badge), Recent Meals.
     * Logging options bottom bar: Recipes, Free Text List (A|I), Search, Scan (barcode/camera mock), Voice.
   - Food Detail / Item Screen (Image 4):
     * Header image/icon, item name, brand/category.
     * Macro cards (kcal, protein, carbs, fat).
     * "Analyze with AI Coach" action.
     * Macro distribution percentage bar (e.g. Carbs 100%).
     * Expandable sections for Nutrition Facts & Micronutrients (Sugars, Fiber, Salt, Sat. Fat) (Image 6).
     * Quantity and Serving selector (e.g. 1 Tasse / 150g).
     * "Add to Meal" (e.g. Add to Breakfast) button.
   - Progress & Stats Screen (Image 5):
     * Nutrition Score weekly score calendar view.
     * Active Goal card ("Weight Maintenance" / "Fat Loss") with weight slider (Current vs Goal weight) and "+ Update Progress".
     * Weekly stats graphs for Calories and Scale Weight.
     * Bottom Navigation bar: Plan, Search, Social, Progress, Coach.
   - Streaks Screen (Image 8):
     * Logged Days streak counter & Best streak.
     * Perfect Days streak counter & Best streak.
     * Monthly calendar heat map / average calorie log for each day.
   - Edit Goal Dialog / Screen (Image 9):
     * Current goal (e.g. Fat Loss, weight target).
     * Edit Initial Weight, Goal Weight.
     * Time to goal pace slider (Slow / Recommended / Fast -> calculated weeks & calorie target e.g. 1284 kcal).
     * Save action.
   - Profile / Settings Side Drawer or Screen (Image 7):
     * User profile avatar & name ("Szent").
     * Navigation to Meal Plans, Shopping List, Fasting, What's New, Settings (OpenRouter Key & Routing URL configuration).

## Project Brief

# Project Brief: ess-mich-ki (Food Tracker)

## Features

1. **Dashboard & Daily Macro Tracking**
   - Calorie gauge and macro progress bars (Proteins, Carbs, Fats) with daily range indicators.
   - Meal section logging (Breakfast, Lunch, Dinner, Snack) with itemized items and meal summary.
   - Calendar day selector header and daily streak counter (Logged Days & Perfect Days).

2. **AI Natural Language Food Logging**
   - Free text food input (e.g., "I ate 500g soy yogurt with raspberries and chocolate").
   - Direct OpenRouter API integration with user-configurable base URL routing (Standard, EU, US) and secure local API key storage.
   - Automated conversion of natural language descriptions into structured local meal entries.

3. **Local Food Database & Item Details**
   - Offline food search across database entries, recent foods, and favorites.
   - Comprehensive nutrition breakdown including macro distribution and expandable micronutrients (sugars, fiber, salt, sat. fat) with custom quantity and serving selectors.

4. **Goal Management & Weight Analytics**
   - Goal configuration (Fat Loss / Maintenance) with current and target weight controls.
   - Dynamic pace slider (Slow, Recommended, Fast) that calculates daily calorie target and estimated timeframe.
   - Progress charts tracking weekly calorie intake and scale weight trends.

## High-Level Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Navigation & Adaptive Layouts**: Jetpack Navigation 3 (state-driven navigation) & Compose Material Adaptive library
- **Architecture & Async**: MVVM Architecture, Kotlin Coroutines, Flow, StateFlow, ViewModel
- **Local Persistence & Storage**: Room Database (local food entries, logged meals, streaks, weight logs) & Jetpack DataStore (secure API key and base URL settings)
- **Networking & JSON Parsing**: Ktor Client (or Retrofit) with Kotlinx Serialization for OpenRouter API natural language logging

## Implementation Steps
**Total Duration:** 21m 17s

### Task_1_DataAndNetworkSetup: Implement Room database (entities & DAOs for meals, foods, weight, streaks), Jetpack DataStore for API key & OpenRouter base URL settings, and OpenRouter API networking client for natural language food parsing.
- **Status:** COMPLETED
- **Updates:** Room database (entities, DAOs, pre-populated sample foods, MealWithItems relation), DataStore settings (API key, OpenRouter base URL selection), and OpenRouter AI network client (OkHttp + Moshi natural language food parser) successfully implemented and tested with 100% passing tests and clean assembleDebug build.
- **Acceptance Criteria:**
  - Room database entities and DAOs configured
  - DataStore implementation for API key and base URL selection (Standard, EU, US)
  - OpenRouter API client integrated with Kotlinx Serialization
  - API_KEY integration configured as setting and header
  - build pass
- **Duration:** 3m 45s

### Task_2_AILoggingAndDashboardUI: Build the Dashboard screen with calorie gauge, macro progress bars, meal sections, streak header, and AI natural language food logging interface integrated with OpenRouter API.
- **Status:** COMPLETED
- **Updates:** Dashboard UI complete with header bar, date selector strip, streak flame badge, custom canvas calorie arc gauge, macro progress indicators (Proteins, Carbs, Fats), itemized meal cards with green checkmark toggles, OpenRouter AI natural language food logging dialog, and settings sheet with OpenRouter API key and base URL routing configuration. Unit tests passing and build succeeded.
- **Acceptance Criteria:**
  - Dashboard displays calorie gauge, macro progress bars (protein/carb/fat), and meal sections
  - Date selector and streak counter functional
  - AI text input logs natural language foods directly into local meal entries via OpenRouter
  - build pass
- **Duration:** 3m 19s

### Task_3_FoodDatabaseAndGoalsAnalyticsUI: Implement local food search and details views with nutrition breakdown, plus Goal Management and Weight Analytics screen with dynamic pace slider and weight tracking charts.
- **Status:** COMPLETED
- **Updates:** Implemented Food Search Screen (flag selector, tabs, recently entered, recent meals, bottom bar), Food Detail Screen (4 macro cards, Fitia AI coach prompt, carbs bar, micronutrients accordion, quantity/serving selectors, add to meal action), Progress Screen (nutrition score calendar, weight goal card, calorie & scale weight charts), Streaks Screen (logged & perfect days flame stats, monthly heat map grid), Edit Goal sheet (dynamic pace slider and calorie target calculation), and complete app navigation. All unit tests passing and assembleDebug clean.
- **Acceptance Criteria:**
  - Offline food search, favorites, and detailed micronutrient view functional
  - Goal configuration with fat loss/maintenance and dynamic pace slider working
  - Weight trend tracking and streak calendar view implemented
  - build pass
- **Duration:** 4m 37s

### Task_4_RunAndVerify: Configure app icons and assets, perform final UI/UX polish, run build and existing test suite, verify app stability and core user workflows without crashes.
- **Status:** COMPLETED
- **Updates:** Final verification completed by critic_agent on standard phone form factor. App built, deployed, and verified with 0 crashes. Dashboard, Calorie Arc Gauge, Macro progress bars, Meal cards, Food search & database view, Food detail screen with micronutrients accordion, Progress & Stats tab, Streaks calendar screen, Goal management slider, and OpenRouter Settings with standard/EU/US routing selections were all confirmed fully operational and matching requirements. All unit tests passed (13/13).
- **Acceptance Criteria:**
  - app icon and basic branding assets integrated
  - make sure all existing tests pass
  - build pass
  - app does not crash
  - critic_agent verified application stability and alignment with user requirements
- **Duration:** 9m 36s

