package com.example.eatmate.domain.repository

import com.example.eatmate.domain.model.FoodAnalysis

interface FoodAnalysisRepository {
    suspend fun analyzeFood(imageBytes: ByteArray): Result<FoodAnalysis>
}
