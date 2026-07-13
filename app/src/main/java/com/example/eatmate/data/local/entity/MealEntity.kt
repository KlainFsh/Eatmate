package com.example.eatmate.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_records")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "meal_type")
    val mealType: String,          // "breakfast" | "lunch" | "dinner" | "snack"

    val date: String,              // "2026-07-13"

    val timestamp: Long,

    @ColumnInfo(name = "image_path")
    val imagePath: String?,

    @ColumnInfo(name = "dishes_json")
    val dishesJson: String,        // JSON array of dishes

    @ColumnInfo(name = "total_calories")
    val totalCalories: Float,

    @ColumnInfo(name = "total_protein")
    val totalProtein: Float,

    @ColumnInfo(name = "total_carb")
    val totalCarb: Float,

    @ColumnInfo(name = "total_fat")
    val totalFat: Float,

    @ColumnInfo(name = "dining_scene")
    val diningScene: String? = null
)
