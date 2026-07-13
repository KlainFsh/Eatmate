package com.example.eatmate.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatmate.domain.model.FoodAnalysis
import com.example.eatmate.domain.model.Meal
import com.example.eatmate.domain.model.NutritionInfo
import com.example.eatmate.domain.usecase.AnalyzeFoodUseCase
import com.example.eatmate.domain.usecase.SaveMealUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class CameraUiState(
    val isAnalyzing: Boolean = false,
    val analysisResult: FoodAnalysis? = null,
    val error: String? = null,
    val savedMealId: Long? = null
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val analyzeFoodUseCase: AnalyzeFoodUseCase,
    private val saveMealUseCase: SaveMealUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun analyzePhoto(imageBytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true, error = null, analysisResult = null) }

            analyzeFoodUseCase(imageBytes)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(isAnalyzing = false, analysisResult = result)
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isAnalyzing = false, error = e.message ?: "分析失败")
                    }
                }
        }
    }

    fun saveMeal(imagePath: String, analysis: FoodAnalysis, mealType: String) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(now))

            val meal = Meal(
                mealType = mealType,
                date = today,
                timestamp = now,
                imagePath = imagePath,
                dishes = analysis.dishes,
                nutrition = analysis.total,
                diningScene = analysis.diningScene
            )

            saveMealUseCase(meal)
                .onSuccess { id ->
                    _uiState.update { it.copy(savedMealId = id) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message ?: "保存失败") }
                }
        }
    }

    fun setError(message: String) {
        _uiState.update { it.copy(isAnalyzing = false, error = message) }
    }

    fun resetState() {
        _uiState.value = CameraUiState()
    }
}
