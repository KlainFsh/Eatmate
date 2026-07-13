package com.example.eatmate.domain.model

data class Meal(
    val id: Long = 0,
    val mealType: String,
    val date: String,
    val timestamp: Long,
    val imagePath: String?,
    val dishes: List<Food>,
    val nutrition: NutritionInfo,
    val diningScene: String? = null
)
