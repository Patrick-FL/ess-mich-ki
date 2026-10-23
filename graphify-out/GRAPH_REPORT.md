# Graph Report - food_tracker  (2026-10-23)

## Corpus Check
- 57 files · ~57,766 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 405 nodes · 818 edges · 21 communities (18 shown, 3 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 9 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- FoodSearchViewModel
- FoodItemEntity
- LoggedMealEntity
- UserGoalEntity
- EssmichkiTheme
- WeightLogEntity
- DashboardViewModel
- FoodDetailViewModel
- LoggedMealItemEntity
- OpenRouterApiClient
- SettingsDataStore
- AppNavigation.kt
- Implementation Steps
- Converters
- gradlew
- ExampleInstrumentedTest
- ExampleUnitTest

## God Nodes (most connected - your core abstractions)
1. `DashboardViewModel` - 35 edges
2. `LoggedMealItemEntity` - 34 edges
3. `LoggedMealEntity` - 30 edges
4. `FoodItemEntity` - 25 edges
5. `EssmichkiTheme()` - 24 edges
6. `UserGoalEntity` - 22 edges
7. `FoodSearchViewModel` - 21 edges
8. `SettingsDataStore` - 20 edges
9. `AppDatabase` - 18 edges
10. `FoodDetailViewModel` - 18 edges

## Surprising Connections (you probably didn't know these)
- `ProgressViewModel` --calls--> `DailyCalorieStat`  [INFERRED]
  app/src/main/java/com/example/ess_mich_ki/ui/progress/ProgressViewModel.kt → app/src/main/java/com/example/ess_mich_ki/ui/progress/ProgressUiState.kt
- `MainActivity` --references--> `DashboardViewModel`  [EXTRACTED]
  app/src/main/java/com/example/ess_mich_ki/MainActivity.kt → app/src/main/java/com/example/ess_mich_ki/ui/dashboard/DashboardViewModel.kt
- `DashboardViewModelTest` --references--> `SettingsDataStore`  [EXTRACTED]
  app/src/test/java/com/example/ess_mich_ki/ui/DashboardViewModelTest.kt → app/src/main/java/com/example/ess_mich_ki/data/datastore/SettingsDataStore.kt
- `DashboardViewModelTest` --references--> `AppDatabase`  [EXTRACTED]
  app/src/test/java/com/example/ess_mich_ki/ui/DashboardViewModelTest.kt → app/src/main/java/com/example/ess_mich_ki/data/local/database/AppDatabase.kt
- `FakeAppDatabase` --inherits--> `AppDatabase`  [EXTRACTED]
  app/src/test/java/com/example/ess_mich_ki/ui/DashboardViewModelTest.kt → app/src/main/java/com/example/ess_mich_ki/data/local/database/AppDatabase.kt

## Import Cycles
- None detected.

## Communities (21 total, 3 thin omitted)

### Community 0 - "FoodSearchViewModel"
Cohesion: 0.08
Nodes (23): androidx, BarcodeScannerDialog(), VoiceLoggingDialog(), BottomLoggingBar(), FoodSearchContent(), FoodSearchItemRow(), FoodSearchScreen(), FoodSearchScreenPreview() (+15 more)

### Community 1 - "FoodItemEntity"
Cohesion: 0.11
Nodes (10): FoodItemDao, Flow, AppDatabase, AppDatabaseCallback, Context, FoodItemEntity, SampleDataTest, Callback (+2 more)

### Community 2 - "LoggedMealEntity"
Cohesion: 0.10
Nodes (8): Flow, LoggedMealDao, LoggedMealEntity, MealWithItems, DashboardUiState, DashboardStateTest, FakeLoggedMealDao, Flow

### Community 3 - "UserGoalEntity"
Cohesion: 0.11
Nodes (13): Flow, UserGoalDao, UserGoalEntity, AddWeightDialog(), EditGoalDialog(), Modifier, ProgressContent(), ProgressScreen() (+5 more)

### Community 4 - "EssmichkiTheme"
Cohesion: 0.14
Nodes (27): CalorieGaugeCard(), CalorieGaugeCardInRangePreview(), CalorieGaugeCardPreview(), Color, Modifier, MacroProgressRow(), HeaderBar(), HeaderBarPreview() (+19 more)

### Community 5 - "WeightLogEntity"
Cohesion: 0.11
Nodes (9): Flow, WeightLogDao, WeightLogEntity, Factory, StateFlow, T, ViewModel, ViewModelProvider (+1 more)

### Community 6 - "DashboardViewModel"
Cohesion: 0.10
Nodes (6): DashboardViewModel, Factory, StateFlow, T, ViewModel, ViewModelProvider

### Community 7 - "FoodDetailViewModel"
Cohesion: 0.12
Nodes (16): AccordionCard(), FoodDetailContent(), FoodDetailScreen(), FoodDetailScreenPreview(), Color, Modifier, MacroStatCard(), NutritionRow() (+8 more)

### Community 8 - "LoggedMealItemEntity"
Cohesion: 0.07
Nodes (10): Flow, LoggedMealItemDao, LoggedMealItemEntity, DashboardViewModelTest, FakeAppDatabase, FakeContext, FakeLoggedMealItemDao, Context (+2 more)

### Community 9 - "OpenRouterApiClient"
Cohesion: 0.13
Nodes (18): OpenRouterApiClient, OpenRouterChoice, OpenRouterError, OpenRouterMessage, OpenRouterRequest, OpenRouterResponse, OpenRouterResponseFormat, ParsedFoodItem (+10 more)

### Community 10 - "SettingsDataStore"
Cohesion: 0.13
Nodes (10): Flow, SettingsDataStore, UserSettings, Factory, StateFlow, T, ViewModel, ViewModelProvider (+2 more)

### Community 11 - "AppNavigation.kt"
Cohesion: 0.16
Nodes (15): MainActivity, AppNavigation(), Modifier, NavTab, PLAN, PROGRESS, SEARCH, STREAKS (+7 more)

### Community 12 - "Implementation Steps"
Cohesion: 0.18
Nodes (10): Features, High-Level Tech Stack, Implementation Steps, Project Brief, Project Brief: ess-mich-ki (Food Tracker), Project Plan, Task_1_DataAndNetworkSetup: Implement Room database (entities & DAOs for meals, foods, weight, streaks), Jetpack DataStore for API key & OpenRouter base URL settings, and OpenRouter API networking client for natural language food parsing., Task_2_AILoggingAndDashboardUI: Build the Dashboard screen with calorie gauge, macro progress bars, meal sections, streak header, and AI natural language food logging interface integrated with OpenRouter API. (+2 more)

### Community 14 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **17 isolated node(s):** `OpenRouterChoice`, `OpenRouterResponse`, `OpenRouterError`, `PLAN`, `SEARCH` (+12 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `SettingsDataStore` connect `SettingsDataStore` to `FoodSearchViewModel`, `EssmichkiTheme`, `DashboardViewModel`, `FoodDetailViewModel`, `LoggedMealItemEntity`?**
  _High betweenness centrality (0.134) - this node is a cross-community bridge._
- **Why does `DashboardViewModel` connect `DashboardViewModel` to `LoggedMealItemEntity`, `LoggedMealEntity`, `AppNavigation.kt`, `EssmichkiTheme`?**
  _High betweenness centrality (0.132) - this node is a cross-community bridge._
- **Why does `EssmichkiTheme()` connect `EssmichkiTheme` to `FoodSearchViewModel`, `UserGoalEntity`, `FoodDetailViewModel`, `OpenRouterApiClient`, `AppNavigation.kt`?**
  _High betweenness centrality (0.111) - this node is a cross-community bridge._
- **What connects `OpenRouterChoice`, `OpenRouterResponse`, `OpenRouterError` to the rest of the system?**
  _17 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `FoodSearchViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.0753045404208195 - nodes in this community are weakly interconnected._
- **Should `FoodItemEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.11494252873563218 - nodes in this community are weakly interconnected._
- **Should `LoggedMealEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.1006006006006006 - nodes in this community are weakly interconnected._