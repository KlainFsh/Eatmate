package com.example.eatmate.domain.model

data class Food(
    val name: String,
    val estimatedWeightG: Int,
    val caloriesKcal: Float,
    val proteinG: Float,
    val carbG: Float,
    val fatG: Float,
    val confidence: Float
)
