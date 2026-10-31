# Graph Report - food_tracker  (2026-10-31)

## Corpus Check
- 64 files · ~67,515 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 497 nodes · 985 edges · 32 communities (26 shown, 6 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 16 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `833c64f7`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- 🥗 EssMichKI – Smarter KI-Mahlzeiten- & Nährstofftracker
- FoodItemEntity
- LoggedMealEntity
- ProgressViewModel
- EssmichkiTheme
- NutritionalGoalState
- DashboardViewModel
- FoodDetailViewModel
- DashboardViewModelTest
- AiFoodLoggingDialog.kt
- SettingsDataStore
- UserGoalEntity
- Implementation Steps
- Converters
- gradlew
- ExampleInstrumentedTest
- ExampleUnitTest
- ProgressScreen.kt
- .calculateMonthlyCalendar
- LoggedMealItemEntity
- WeightLogEntity
- FakeLoggedMealDao
- CsvExportImportUtil
- .fromString
- FakeAppDatabase
- AppNavigation.kt
- Task3ViewModelsTest.kt
- DashboardViewModelTest.kt

## God Nodes (most connected - your core abstractions)
1. `DashboardViewModel` - 56 edges
2. `LoggedMealItemEntity` - 46 edges
3. `LoggedMealEntity` - 34 edges
4. `UserGoalEntity` - 24 edges
5. `MealWithItems` - 24 edges
6. `EssmichkiTheme()` - 23 edges
7. `FoodItemEntity` - 19 edges
8. `WeightLogEntity` - 19 edges
9. `ProgressViewModel` - 19 edges
10. `NutritionalGoalState` - 19 edges

## Surprising Connections (you probably didn't know these)
- `ProgressScreenPreview()` --calls--> `DailyCalorieStat`  [INFERRED]
  app/src/main/java/com/example/ess_mich_ki/ui/progress/ProgressScreen.kt → app/src/main/java/com/example/ess_mich_ki/ui/progress/ProgressUiState.kt
- `ProgressViewModel` --calls--> `DailyCalorieStat`  [INFERRED]
  app/src/main/java/com/example/ess_mich_ki/ui/progress/ProgressViewModel.kt → app/src/main/java/com/example/ess_mich_ki/ui/progress/ProgressUiState.kt
- `NutritionalGoalsDialog()` --calls--> `NutritionalGoalState`  [INFERRED]
  app/src/main/java/com/example/ess_mich_ki/ui/progress/components/NutritionalGoalsDialog.kt → app/src/main/java/com/example/ess_mich_ki/util/NutritionalGoalMath.kt
- `DashboardViewModelTest` --references--> `SettingsDataStore`  [EXTRACTED]
  app/src/test/java/com/example/ess_mich_ki/ui/DashboardViewModelTest.kt → app/src/main/java/com/example/ess_mich_ki/data/datastore/SettingsDataStore.kt
- `FakeLoggedMealDao` --implements--> `LoggedMealDao`  [EXTRACTED]
  app/src/test/java/com/example/ess_mich_ki/ui/DashboardViewModelTest.kt → app/src/main/java/com/example/ess_mich_ki/data/local/dao/LoggedMealDao.kt

## Import Cycles
- None detected.

## Communities (32 total, 6 thin omitted)

### Community 0 - "🥗 EssMichKI – Smarter KI-Mahlzeiten- & Nährstofftracker"
Cohesion: 0.10
Nodes (19): 1. Voraussetzungen, 2. OpenRouter API-Key einrichten, 🏗️ Architektur & Technologie-Stack, 📖 Bedienungsanleitung, 💾 Daten exportieren / sichern, 🥗 EssMichKI – Smarter KI-Mahlzeiten- & Nährstofftracker, 📈 Fortschritt & Gewichtsverlauf, 🛠️ Für Entwickler / Projekt bauen (+11 more)

### Community 1 - "FoodItemEntity"
Cohesion: 0.11
Nodes (10): FoodItemDao, Flow, AppDatabase, AppDatabaseCallback, Context, FoodItemEntity, SampleDataTest, Callback (+2 more)

### Community 2 - "LoggedMealEntity"
Cohesion: 0.12
Nodes (5): Flow, LoggedMealDao, LoggedMealEntity, MealWithItems, CsvExportImportUtilTest

### Community 3 - "ProgressViewModel"
Cohesion: 0.14
Nodes (6): Factory, StateFlow, T, ViewModel, ViewModelProvider, ProgressViewModel

### Community 4 - "EssmichkiTheme"
Cohesion: 0.09
Nodes (33): CalorieGaugeCard(), CalorieGaugeCardPreview(), Color, Modifier, MacroProgressRow(), HeaderBar(), HeaderBarPreview(), Modifier (+25 more)

### Community 5 - "NutritionalGoalState"
Cohesion: 0.14
Nodes (7): MacroType, CARBS, FAT, PROTEIN, NutritionalGoalMath, NutritionalGoalState, NutritionalGoalMathTest

### Community 6 - "DashboardViewModel"
Cohesion: 0.06
Nodes (12): android, MainActivity, DashboardViewModel, Factory, Context, StateFlow, T, ViewModel (+4 more)

### Community 7 - "FoodDetailViewModel"
Cohesion: 0.10
Nodes (20): EditLoggedMealItemDialog(), Modifier, SheetState, PureUnitOption, AccordionCard(), FoodDetailContent(), FoodDetailScreen(), FoodDetailScreenPreview() (+12 more)

### Community 8 - "DashboardViewModelTest"
Cohesion: 0.15
Nodes (4): DashboardViewModelTest, OpenRouterApiClient, FakeLoggedMealItemDao, OpenRouterApiClient

### Community 9 - "AiFoodLoggingDialog.kt"
Cohesion: 0.11
Nodes (20): Result, OpenRouterApiClient, OpenRouterChoice, OpenRouterError, OpenRouterMessage, OpenRouterRequest, OpenRouterResponse, OpenRouterResponseFormat (+12 more)

### Community 10 - "SettingsDataStore"
Cohesion: 0.13
Nodes (10): Flow, SettingsDataStore, UserSettings, Factory, StateFlow, T, ViewModel, ViewModelProvider (+2 more)

### Community 11 - "UserGoalEntity"
Cohesion: 0.20
Nodes (4): Flow, UserGoalDao, UserGoalEntity, FakeUserGoalDao

### Community 12 - "Implementation Steps"
Cohesion: 0.18
Nodes (10): Features, High-Level Tech Stack, Implementation Steps, Project Brief, Project Brief: ess-mich-ki (Food Tracker), Project Plan, Task_1_DataAndNetworkSetup: Implement Room database (entities & DAOs for meals, foods, weight, streaks), Jetpack DataStore for API key & OpenRouter base URL settings, and OpenRouter API networking client for natural language food parsing., Task_2_AILoggingAndDashboardUI: Build the Dashboard screen with calorie gauge, macro progress bars, meal sections, streak header, and AI natural language food logging interface integrated with OpenRouter API. (+2 more)

### Community 14 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 21 - "ProgressScreen.kt"
Cohesion: 0.25
Nodes (9): AddWeightDialog(), EditGoalDialog(), formatWeightDialogDate(), ManageWeightLogsDialog(), formatWeightDate(), Modifier, ProgressContent(), ProgressScreen() (+1 more)

### Community 22 - ".calculateMonthlyCalendar"
Cohesion: 0.13
Nodes (13): Color, Modifier, StreaksScreen(), StreaksScreenPreview(), StreakStatCard(), CalendarWeekInfo, MonthDayInfo, MonthlyCalendarData (+5 more)

### Community 23 - "LoggedMealItemEntity"
Cohesion: 0.26
Nodes (3): Flow, LoggedMealItemDao, LoggedMealItemEntity

### Community 24 - "WeightLogEntity"
Cohesion: 0.27
Nodes (3): Flow, WeightLogDao, WeightLogEntity

### Community 27 - ".fromString"
Cohesion: 0.22
Nodes (5): CanonicalMealType, BREAKFAST, DINNER, LUNCH, SNACK

### Community 29 - "AppNavigation.kt"
Cohesion: 0.40
Nodes (5): AppNavigation(), Modifier, NavTab, PLAN, PROGRESS

### Community 30 - "Task3ViewModelsTest.kt"
Cohesion: 0.47
Nodes (3): DailyCalorieStat, ProgressUiState, Task3ViewModelsTest

### Community 31 - "DashboardViewModelTest.kt"
Cohesion: 0.40
Nodes (3): FakeContext, Context, ContextWrapper

## Knowledge Gaps
- **33 isolated node(s):** `OpenRouterChoice`, `OpenRouterResponse`, `OpenRouterError`, `PLAN`, `PROGRESS` (+28 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **6 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `DashboardViewModel` connect `DashboardViewModel` to `LoggedMealEntity`, `EssmichkiTheme`, `DashboardViewModelTest`, `AiFoodLoggingDialog.kt`, `.calculateMonthlyCalendar`, `.fromString`, `AppNavigation.kt`, `DashboardViewModelTest.kt`?**
  _High betweenness centrality (0.214) - this node is a cross-community bridge._
- **Why does `LoggedMealItemEntity` connect `LoggedMealItemEntity` to `FoodItemEntity`, `LoggedMealEntity`, `EssmichkiTheme`, `DashboardViewModel`, `FoodDetailViewModel`, `DashboardViewModelTest`, `FakeLoggedMealDao`, `.fromString`, `DashboardViewModelTest.kt`?**
  _High betweenness centrality (0.135) - this node is a cross-community bridge._
- **Why does `EssmichkiTheme()` connect `EssmichkiTheme` to `DashboardViewModel`, `FoodDetailViewModel`, `AiFoodLoggingDialog.kt`, `ProgressScreen.kt`, `.calculateMonthlyCalendar`?**
  _High betweenness centrality (0.116) - this node is a cross-community bridge._
- **What connects `OpenRouterChoice`, `OpenRouterResponse`, `OpenRouterError` to the rest of the system?**
  _33 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `🥗 EssMichKI – Smarter KI-Mahlzeiten- & Nährstofftracker` be split into smaller, more focused modules?**
  _Cohesion score 0.1 - nodes in this community are weakly interconnected._
- **Should `FoodItemEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.10967741935483871 - nodes in this community are weakly interconnected._
- **Should `LoggedMealEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.11904761904761904 - nodes in this community are weakly interconnected._