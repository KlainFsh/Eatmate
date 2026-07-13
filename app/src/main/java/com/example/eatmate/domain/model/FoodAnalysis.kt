package com.example.eatmate.domain.model

data class FoodAnalysis(
    val dishes: List<Food>,
    val total: NutritionInfo,
    val diningScene: String? = null,
    val advice: String? = null
)
