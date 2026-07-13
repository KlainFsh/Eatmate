package com.example.eatmate.domain.model

data class UserGoal(
    val goalType: String,         // "lose_fat" / "gain_muscle" / "maintain"
    val currentWeightKg: Float,
    val targetWeightKg: Float,
    val dailyCalorieTargetKcal: Int
) {
    val goalLabel: String get() = when (goalType) {
        "lose_fat" -> "减脂"
        "gain_muscle" -> "增肌"
        else -> "维持体重"
    }

    companion object {
        fun default(): UserGoal = UserGoal(
            goalType = "maintain",
            currentWeightKg = 65f,
            targetWeightKg = 65f,
            dailyCalorieTargetKcal = 2000
        )
    }
}
