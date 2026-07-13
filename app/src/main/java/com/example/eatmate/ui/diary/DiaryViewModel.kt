package com.example.eatmate.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatmate.domain.model.Meal
import com.example.eatmate.domain.model.NutritionInfo
import com.example.eatmate.domain.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class DiaryUiState(
    val selectedDate: String = "",
    val meals: List<Meal> = emptyList(),
    val dailyNutrition: NutritionInfo = NutritionInfo(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val calendar = Calendar.getInstance()

    init {
        loadToday()
    }

    fun loadToday() {
        loadDate(dateFormat.format(Date()))
    }

    fun loadDate(date: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val meals = mealRepository.getMealsByDate(date)
                val nutrition = mealRepository.getDailyNutrition(date)
                _uiState.update {
                    it.copy(
                        selectedDate = date,
                        meals = meals,
                        dailyNutrition = nutrition,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun goToPreviousDay() {
        calendar.time = dateFormat.parse(uiState.value.selectedDate) ?: Date()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        loadDate(dateFormat.format(calendar.time))
    }

    fun goToNextDay() {
        calendar.time = dateFormat.parse(uiState.value.selectedDate) ?: Date()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        loadDate(dateFormat.format(calendar.time))
    }

    fun isToday(): Boolean {
        return uiState.value.selectedDate == dateFormat.format(Date())
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.deleteMeal(meal.id)
            loadDate(uiState.value.selectedDate)
        }
    }
}
