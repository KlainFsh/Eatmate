package com.example.eatmate.domain.usecase

import com.example.eatmate.domain.model.FoodAnalysis
import com.example.eatmate.domain.repository.FoodAnalysisRepository
import javax.inject.Inject

class AnalyzeFoodUseCase @Inject constructor(
    private val repository: FoodAnalysisRepository
) {
    suspend operator fun invoke(imageBytes: ByteArray): Result<FoodAnalysis> {
        return repository.analyzeFood(imageBytes)
    }
}
