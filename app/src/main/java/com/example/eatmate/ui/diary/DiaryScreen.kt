package com.example.eatmate.ui.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eatmate.domain.model.Meal
import com.example.eatmate.domain.model.NutritionInfo
import com.example.eatmate.ui.theme.BrandGreen
import com.example.eatmate.ui.theme.BrandOrange
import com.example.eatmate.ui.theme.BrandPeach
import com.example.eatmate.ui.theme.CarbYellow
import com.example.eatmate.ui.theme.FatCoral
import com.example.eatmate.ui.theme.ProteinPurple
import com.example.eatmate.ui.theme.SurfaceLight
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

private val mealTypeLabels = mapOf(
    "breakfast" to "早餐",
    "lunch" to "午餐",
    "dinner" to "晚餐",
    "snack" to "加餐"
)

private val mealTypeOrder = listOf("breakfast", "lunch", "dinner", "snack")

@Composable
fun DiaryScreen(
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Date switcher
        DateSwitcher(
            selectedDate = uiState.selectedDate,
            isToday = viewModel.isToday(),
            onPrevious = { viewModel.goToPreviousDay() },
            onNext = { viewModel.goToNextDay() },
            onToday = { viewModel.loadToday() }
        )

        Spacer(Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandOrange)
            }
            return
        }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Daily nutrition summary
            NutritionSummaryCard(uiState.dailyNutrition)

            Spacer(Modifier.height(16.dp))

            // Meals grouped by type
            if (uiState.meals.isEmpty()) {
                EmptyDiary()
            } else {
                val grouped = uiState.meals.groupBy { it.mealType }

                mealTypeOrder.forEach { type ->
                    grouped[type]?.let { meals ->
                        MealSection(type = type, meals = meals,
                            onDelete = { viewModel.deleteMeal(it) })
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DateSwitcher(
    selectedDate: String,
    isToday: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "前一天")
        }

        Text(
            text = formatDisplayDate(selectedDate),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!isToday) {
                Surface(
                    onClick = onToday,
                    shape = RoundedCornerShape(8.dp),
                    color = BrandPeach
                ) {
                    Text(
                        "今天",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF5C4322)
                    )
                }
                Spacer(Modifier.width(4.dp))
            }
            IconButton(onClick = onNext) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "后一天")
            }
        }
    }
}

@Composable
private fun NutritionSummaryCard(nutrition: NutritionInfo) {
    Surface(
        color = BrandPeach,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("总摄入", style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF5C4322))
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = if (nutrition.calories > 0) "${nutrition.calories.roundToInt()}" else "--",
                    style = MaterialTheme.typography.displayLarge,
                    color = Color(0xFF3D2E1F)
                )
                Spacer(Modifier.width(4.dp))
                Text("千卡", style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF7A6650),
                    modifier = Modifier.padding(bottom = 6.dp))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                NutrientLabel("蛋白质", nutrition.protein, ProteinPurple)
                NutrientLabel("碳水", nutrition.carb, CarbYellow)
                NutrientLabel("脂肪", nutrition.fat, FatCoral)
            }
        }
    }
}

@Composable
private fun NutrientLabel(name: String, value: Float, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(color = color, shape = RoundedCornerShape(2.dp),
            modifier = Modifier.size(8.dp)) {}
        Spacer(Modifier.width(6.dp))
        Text(
            "${name} ${if (value > 0) "${value.roundToInt()}g" else "--"}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF5C4322)
        )
    }
}

@Composable
private fun MealSection(type: String, meals: List<Meal>, onDelete: (Meal) -> Unit) {
    val totalCal = meals.sumOf { it.nutrition.calories.toDouble() }.roundToInt()

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = BrandGreen.copy(alpha = 0.3f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                mealTypeLabels[type] ?: type,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF3D2E1F)
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            "${totalCal} 千卡",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    meals.forEach { meal ->
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        meal.dishes.firstOrNull()?.let { dish ->
                            Text(dish.name, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "${dish.caloriesKcal.roundToInt()}kcal",
                                style = MaterialTheme.typography.bodySmall,
                                color = BrandOrange,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    if (meal.dishes.size > 1) {
                        Text(
                            "+${meal.dishes.size - 1} 道菜",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    meal.diningScene?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    }
                }
                IconButton(onClick = { onDelete(meal) }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Filled.DeleteOutline, "删除",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyDiary() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.Restaurant, null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text("今天还没有记录", style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("拍张照片开始记录吧", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        }
    }
}

private fun formatDisplayDate(dateStr: String): String {
    return try {
        val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateStr)
        val out = SimpleDateFormat("M月d日 EEEE", Locale.CHINESE)
        out.format(parsed!!)
    } catch (_: Exception) {
        dateStr
    }
}
