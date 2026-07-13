package com.example.eatmate.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.eatmate.data.local.dao.MealDao
import com.example.eatmate.data.local.entity.MealEntity

@Database(
    entities = [MealEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
}
