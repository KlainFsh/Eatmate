package com.example.eatmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_goal")
data class UserGoalEntity(
    @PrimaryKey val id: Int = 1, // Single row
    val goalType: String,        // "lose_fat" / "gain_muscle" / "maintain"
    val currentWeightKg: Float,
    val targetWeightKg: Float,
    val dailyCalorieTargetKcal: Int,
    val updatedAt: Long = System.currentTimeMillis()
)
