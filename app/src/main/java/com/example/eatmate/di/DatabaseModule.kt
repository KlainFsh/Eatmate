package com.example.eatmate.di

import android.content.Context
import androidx.room.Room
import com.example.eatmate.data.local.AppDatabase
import com.example.eatmate.data.local.dao.ChatMessageDao
import com.example.eatmate.data.local.dao.MealDao
import com.example.eatmate.data.local.dao.UserGoalDao
import com.example.eatmate.data.local.EnenProfileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "eatmate.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMealDao(database: AppDatabase): MealDao {
        return database.mealDao()
    }

    @Provides
    fun provideUserGoalDao(database: AppDatabase): UserGoalDao {
        return database.userGoalDao()
    }

    @Provides
    fun provideChatMessageDao(database: AppDatabase): ChatMessageDao {
        return database.chatMessageDao()
    }

    @Provides
    @Singleton
    fun provideEnenProfileManager(@ApplicationContext context: Context): EnenProfileManager {
        return EnenProfileManager(context)
    }
}
