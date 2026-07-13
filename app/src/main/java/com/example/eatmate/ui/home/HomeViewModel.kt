package com.example.eatmate.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatmate.data.local.dao.UserGoalDao
import com.example.eatmate.domain.model.Meal
import com.example.eatmate.domain.model.NutritionInfo
import com.example.eatmate.domain.model.UserGoal
import com.example.eatmate.domain.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val todayNutrition: NutritionInfo = NutritionInfo(),
    val recentMeals: List<Meal> = emptyList(),
    val userGoal: UserGoal? = null,
    val calorieProgress: Float = 0f // 0..1
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val goalDao: UserGoalDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                .format(java.util.Date())
            val nutrition = mealRepository.getDailyNutrition(today)
            val recent = mealRepository.getRecentMeals(5)

            val entity = goalDao.getGoal()
            val goal = entity?.let {
                UserGoal(
                    goalType = it.goalType,
                    currentWeightKg = it.currentWeightKg,
                    targetWeightKg = it.targetWeightKg,
                    dailyCalorieTargetKcal = it.dailyCalorieTargetKcal
                )
            }
            val progress = if (goal != null && goal.dailyCalorieTargetKcal > 0) {
                (nutrition.calories.toFloat() / goal.dailyCalorieTargetKcal).coerceIn(0f, 1f)
            } else 0f

            _uiState.update {
                it.copy(
                    todayNutrition = nutrition,
                    recentMeals = recent,
                    userGoal = goal,
                    calorieProgress = progress
                )
            }
        }
    }
}
