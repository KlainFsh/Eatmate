package com.example.eatmate.domain.usecase

import com.example.eatmate.domain.model.Meal
import com.example.eatmate.domain.repository.MealRepository
import javax.inject.Inject

class SaveMealUseCase @Inject constructor(
    private val repository: MealRepository
) {
    suspend operator fun invoke(meal: Meal): Result<Long> {
        return try {
            val id = repository.saveMeal(meal)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
