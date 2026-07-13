package com.example.eatmate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.eatmate.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Insert
    suspend fun insert(meal: MealEntity): Long

    @Query("SELECT * FROM meal_records WHERE date = :date ORDER BY timestamp DESC")
    suspend fun getMealsByDate(date: String): List<MealEntity>

    @Query("SELECT * FROM meal_records WHERE date = :date ORDER BY timestamp DESC")
    fun observeMealsByDate(date: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM meal_records ORDER BY timestamp DESC LIMIT 5")
    suspend fun getRecentMeals(): List<MealEntity>

    @Query("SELECT COALESCE(SUM(total_calories), 0) FROM meal_records WHERE date = :date")
    suspend fun getDailyTotalCalories(date: String): Float

    @Query("SELECT COALESCE(SUM(total_protein), 0) FROM meal_records WHERE date = :date")
    suspend fun getDailyTotalProtein(date: String): Float

    @Query("SELECT COALESCE(SUM(total_carb), 0) FROM meal_records WHERE date = :date")
    suspend fun getDailyTotalCarb(date: String): Float

    @Query("SELECT COALESCE(SUM(total_fat), 0) FROM meal_records WHERE date = :date")
    suspend fun getDailyTotalFat(date: String): Float

    @Query("SELECT DISTINCT date FROM meal_records ORDER BY date DESC")
    suspend fun getAllRecordedDates(): List<String>

    @Query("DELETE FROM meal_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
