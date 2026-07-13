package com.example.eatmate.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatmate.data.local.EnenProfileManager
import com.example.eatmate.data.local.dao.UserGoalDao
import com.example.eatmate.data.local.entity.UserGoalEntity
import com.example.eatmate.domain.model.UserGoal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val currentWeightKg: Float = 65f,
    val targetWeightKg: Float = 65f,
    val dailyCalorieKcal: Int = 2000,
    val goalType: String = "maintain",
    val isSaved: Boolean = false,
    val enenName: String = "恩恩",
    val hasExistingGoal: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val goalDao: UserGoalDao,
    private val enenProfile: EnenProfileManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadGoal()
        loadEnenName()
    }

    private fun loadGoal() {
        viewModelScope.launch {
            val entity = goalDao.getGoal()
            if (entity != null) {
                _uiState.update {
                    ProfileUiState(
                        currentWeightKg = entity.currentWeightKg,
                        targetWeightKg = entity.targetWeightKg,
                        dailyCalorieKcal = entity.dailyCalorieTargetKcal,
                        goalType = entity.goalType,
                        hasExistingGoal = true
                    )
                }
            }
        }
    }

    fun updateGoalType(type: String) {
        _uiState.update { it.copy(goalType = type) }
    }

    fun updateCurrentWeight(kg: Float) {
        _uiState.update { it.copy(currentWeightKg = kg) }
    }

    fun updateTargetWeight(kg: Float) {
        _uiState.update { it.copy(targetWeightKg = kg) }
    }

    fun updateDailyCalorie(kcal: Int) {
        _uiState.update { it.copy(dailyCalorieKcal = kcal) }
    }

    fun saveGoal() {
        viewModelScope.launch {
            val state = _uiState.value
            goalDao.upsertGoal(
                UserGoalEntity(
                    goalType = state.goalType,
                    currentWeightKg = state.currentWeightKg,
                    targetWeightKg = state.targetWeightKg,
                    dailyCalorieTargetKcal = state.dailyCalorieKcal
                )
            )
            _uiState.update { it.copy(isSaved = true, hasExistingGoal = true) }
        }
    }

    private fun loadEnenName() {
        viewModelScope.launch {
            val name = enenProfile.getName()
            _uiState.update { it.copy(enenName = name) }
        }
    }

    fun updateEnenName(name: String) {
        _uiState.update { it.copy(enenName = name) }
    }

    fun saveEnenName() {
        viewModelScope.launch {
            enenProfile.setName(_uiState.value.enenName)
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    fun getUserGoal(): UserGoal {
        val s = _uiState.value
        return UserGoal(
            goalType = s.goalType,
            currentWeightKg = s.currentWeightKg,
            targetWeightKg = s.targetWeightKg,
            dailyCalorieTargetKcal = s.dailyCalorieKcal
        )
    }
}
