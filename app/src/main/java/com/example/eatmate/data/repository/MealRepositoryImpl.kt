package com.example.eatmate.data.repository

import com.example.eatmate.data.local.dao.MealDao
import com.example.eatmate.data.local.entity.MealEntity
import com.example.eatmate.domain.model.Food
import com.example.eatmate.domain.model.Meal
import com.example.eatmate.domain.model.NutritionInfo
import com.example.eatmate.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao
) : MealRepository {

    private val json = Json { ignoreUnknownKeys = true }

    @kotlinx.serialization.Serializable
    data class DishJson(
        val name: String,
        val estimatedWeightG: Int = 0,
        val caloriesKcal: Float = 0f,
        val proteinG: Float = 0f,
        val carbG: Float = 0f,
        val fatG: Float = 0f,
        val confidence: Float = 0f
    )

    override suspend fun saveMeal(meal: Meal): Long {
        val dishesJson = json.encodeToString(
            meal.dishes.map { dish ->
                DishJson(
                    name = dish.name,
                    estimatedWeightG = dish.estimatedWeightG,
                    caloriesKcal = dish.caloriesKcal,
                    proteinG = dish.proteinG,
                    carbG = dish.carbG,
                    fatG = dish.fatG,
                    confidence = dish.confidence
                )
            }
        )
        val entity = MealEntity(
            mealType = meal.mealType,
            date = meal.date,
            timestamp = meal.timestamp,
            imagePath = meal.imagePath,
            dishesJson = dishesJson,
            totalCalories = meal.nutrition.calories,
            totalProtein = meal.nutrition.protein,
            totalCarb = meal.nutrition.carb,
            totalFat = meal.nutrition.fat,
            diningScene = meal.diningScene
        )
        return mealDao.insert(entity)
    }

    override suspend fun getMealsByDate(date: String): List<Meal> {
        return mealDao.getMealsByDate(date).map { it.toDomain() }
    }

    override fun observeMealsByDate(date: String): Flow<List<Meal>> {
        return mealDao.observeMealsByDate(date).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getRecentMeals(limit: Int): List<Meal> {
        return mealDao.getRecentMeals().take(limit).map { it.toDomain() }
    }

    override suspend fun getDailyNutrition(date: String): NutritionInfo {
        val calories = mealDao.getDailyTotalCalories(date)
        val protein = mealDao.getDailyTotalProtein(date)
        val carb = mealDao.getDailyTotalCarb(date)
        val fat = mealDao.getDailyTotalFat(date)
        return NutritionInfo(
            calories = calories,
            protein = protein,
            carb = carb,
            fat = fat
        )
    }

    override suspend fun deleteMeal(id: Long) {
        mealDao.deleteById(id)
    }

    private fun MealEntity.toDomain(): Meal {
        val dishList = try {
            json.decodeFromString<List<DishJson>>(dishesJson).map { d ->
                Food(
                    name = d.name,
                    estimatedWeightG = d.estimatedWeightG,
                    caloriesKcal = d.caloriesKcal,
                    proteinG = d.proteinG,
                    carbG = d.carbG,
                    fatG = d.fatG,
                    confidence = d.confidence
                )
            }
        } catch (_: Exception) {
            emptyList()
        }

        return Meal(
            id = id,
            mealType = mealType,
            date = date,
            timestamp = timestamp,
            imagePath = imagePath,
            dishes = dishList,
            nutrition = NutritionInfo(
                calories = totalCalories,
                protein = totalProtein,
                carb = totalCarb,
                fat = totalFat
            ),
            diningScene = diningScene
        )
    }
}
