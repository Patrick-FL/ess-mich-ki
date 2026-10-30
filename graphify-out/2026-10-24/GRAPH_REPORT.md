# Graph Report - food_tracker  (2026-10-23)

## Corpus Check
- 60 files · ~62,813 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 444 nodes · 888 edges · 22 communities (19 shown, 3 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 16 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b5560e9c`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- DashboardViewModelTest.kt
- FoodItemEntity
- LoggedMealEntity
- UserGoalEntity
- EssmichkiTheme
- NutritionalGoalState
- DashboardViewModel
- FoodDetailViewModel
- LoggedMealItemEntity
- OpenRouterApiClient
- SettingsDataStore
- EditLoggedMealItemDialog.kt
- Implementation Steps
- Converters
- gradlew
- ExampleInstrumentedTest
- ExampleUnitTest
- .calculateMonthlyCalendar

## God Nodes (most connected - your core abstractions)
1. `DashboardViewModel` - 47 edges
2. `LoggedMealItemEntity` - 41 edges
3. `LoggedMealEntity` - 29 edges
4. `UserGoalEntity` - 24 edges
5. `EssmichkiTheme()` - 23 edges
6. `FoodItemEntity` - 19 edges
7. `WeightLogEntity` - 19 edges
8. `ProgressViewModel` - 19 edges
9. `NutritionalGoalState` - 19 edges
10. `SettingsDataStore` - 18 edges

## Surprising Connections (you probably didn't know these)
- `NutritionalGoalsDialog()` --calls--> `NutritionalGoalState`  [INFERRED]
  app/src/main/java/com/example/ess_mich_ki/ui/progress/components/NutritionalGoalsDialog.kt → app/src/main/java/com/example/ess_mich_ki/util/NutritionalGoalMath.kt
- `DashboardViewModelTest` --references--> `SettingsDataStore`  [EXTRACTED]
  app/src/test/java/com/example/ess_mich_ki/ui/DashboardViewModelTest.kt → app/src/main/java/com/example/ess_mich_ki/data/datastore/SettingsDataStore.kt
- `DashboardViewModelTest` --references--> `AppDatabase`  [EXTRACTED]
  app/src/test/java/com/example/ess_mich_ki/ui/DashboardViewModelTest.kt → app/src/main/java/com/example/ess_mich_ki/data/local/database/AppDatabase.kt
- `FakeAppDatabase` --inherits--> `AppDatabase`  [EXTRACTED]
  app/src/test/java/com/example/ess_mich_ki/ui/DashboardViewModelTest.kt → app/src/main/java/com/example/ess_mich_ki/data/local/database/AppDatabase.kt
- `FoodDetailScreenPreview()` --calls--> `FoodItemEntity`  [EXTRACTED]
  app/src/main/java/com/example/ess_mich_ki/ui/detail/FoodDetailScreen.kt → app/src/main/java/com/example/ess_mich_ki/data/local/entity/FoodItemEntity.kt

## Import Cycles
- None detected.

## Communities (22 total, 3 thin omitted)

### Community 0 - "DashboardViewModelTest.kt"
Cohesion: 0.11
Nodes (8): DashboardViewModelTest, OpenRouterApiClient, FakeAppDatabase, FakeContext, Context, ContextWrapper, InvalidationTracker, OpenRouterApiClient

### Community 1 - "FoodItemEntity"
Cohesion: 0.11
Nodes (10): FoodItemDao, Flow, AppDatabase, AppDatabaseCallback, Context, FoodItemEntity, SampleDataTest, Callback (+2 more)

### Community 2 - "LoggedMealEntity"
Cohesion: 0.10
Nodes (8): Flow, LoggedMealDao, LoggedMealEntity, MealWithItems, DashboardUiState, DashboardStateTest, FakeLoggedMealDao, Flow

### Community 3 - "UserGoalEntity"
Cohesion: 0.05
Nodes (25): Flow, UserGoalDao, Flow, WeightLogDao, UserGoalEntity, WeightLogEntity, AddWeightDialog(), EditGoalDialog() (+17 more)

### Community 4 - "EssmichkiTheme"
Cohesion: 0.12
Nodes (25): CalorieGaugeCard(), CalorieGaugeCardPreview(), Color, Modifier, MacroProgressRow(), HeaderBar(), HeaderBarPreview(), Modifier (+17 more)

### Community 5 - "NutritionalGoalState"
Cohesion: 0.14
Nodes (7): MacroType, CARBS, FAT, PROTEIN, NutritionalGoalMath, NutritionalGoalState, NutritionalGoalMathTest

### Community 6 - "DashboardViewModel"
Cohesion: 0.07
Nodes (14): MainActivity, DashboardViewModel, Factory, StateFlow, T, ViewModel, ViewModelProvider, AppNavigation() (+6 more)

### Community 7 - "FoodDetailViewModel"
Cohesion: 0.12
Nodes (16): AccordionCard(), FoodDetailContent(), FoodDetailScreen(), FoodDetailScreenPreview(), Color, Modifier, MacroStatCard(), NutritionRow() (+8 more)

### Community 8 - "LoggedMealItemEntity"
Cohesion: 0.13
Nodes (9): Flow, LoggedMealItemDao, LoggedMealItemEntity, FoodItemRow(), getEmojiForFood(), Modifier, MealCard(), MealCardPreview() (+1 more)

### Community 9 - "OpenRouterApiClient"
Cohesion: 0.11
Nodes (20): Result, OpenRouterApiClient, OpenRouterChoice, OpenRouterError, OpenRouterMessage, OpenRouterRequest, OpenRouterResponse, OpenRouterResponseFormat (+12 more)

### Community 10 - "SettingsDataStore"
Cohesion: 0.13
Nodes (10): Flow, SettingsDataStore, UserSettings, Factory, StateFlow, T, ViewModel, ViewModelProvider (+2 more)

### Community 11 - "EditLoggedMealItemDialog.kt"
Cohesion: 0.24
Nodes (5): EditLoggedMealItemDialog(), Modifier, SheetState, PureUnitOption, ServingUnitUtils

### Community 12 - "Implementation Steps"
Cohesion: 0.18
Nodes (10): Features, High-Level Tech Stack, Implementation Steps, Project Brief, Project Brief: ess-mich-ki (Food Tracker), Project Plan, Task_1_DataAndNetworkSetup: Implement Room database (entities & DAOs for meals, foods, weight, streaks), Jetpack DataStore for API key & OpenRouter base URL settings, and OpenRouter API networking client for natural language food parsing., Task_2_AILoggingAndDashboardUI: Build the Dashboard screen with calorie gauge, macro progress bars, meal sections, streak header, and AI natural language food logging interface integrated with OpenRouter API. (+2 more)

### Community 14 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 22 - ".calculateMonthlyCalendar"
Cohesion: 0.13
Nodes (13): Color, Modifier, StreaksScreen(), StreaksScreenPreview(), StreakStatCard(), CalendarWeekInfo, MonthDayInfo, MonthlyCalendarData (+5 more)

## Knowledge Gaps
- **15 isolated node(s):** `OpenRouterChoice`, `OpenRouterResponse`, `OpenRouterError`, `PLAN`, `PROGRESS` (+10 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `DashboardViewModel` connect `DashboardViewModel` to `DashboardViewModelTest.kt`, `LoggedMealEntity`, `EssmichkiTheme`, `LoggedMealItemEntity`, `OpenRouterApiClient`, `EditLoggedMealItemDialog.kt`, `.calculateMonthlyCalendar`?**
  _High betweenness centrality (0.203) - this node is a cross-community bridge._
- **Why does `LoggedMealItemEntity` connect `LoggedMealItemEntity` to `DashboardViewModelTest.kt`, `FoodItemEntity`, `LoggedMealEntity`, `EssmichkiTheme`, `DashboardViewModel`, `FoodDetailViewModel`, `EditLoggedMealItemDialog.kt`?**
  _High betweenness centrality (0.157) - this node is a cross-community bridge._
- **Why does `EssmichkiTheme()` connect `EssmichkiTheme` to `UserGoalEntity`, `DashboardViewModel`, `FoodDetailViewModel`, `LoggedMealItemEntity`, `OpenRouterApiClient`, `.calculateMonthlyCalendar`?**
  _High betweenness centrality (0.149) - this node is a cross-community bridge._
- **What connects `OpenRouterChoice`, `OpenRouterResponse`, `OpenRouterError` to the rest of the system?**
  _15 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `DashboardViewModelTest.kt` be split into smaller, more focused modules?**
  _Cohesion score 0.10869565217391304 - nodes in this community are weakly interconnected._
- **Should `FoodItemEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.10967741935483871 - nodes in this community are weakly interconnected._
- **Should `LoggedMealEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.09743589743589744 - nodes in this community are weakly interconnected._