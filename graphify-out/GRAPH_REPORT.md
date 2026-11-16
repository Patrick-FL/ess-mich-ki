# Graph Report - food_tracker  (2026-11-16)

## Corpus Check
- 64 files · ~103,698 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 519 nodes · 1035 edges · 25 communities (20 shown, 5 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 16 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `24087e14`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- 🥗 EssMichKI – Smarter KI-Mahlzeiten- & Nährstofftracker
- FoodItemEntity
- LoggedMealEntity
- UserGoalEntity
- EssmichkiTheme
- NutritionalGoalState
- DashboardViewModel
- FoodDetailViewModel
- LoggedMealItemEntity
- AiFoodLoggingDialog.kt
- DashboardViewModelTest
- DayItem
- Implementation Steps
- Converters
- gradlew
- ExampleInstrumentedTest
- ExampleUnitTest
- ServingUnitUtils
- .calculateMonthlyCalendar
- CsvExportImportUtil
- CanonicalMealType

## God Nodes (most connected - your core abstractions)
1. `DashboardViewModel` - 61 edges
2. `LoggedMealItemEntity` - 46 edges
3. `LoggedMealEntity` - 36 edges
4. `UserGoalEntity` - 24 edges
5. `MealWithItems` - 24 edges
6. `ProgressViewModel` - 23 edges
7. `EssmichkiTheme()` - 23 edges
8. `DashboardViewModelTest` - 21 edges
9. `LoggedMealDao` - 20 edges
10. `FoodItemEntity` - 19 edges

## Surprising Connections (you probably didn't know these)
- `ProgressScreenPreview()` --calls--> `DailyCalorieStat`  [INFERRED]
  app/src/main/java/com/example/ess_mich_ki/ui/progress/ProgressScreen.kt → app/src/main/java/com/example/ess_mich_ki/ui/progress/ProgressUiState.kt
- `ProgressViewModel` --calls--> `DailyCalorieStat`  [INFERRED]
  app/src/main/java/com/example/ess_mich_ki/ui/progress/ProgressViewModel.kt → app/src/main/java/com/example/ess_mich_ki/ui/progress/ProgressUiState.kt
- `NutritionalGoalsDialog()` --calls--> `NutritionalGoalState`  [INFERRED]
  app/src/main/java/com/example/ess_mich_ki/ui/progress/components/NutritionalGoalsDialog.kt → app/src/main/java/com/example/ess_mich_ki/util/NutritionalGoalMath.kt
- `MainActivity` --references--> `DashboardViewModel`  [EXTRACTED]
  app/src/main/java/com/example/ess_mich_ki/MainActivity.kt → app/src/main/java/com/example/ess_mich_ki/ui/dashboard/DashboardViewModel.kt
- `DashboardViewModelTest` --references--> `AppDatabase`  [EXTRACTED]
  app/src/test/java/com/example/ess_mich_ki/ui/DashboardViewModelTest.kt → app/src/main/java/com/example/ess_mich_ki/data/local/database/AppDatabase.kt

## Import Cycles
- None detected.

## Communities (25 total, 5 thin omitted)

### Community 0 - "🥗 EssMichKI – Smarter KI-Mahlzeiten- & Nährstofftracker"
Cohesion: 0.10
Nodes (20): 🔒 100 % Privacy First & Datensouveränität, 1. Voraussetzungen, 2. OpenRouter API-Schlüssel hinterlegen, 🏗️ Architektur & Technologie-Stack, Bauen & Ausführen, 📖 Bedienungsanleitung, 💾 Daten exportieren, 🥗 EssMichKI – Smarter KI-Mahlzeiten- & Nährstofftracker (+12 more)

### Community 1 - "FoodItemEntity"
Cohesion: 0.12
Nodes (10): FoodItemDao, Flow, AppDatabase, AppDatabaseCallback, Context, FoodItemEntity, SampleDataTest, Callback (+2 more)

### Community 2 - "LoggedMealEntity"
Cohesion: 0.07
Nodes (7): Flow, LoggedMealDao, LoggedMealEntity, MealWithItems, FakeLoggedMealDao, Flow, CsvExportImportUtilTest

### Community 3 - "UserGoalEntity"
Cohesion: 0.05
Nodes (31): Flow, UserGoalDao, Flow, WeightLogDao, UserGoalEntity, WeightLogEntity, AddWeightDialog(), EditGoalDialog() (+23 more)

### Community 4 - "EssmichkiTheme"
Cohesion: 0.09
Nodes (35): MainActivity, CalorieGaugeCard(), CalorieGaugeCardPreview(), Color, Modifier, MacroProgressRow(), FoodItemRow(), getEmojiForFood() (+27 more)

### Community 5 - "NutritionalGoalState"
Cohesion: 0.14
Nodes (7): MacroType, CARBS, FAT, PROTEIN, NutritionalGoalMath, NutritionalGoalState, NutritionalGoalMathTest

### Community 6 - "DashboardViewModel"
Cohesion: 0.07
Nodes (8): android, DashboardViewModel, Factory, Context, StateFlow, T, ViewModel, ViewModelProvider

### Community 7 - "FoodDetailViewModel"
Cohesion: 0.09
Nodes (22): EditLoggedMealItemDialog(), Modifier, SheetState, PureUnitOption, AccordionCard(), FoodDetailContent(), FoodDetailScreen(), FoodDetailScreenPreview() (+14 more)

### Community 8 - "LoggedMealItemEntity"
Cohesion: 0.15
Nodes (4): Flow, LoggedMealItemDao, LoggedMealItemEntity, FakeLoggedMealItemDao

### Community 9 - "AiFoodLoggingDialog.kt"
Cohesion: 0.11
Nodes (20): Result, OpenRouterApiClient, OpenRouterChoice, OpenRouterError, OpenRouterMessage, OpenRouterRequest, OpenRouterResponse, OpenRouterResponseFormat (+12 more)

### Community 10 - "DashboardViewModelTest"
Cohesion: 0.06
Nodes (18): Flow, SettingsDataStore, UserSettings, Factory, StateFlow, T, ViewModel, ViewModelProvider (+10 more)

### Community 11 - "DayItem"
Cohesion: 0.22
Nodes (6): HeaderBar(), HeaderBarPreview(), Modifier, DashboardUiState, DayItem, DashboardStateTest

### Community 12 - "Implementation Steps"
Cohesion: 0.18
Nodes (10): Features, High-Level Tech Stack, Implementation Steps, Project Brief, Project Brief: ess-mich-ki (Food Tracker), Project Plan, Task_1_DataAndNetworkSetup: Implement Room database (entities & DAOs for meals, foods, weight, streaks), Jetpack DataStore for API key & OpenRouter base URL settings, and OpenRouter API networking client for natural language food parsing., Task_2_AILoggingAndDashboardUI: Build the Dashboard screen with calorie gauge, macro progress bars, meal sections, streak header, and AI natural language food logging interface integrated with OpenRouter API. (+2 more)

### Community 14 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 22 - ".calculateMonthlyCalendar"
Cohesion: 0.13
Nodes (13): Color, Modifier, StreaksScreen(), StreaksScreenPreview(), StreakStatCard(), CalendarWeekInfo, MonthDayInfo, MonthlyCalendarData (+5 more)

### Community 27 - "CanonicalMealType"
Cohesion: 0.33
Nodes (5): CanonicalMealType, BREAKFAST, DINNER, LUNCH, SNACK

## Knowledge Gaps
- **36 isolated node(s):** `OpenRouterChoice`, `OpenRouterResponse`, `OpenRouterError`, `PLAN`, `PROGRESS` (+31 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **5 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `DashboardViewModel` connect `DashboardViewModel` to `LoggedMealEntity`, `EssmichkiTheme`, `LoggedMealItemEntity`, `AiFoodLoggingDialog.kt`, `DashboardViewModelTest`, `DayItem`, `.calculateMonthlyCalendar`?**
  _High betweenness centrality (0.203) - this node is a cross-community bridge._
- **Why does `LoggedMealItemEntity` connect `LoggedMealItemEntity` to `FoodItemEntity`, `LoggedMealEntity`, `EssmichkiTheme`, `DashboardViewModel`, `FoodDetailViewModel`, `DashboardViewModelTest`, `DayItem`?**
  _High betweenness centrality (0.131) - this node is a cross-community bridge._
- **Why does `EssmichkiTheme()` connect `EssmichkiTheme` to `UserGoalEntity`, `FoodDetailViewModel`, `AiFoodLoggingDialog.kt`, `DayItem`, `.calculateMonthlyCalendar`?**
  _High betweenness centrality (0.117) - this node is a cross-community bridge._
- **What connects `OpenRouterChoice`, `OpenRouterResponse`, `OpenRouterError` to the rest of the system?**
  _36 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `🥗 EssMichKI – Smarter KI-Mahlzeiten- & Nährstofftracker` be split into smaller, more focused modules?**
  _Cohesion score 0.09523809523809523 - nodes in this community are weakly interconnected._
- **Should `FoodItemEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.1206896551724138 - nodes in this community are weakly interconnected._
- **Should `LoggedMealEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.07013574660633484 - nodes in this community are weakly interconnected._