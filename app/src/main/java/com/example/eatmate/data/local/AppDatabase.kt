package com.example.eatmate.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.eatmate.data.local.dao.ChatMessageDao
import com.example.eatmate.data.local.dao.MealDao
import com.example.eatmate.data.local.dao.UserGoalDao
import com.example.eatmate.data.local.entity.ChatMessageEntity
import com.example.eatmate.data.local.entity.MealEntity
import com.example.eatmate.data.local.entity.UserGoalEntity

@Database(
    entities = [MealEntity::class, UserGoalEntity::class, ChatMessageEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun userGoalDao(): UserGoalDao
    abstract fun chatMessageDao(): ChatMessageDao
}
