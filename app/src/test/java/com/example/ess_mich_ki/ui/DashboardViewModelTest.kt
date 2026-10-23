package com.example.ess_mich_ki.ui

import android.content.Context
import android.content.ContextWrapper
import androidx.room.InvalidationTracker
import com.example.ess_mich_ki.data.datastore.SettingsDataStore
import com.example.ess_mich_ki.data.local.dao.FoodItemDao
import com.example.ess_mich_ki.data.local.dao.LoggedMealDao
import com.example.ess_mich_ki.data.local.dao.LoggedMealItemDao
import com.example.ess_mich_ki.data.local.dao.UserGoalDao
import com.example.ess_mich_ki.data.local.dao.WeightLogDao
import com.example.ess_mich_ki.data.local.database.AppDatabase
import com.example.ess_mich_ki.data.local.entity.LoggedMealEntity
import com.example.ess_mich_ki.data.local.entity.LoggedMealItemEntity
import com.example.ess_mich_ki.data.local.entity.UserGoalEntity
import com.example.ess_mich_ki.data.local.model.MealWithItems
import com.example.ess_mich_ki.data.network.openrouter.OpenRouterApiClient
import com.example.ess_mich_ki.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeMealDao: FakeLoggedMealDao
    private lateinit var fakeItemDao: FakeLoggedMealItemDao
    private lateinit var fakeUserGoalDao: FakeUserGoalDao
    private lateinit var fakeDatabase: AppDatabase
    private lateinit var settingsDataStore: SettingsDataStore
    private lateinit var openRouterApiClient: OpenRouterApiClient

    @Before
    fun setUp() = runTest {
        Dispatchers.setMain(testDispatcher)
        fakeMealDao = FakeLoggedMealDao()
        fakeItemDao = FakeLoggedMealItemDao(fakeMealDao)
        fakeUserGoalDao = FakeUserGoalDao()
        fakeDatabase = FakeAppDatabase(fakeMealDao, fakeItemDao, fakeUserGoalDao)

        val fakeContext = FakeContext()
        settingsDataStore = SettingsDataStore(fakeContext)
        settingsDataStore.updateOpenRouterApiKey("test-api-key")
        openRouterApiClient = OpenRouterApiClient(okHttpClient = OkHttpClient())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialSeedingOnToday() = runTest {
        val viewModel = DashboardViewModel(fakeDatabase, settingsDataStore, openRouterApiClient)
        advanceUntilIdle()

        val state = viewModel.uiState.first { it.meals.isNotEmpty() }
        val today = LocalDate.now()

        assertEquals(today, state.selectedDate)
        assertEquals(4, state.meals.size)
        // 108 + 120 + 52 + 248 + 92 = 620 kcal
        assertEquals(620, state.calorieConsumed)
    }

    @Test
    fun testSwitchingDatesDoesNotDuplicateMealsOrDoubleCalories() = runTest {
        val viewModel = DashboardViewModel(fakeDatabase, settingsDataStore, openRouterApiClient)
        advanceUntilIdle()

        val initialState = viewModel.uiState.first { it.meals.isNotEmpty() }
        val initialCalories = initialState.calorieConsumed
        assertEquals(620, initialCalories)

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val tomorrow = today.plusDays(1)

        // Switch to yesterday
        viewModel.selectDate(yesterday)
        advanceUntilIdle()

        val yesterdayState = viewModel.uiState.first { it.selectedDate == yesterday }
        assertEquals(yesterday, yesterdayState.selectedDate)
        assertEquals(0, yesterdayState.calorieConsumed)

        // Switch back to today
        viewModel.selectDate(today)
        advanceUntilIdle()

        val backToTodayState = viewModel.uiState.first { it.selectedDate == today && it.meals.isNotEmpty() }
        assertEquals(today, backToTodayState.selectedDate)
        assertEquals(620, backToTodayState.calorieConsumed)
        assertEquals(4, backToTodayState.meals.size)

        // Switch to tomorrow and back to today multiple times
        viewModel.selectDate(tomorrow)
        advanceUntilIdle()
        viewModel.selectDate(today)
        advanceUntilIdle()

        val finalTodayState = viewModel.uiState.first { it.selectedDate == today && it.meals.isNotEmpty() }
        assertEquals(620, finalTodayState.calorieConsumed)
        assertEquals(4, finalTodayState.meals.size)
    }

    @Test
    fun testDeduplicationHandlesExistingDuplicateMealEntities() = runTest {
        val today = LocalDate.now()
        val dateString = today.toString()

        // Manually insert duplicate "Breakfast" meals to simulate a corrupted database state
        val meal1Id = fakeMealDao.insertMeal(
            LoggedMealEntity(id = 1, dateString = dateString, mealType = "Breakfast", totalCalories = 100.0)
        )
        fakeItemDao.insertItem(
            LoggedMealItemEntity(id = 1, mealId = meal1Id, name = "Apple", calories = 100.0, isChecked = true)
        )

        val meal2Id = fakeMealDao.insertMeal(
            LoggedMealEntity(id = 2, dateString = dateString, mealType = "Breakfast", totalCalories = 100.0)
        )
        fakeItemDao.insertItem(
            LoggedMealItemEntity(id = 2, mealId = meal2Id, name = "Apple Duplicate", calories = 100.0, isChecked = true)
        )

        val viewModel = DashboardViewModel(fakeDatabase, settingsDataStore, openRouterApiClient)
        advanceUntilIdle()

        val state = viewModel.uiState.first { it.meals.isNotEmpty() }
        // Distinct meals logic should pick only one Breakfast meal
        assertEquals(1, state.meals.size)
        assertEquals(100, state.calorieConsumed)
    }

    @Test
    fun testUpdateLoggedMealItemScalesMacrosAndRecalculatesTotals() = runTest {
        val viewModel = DashboardViewModel(fakeDatabase, settingsDataStore, openRouterApiClient)
        advanceUntilIdle()

        val initialState = viewModel.uiState.first { it.meals.isNotEmpty() }
        val breakfast = initialState.meals.find { it.meal.mealType.equals("Breakfast", true) }!!
        val soyYogurtItem = breakfast.items.find { it.name.contains("Soy Yogurt", true) }!!

        // Initial values: quantity 1.0, 200g serving, 108 kcal, 8g protein, 4.2g carbs, 4.6g fat
        assertEquals(1.0, soyYogurtItem.quantity, 0.01)
        assertEquals(108.0, soyYogurtItem.calories, 0.01)

        // Change quantity to 1.5 and serving to 200g -> total grams: 300g (1.5x scale)
        viewModel.updateLoggedMealItem(
            item = soyYogurtItem,
            newQuantity = 1.5,
            newServingName = soyYogurtItem.servingName,
            newServingGrams = soyYogurtItem.servingGrams
        )
        advanceUntilIdle()

        val updatedItem = fakeItemDao.getItemById(soyYogurtItem.id)!!
        assertEquals(1.5, updatedItem.quantity, 0.01)
        assertEquals(162.0, updatedItem.calories, 0.01) // 108 * 1.5 = 162
        assertEquals(12.0, updatedItem.protein, 0.01)  // 8 * 1.5 = 12

        // Verify total calories for today increased by 54 kcal (from 620 to 674)
        val updatedState = viewModel.uiState.first { it.calorieConsumed == 674 }
        assertEquals(674, updatedState.calorieConsumed)
    }

    @Test
    fun testUnitSwitchingPreservesQuantityNumberAndRecalculatesMacros() = runTest {
        val viewModel = DashboardViewModel(fakeDatabase, settingsDataStore, openRouterApiClient)
        advanceUntilIdle()

        val initialState = viewModel.uiState.first { it.meals.isNotEmpty() }
        val breakfast = initialState.meals.find { it.meal.mealType.equals("Breakfast", true) }!!

        // Insert a strawberry item: 7 pieces (~100g total), 32 kcal
        val mealId = breakfast.meal.id
        val strawberryItem = LoggedMealItemEntity(
            mealId = mealId,
            name = "Strawberries",
            quantity = 7.0,
            servingName = "piece",
            servingGrams = 14.2857, // 7 * 14.2857 = 100g
            calories = 32.0,
            protein = 0.7,
            carbs = 7.7,
            fat = 0.3,
            isChecked = true
        )
        val itemId = fakeItemDao.insertItem(strawberryItem)

        // Switch unit from "piece" to "g" while keeping quantity = 7.0
        // New total grams = 7.0 * 1.0 = 7g
        // Scale factor = 7g / 100g = 0.07
        viewModel.updateLoggedMealItem(
            item = strawberryItem.copy(id = itemId),
            newQuantity = 7.0,
            newServingName = "g",
            newServingGrams = 1.0
        )
        advanceUntilIdle()

        val updatedItem = fakeItemDao.getItemById(itemId)!!
        assertEquals(7.0, updatedItem.quantity, 0.01)
        assertEquals("g", updatedItem.servingName)
        assertEquals(1.0, updatedItem.servingGrams, 0.01)
        assertEquals(2.24, updatedItem.calories, 0.01) // 32.0 * 0.07 = 2.24
    }

    @Test
    fun testUpdateParsedItemInAiLogger() = runTest {
        val fakeApiClient = object : OpenRouterApiClient() {
            override suspend fun parseFoodText(
                query: String,
                apiKey: String,
                baseUrl: String,
                model: String
            ): Result<com.example.ess_mich_ki.data.network.openrouter.ParsedMealAiResult> {
                return Result.success(
                    com.example.ess_mich_ki.data.network.openrouter.ParsedMealAiResult(
                        items = listOf(
                            com.example.ess_mich_ki.data.network.openrouter.ParsedFoodItem(
                                name = "Oatmeal",
                                quantity = 1.0,
                                servingName = "1 Cup (100g)",
                                servingGrams = 100.0,
                                calories = 350.0,
                                proteinGrams = 12.0,
                                carbsGrams = 60.0,
                                fatGrams = 6.0
                            )
                        ),
                        totalCalories = 350.0,
                        totalProtein = 12.0,
                        totalCarbs = 60.0,
                        totalFat = 6.0
                    )
                )
            }
        }

        val viewModel = DashboardViewModel(fakeDatabase, settingsDataStore, fakeApiClient)
        advanceUntilIdle()

        // Wait for settingsDataStore flow to update uiState.apiKey
        val stateWithKey = viewModel.uiState.first { it.apiKey.isNotBlank() }
        assertEquals("test-api-key", stateWithKey.apiKey)

        viewModel.openAiDialog("Breakfast")
        viewModel.updateAiInputText("1 Cup Oatmeal")

        // Trigger AI analysis
        viewModel.analyzeFoodText()
        advanceUntilIdle()

        val parsedBefore = viewModel.uiState.value.aiParsedResult
        assertEquals(1, parsedBefore?.items?.size)
        assertEquals(350.0, parsedBefore?.totalCalories ?: 0.0, 0.01)

        // Update parsed item quantity to 2.0
        val updatedParsedItem = parsedBefore!!.items[0].copy(
            quantity = 2.0,
            calories = 700.0,
            proteinGrams = 24.0,
            carbsGrams = 120.0,
            fatGrams = 12.0
        )
        viewModel.updateParsedItem(0, updatedParsedItem)

        // Verify state holds updated total calories via first subscriber
        val updatedState = viewModel.uiState.first { it.aiParsedResult?.totalCalories == 700.0 }
        val parsedAfter = updatedState.aiParsedResult
        assertEquals(1, parsedAfter?.items?.size)
        assertEquals(700.0, parsedAfter?.totalCalories ?: 0.0, 0.01)
    }

    private class FakeContext : ContextWrapper(null) {
        private val tempDir: File = Files.createTempDirectory("test_datastore").toFile().apply { deleteOnExit() }
        override fun getFilesDir(): File = tempDir
        override fun getApplicationContext(): Context = this
    }

    // --- Fakes ---

    private class FakeAppDatabase(
        private val mealDao: LoggedMealDao,
        private val itemDao: LoggedMealItemDao,
        private val goalDao: UserGoalDao
    ) : AppDatabase() {
        override fun foodItemDao(): FoodItemDao = throw NotImplementedError()
        override fun loggedMealDao(): LoggedMealDao = mealDao
        override fun loggedMealItemDao(): LoggedMealItemDao = itemDao
        override fun weightLogDao(): WeightLogDao = throw NotImplementedError()
        override fun userGoalDao(): UserGoalDao = goalDao
        override fun clearAllTables() {}
        override fun createInvalidationTracker(): InvalidationTracker {
            throw NotImplementedError()
        }
    }

    private class FakeLoggedMealDao : LoggedMealDao {
        val meals = mutableListOf<LoggedMealEntity>()
        val items = mutableListOf<LoggedMealItemEntity>()
        val mealsFlow = MutableStateFlow<List<LoggedMealEntity>>(emptyList())
        private var nextMealId = 1L

        fun notifyChanged() {
            mealsFlow.value = meals.map { it.copy() }
        }

        override fun getMealsByDate(dateString: String): Flow<List<LoggedMealEntity>> {
            return mealsFlow.map { list -> list.filter { it.dateString == dateString } }
        }

        override suspend fun getMealsByDateSync(dateString: String): List<LoggedMealEntity> {
            return meals.filter { it.dateString == dateString }
        }

        override fun getMealsWithItemsByDate(dateString: String): Flow<List<MealWithItems>> {
            return mealsFlow.map { list ->
                list.filter { it.dateString == dateString }
                    .map { meal ->
                        MealWithItems(meal = meal, items = items.filter { it.mealId == meal.id })
                    }
            }
        }

        override suspend fun getMealWithItemsByDateAndType(dateString: String, mealType: String): MealWithItems? {
            val meal = meals.find { it.dateString == dateString && it.mealType.equals(mealType, ignoreCase = true) }
                ?: return null
            return MealWithItems(meal = meal, items = items.filter { it.mealId == meal.id })
        }

        override fun getAllMeals(): Flow<List<LoggedMealEntity>> = mealsFlow

        override suspend fun getMealById(id: Long): LoggedMealEntity? = meals.find { it.id == id }

        override suspend fun getMealWithItemsById(id: Long): MealWithItems? {
            val meal = meals.find { it.id == id } ?: return null
            return MealWithItems(meal = meal, items = items.filter { it.mealId == id })
        }

        override suspend fun insertMeal(meal: LoggedMealEntity): Long {
            val id = if (meal.id == 0L) nextMealId++ else meal.id
            val newMeal = meal.copy(id = id)
            meals.removeAll { it.id == id }
            meals.add(newMeal)
            notifyChanged()
            return id
        }

        override suspend fun updateMeal(meal: LoggedMealEntity) {
            val index = meals.indexOfFirst { it.id == meal.id }
            if (index >= 0) {
                meals[index] = meal
                notifyChanged()
            }
        }

        override suspend fun deleteMeal(meal: LoggedMealEntity) {
            meals.removeAll { it.id == meal.id }
            items.removeAll { it.mealId == meal.id }
            notifyChanged()
        }

        override suspend fun deleteMealById(id: Long) {
            meals.removeAll { it.id == id }
            items.removeAll { it.mealId == id }
            notifyChanged()
        }
    }

    private class FakeLoggedMealItemDao(private val fakeMealDao: FakeLoggedMealDao) : LoggedMealItemDao {
        private var nextItemId = 1L

        override fun getItemsForMeal(mealId: Long): Flow<List<LoggedMealItemEntity>> {
            return fakeMealDao.mealsFlow.map { fakeMealDao.items.filter { it.mealId == mealId } }
        }

        override suspend fun getItemsForMealSync(mealId: Long): List<LoggedMealItemEntity> {
            return fakeMealDao.items.filter { it.mealId == mealId }
        }

        override suspend fun getItemById(id: Long): LoggedMealItemEntity? {
            return fakeMealDao.items.find { it.id == id }
        }

        override suspend fun insertItem(item: LoggedMealItemEntity): Long {
            val id = if (item.id == 0L) nextItemId++ else item.id
            val newItem = item.copy(id = id)
            fakeMealDao.items.removeAll { it.id == id }
            fakeMealDao.items.add(newItem)
            fakeMealDao.notifyChanged()
            return id
        }

        override suspend fun insertItems(items: List<LoggedMealItemEntity>) {
            items.forEach { insertItem(it) }
        }

        override suspend fun updateItem(item: LoggedMealItemEntity) {
            val index = fakeMealDao.items.indexOfFirst { it.id == item.id }
            if (index >= 0) {
                fakeMealDao.items[index] = item
                fakeMealDao.notifyChanged()
            }
        }

        override suspend fun deleteItem(item: LoggedMealItemEntity) {
            fakeMealDao.items.removeAll { it.id == item.id }
            fakeMealDao.notifyChanged()
        }

        override suspend fun deleteItemsForMeal(mealId: Long) {
            fakeMealDao.items.removeAll { it.mealId == mealId }
            fakeMealDao.notifyChanged()
        }
    }

    private class FakeUserGoalDao : UserGoalDao {
        private val goalFlow = MutableStateFlow<UserGoalEntity?>(null)

        override fun getUserGoal(): Flow<UserGoalEntity?> = goalFlow

        override suspend fun getUserGoalSync(): UserGoalEntity? = goalFlow.value

        override suspend fun insertOrUpdateUserGoal(userGoal: UserGoalEntity) {
            goalFlow.value = userGoal
        }

        override suspend fun deleteUserGoal(userGoal: UserGoalEntity) {
            goalFlow.value = null
        }
    }
}
