package com.example.eatmate.domain.repository

import com.example.eatmate.domain.model.Meal
import com.example.eatmate.domain.model.NutritionInfo
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    suspend fun saveMeal(meal: Meal): Long
    suspend fun getMealsByDate(date: String): List<Meal>
    fun observeMealsByDate(date: String): Flow<List<Meal>>
    suspend fun getRecentMeals(limit: Int = 5): List<Meal>
    suspend fun getDailyNutrition(date: String): NutritionInfo
    suspend fun deleteMeal(id: Long)
}
