package com.example.eatmate.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Expected structure from Qwen's food analysis JSON response.
 * Parsed from the raw content string returned by the API.
 */
@Serializable
data class FoodAnalysisResponse(
    val dishes: List<DishItem> = emptyList(),
    @kotlinx.serialization.SerialName("total_calories_kcal")
    val totalCaloriesKcal: Float = 0f,
    @kotlinx.serialization.SerialName("total_protein_g")
    val totalProteinG: Float = 0f,
    @kotlinx.serialization.SerialName("total_carb_g")
    val totalCarbG: Float = 0f,
    @kotlinx.serialization.SerialName("total_fat_g")
    val totalFatG: Float = 0f,
    @kotlinx.serialization.SerialName("dining_scene")
    val diningScene: String? = null,
    val advice: String? = null
)

@Serializable
data class DishItem(
    val name: String = "",
    @kotlinx.serialization.SerialName("estimated_weight_g")
    val estimatedWeightG: Int = 0,
    @kotlinx.serialization.SerialName("calories_kcal")
    val caloriesKcal: Float = 0f,
    @kotlinx.serialization.SerialName("protein_g")
    val proteinG: Float = 0f,
    @kotlinx.serialization.SerialName("carb_g")
    val carbG: Float = 0f,
    @kotlinx.serialization.SerialName("fat_g")
    val fatG: Float = 0f,
    val confidence: Float = 0f
)
