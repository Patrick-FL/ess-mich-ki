package com.example.ess_mich_ki.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ess_mich_ki.data.local.dao.FoodItemDao
import com.example.ess_mich_ki.data.local.dao.LoggedMealDao
import com.example.ess_mich_ki.data.local.dao.LoggedMealItemDao
import com.example.ess_mich_ki.data.local.dao.UserGoalDao
import com.example.ess_mich_ki.data.local.dao.WeightLogDao
import com.example.ess_mich_ki.data.local.entity.FoodItemEntity
import com.example.ess_mich_ki.data.local.entity.LoggedMealEntity
import com.example.ess_mich_ki.data.local.entity.LoggedMealItemEntity
import com.example.ess_mich_ki.data.local.entity.UserGoalEntity
import com.example.ess_mich_ki.data.local.entity.WeightLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        FoodItemEntity::class,
        LoggedMealEntity::class,
        LoggedMealItemEntity::class,
        WeightLogEntity::class,
        UserGoalEntity::class,
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun foodItemDao(): FoodItemDao
    abstract fun loggedMealDao(): LoggedMealDao
    abstract fun loggedMealItemDao(): LoggedMealItemDao
    abstract fun weightLogDao(): WeightLogDao
    abstract fun userGoalDao(): UserGoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "food_tracker_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(AppDatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getSampleFoods(): List<FoodItemEntity> {
            return listOf(
                FoodItemEntity(
                    id = 1,
                    name = "Soy Yogurt",
                    category = "Dairy & Alternatives",
                    calories = 54.0,
                    proteinGrams = 4.0,
                    carbsGrams = 2.1,
                    fatGrams = 2.3,
                    sugarsGrams = 0.5,
                    fiberGrams = 1.0,
                    saltGrams = 0.1,
                    satFatGrams = 0.4,
                    servingName = "1 Cup (200g)",
                    servingGrams = 200.0,
                    isFavorite = true,
                    isVerified = true
                ),
                FoodItemEntity(
                    id = 2,
                    name = "Chocolate Without Sugar",
                    category = "Sweets & Snacks",
                    calories = 480.0,
                    proteinGrams = 7.0,
                    carbsGrams = 30.0,
                    fatGrams = 38.0,
                    sugarsGrams = 0.5,
                    fiberGrams = 10.0,
                    saltGrams = 0.05,
                    satFatGrams = 22.0,
                    servingName = "1 Bar (100g)",
                    servingGrams = 100.0,
                    isFavorite = true,
                    isVerified = true
                ),
                FoodItemEntity(
                    id = 3,
                    name = "Himbeere Raspberry",
                    category = "Fruits",
                    calories = 52.0,
                    proteinGrams = 1.2,
                    carbsGrams = 12.0,
                    fatGrams = 0.6,
                    sugarsGrams = 4.4,
                    fiberGrams = 6.5,
                    saltGrams = 0.0,
                    satFatGrams = 0.0,
                    servingName = "1 Portion (100g)",
                    servingGrams = 100.0,
                    isFavorite = true,
                    isVerified = true
                ),
                FoodItemEntity(
                    id = 4,
                    name = "Oat Milk",
                    category = "Dairy & Alternatives",
                    calories = 46.0,
                    proteinGrams = 1.0,
                    carbsGrams = 6.5,
                    fatGrams = 1.5,
                    sugarsGrams = 3.0,
                    fiberGrams = 0.8,
                    saltGrams = 0.1,
                    satFatGrams = 0.2,
                    servingName = "1 Glass (200ml)",
                    servingGrams = 200.0,
                    isFavorite = false,
                    isVerified = true
                ),
                FoodItemEntity(
                    id = 5,
                    name = "Chicken Breast",
                    category = "Meat & Fish",
                    calories = 165.0,
                    proteinGrams = 31.0,
                    carbsGrams = 0.0,
                    fatGrams = 3.6,
                    sugarsGrams = 0.0,
                    fiberGrams = 0.0,
                    saltGrams = 0.15,
                    satFatGrams = 1.0,
                    servingName = "1 Piece (150g)",
                    servingGrams = 150.0,
                    isFavorite = false,
                    isVerified = true
                ),
                FoodItemEntity(
                    id = 6,
                    name = "Banana",
                    category = "Fruits",
                    calories = 89.0,
                    proteinGrams = 1.1,
                    carbsGrams = 22.8,
                    fatGrams = 0.3,
                    sugarsGrams = 12.2,
                    fiberGrams = 2.6,
                    saltGrams = 0.0,
                    satFatGrams = 0.1,
                    servingName = "1 Medium (118g)",
                    servingGrams = 118.0,
                    isFavorite = false,
                    isVerified = true
                ),
                FoodItemEntity(
                    id = 7,
                    name = "Whole Grain Bread",
                    category = "Bakery",
                    calories = 213.0,
                    proteinGrams = 8.5,
                    carbsGrams = 38.0,
                    fatGrams = 1.8,
                    sugarsGrams = 2.0,
                    fiberGrams = 7.0,
                    saltGrams = 1.1,
                    satFatGrams = 0.3,
                    servingName = "1 Slice (50g)",
                    servingGrams = 50.0,
                    isFavorite = false,
                    isVerified = true
                ),
                FoodItemEntity(
                    id = 8,
                    name = "Almonds",
                    category = "Nuts & Seeds",
                    calories = 579.0,
                    proteinGrams = 21.0,
                    carbsGrams = 22.0,
                    fatGrams = 50.0,
                    sugarsGrams = 4.4,
                    fiberGrams = 12.5,
                    saltGrams = 0.0,
                    satFatGrams = 3.8,
                    servingName = "1 Handful (30g)",
                    servingGrams = 30.0,
                    isFavorite = false,
                    isVerified = true
                )
            )
        }
    }

    private class AppDatabaseCallback(
        private val context: Context
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val database = getDatabase(context)
                database.foodItemDao().insertFoods(getSampleFoods())
            }
        }
    }
}
