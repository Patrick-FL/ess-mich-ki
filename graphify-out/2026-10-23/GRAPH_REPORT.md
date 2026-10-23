# Graph Report - food_tracker  (2026-10-23)

## Corpus Check
- 59 files · ~62,411 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 440 nodes · 881 edges · 21 communities (18 shown, 3 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 16 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b5560e9c`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- UserGoalEntity
- FoodItemEntity
- LoggedMealItemEntity
- WeightLogEntity
- EssmichkiTheme
- NutritionalGoalState
- DashboardViewModel
- FoodDetailViewModel
- OpenRouterApiClient
- SettingsDataStore
- AppNavigation.kt
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
7. `NutritionalGoalState` - 19 edges
8. `SettingsDataStore` - 18 edges
9. `FoodDetailViewModel` - 18 edges
10. `DashboardViewModelTest` - 18 edges

## Surprising Connections (you probably didn't know these)
- `NutritionalGoalsDialog()` --calls--> `NutritionalGoalState`  [INFERRED]
  app/src/main/java/com/example/ess_mich_ki/ui/progress/components/NutritionalGoalsDialog.kt → app/src/main/java/com/example/ess_mich_ki/util/NutritionalGoalMath.kt
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

### Community 0 - "UserGoalEntity"
Cohesion: 0.10
Nodes (9): Flow, UserGoalDao, UserGoalEntity, FakeAppDatabase, FakeContext, FakeUserGoalDao, Context, ContextWrapper (+1 more)

### Community 1 - "FoodItemEntity"
Cohesion: 0.11
Nodes (10): FoodItemDao, Flow, AppDatabase, AppDatabaseCallback, Context, FoodItemEntity, SampleDataTest, Callback (+2 more)

### Community 2 - "LoggedMealItemEntity"
Cohesion: 0.06
Nodes (17): Flow, LoggedMealDao, Flow, LoggedMealItemDao, LoggedMealEntity, LoggedMealItemEntity, MealWithItems, FoodItemRow() (+9 more)

### Community 3 - "WeightLogEntity"
Cohesion: 0.07
Nodes (20): Flow, WeightLogDao, WeightLogEntity, AddWeightDialog(), EditGoalDialog(), formatWeightDate(), formatWeightDateLong(), Modifier (+12 more)

### Community 4 - "EssmichkiTheme"
Cohesion: 0.12
Nodes (25): CalorieGaugeCard(), CalorieGaugeCardPreview(), Color, Modifier, MacroProgressRow(), HeaderBar(), HeaderBarPreview(), Modifier (+17 more)

### Community 5 - "NutritionalGoalState"
Cohesion: 0.14
Nodes (7): MacroType, CARBS, FAT, PROTEIN, NutritionalGoalMath, NutritionalGoalState, NutritionalGoalMathTest

### Community 6 - "DashboardViewModel"
Cohesion: 0.06
Nodes (10): DashboardViewModel, Factory, StateFlow, T, ViewModel, ViewModelProvider, ServingUnitUtils, DashboardViewModelTest (+2 more)

### Community 7 - "FoodDetailViewModel"
Cohesion: 0.10
Nodes (20): EditLoggedMealItemDialog(), Modifier, SheetState, PureUnitOption, AccordionCard(), FoodDetailContent(), FoodDetailScreen(), FoodDetailScreenPreview() (+12 more)

### Community 9 - "OpenRouterApiClient"
Cohesion: 0.11
Nodes (20): Result, OpenRouterApiClient, OpenRouterChoice, OpenRouterError, OpenRouterMessage, OpenRouterRequest, OpenRouterResponse, OpenRouterResponseFormat (+12 more)

### Community 10 - "SettingsDataStore"
Cohesion: 0.13
Nodes (10): Flow, SettingsDataStore, UserSettings, Factory, StateFlow, T, ViewModel, ViewModelProvider (+2 more)

### Community 11 - "AppNavigation.kt"
Cohesion: 0.25
Nodes (8): MainActivity, AppNavigation(), Modifier, NavTab, PLAN, PROGRESS, Bundle, ComponentActivity

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

- **Why does `DashboardViewModel` connect `DashboardViewModel` to `UserGoalEntity`, `LoggedMealItemEntity`, `EssmichkiTheme`, `OpenRouterApiClient`, `AppNavigation.kt`, `.calculateMonthlyCalendar`?**
  _High betweenness centrality (0.204) - this node is a cross-community bridge._
- **Why does `LoggedMealItemEntity` connect `LoggedMealItemEntity` to `UserGoalEntity`, `FoodItemEntity`, `EssmichkiTheme`, `DashboardViewModel`, `FoodDetailViewModel`?**
  _High betweenness centrality (0.158) - this node is a cross-community bridge._
- **Why does `EssmichkiTheme()` connect `EssmichkiTheme` to `LoggedMealItemEntity`, `WeightLogEntity`, `FoodDetailViewModel`, `OpenRouterApiClient`, `AppNavigation.kt`, `.calculateMonthlyCalendar`?**
  _High betweenness centrality (0.149) - this node is a cross-community bridge._
- **What connects `OpenRouterChoice`, `OpenRouterResponse`, `OpenRouterError` to the rest of the system?**
  _15 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `UserGoalEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.09885057471264368 - nodes in this community are weakly interconnected._
- **Should `FoodItemEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.10967741935483871 - nodes in this community are weakly interconnected._
- **Should `LoggedMealItemEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.060455486542443065 - nodes in this community are weakly interconnected._