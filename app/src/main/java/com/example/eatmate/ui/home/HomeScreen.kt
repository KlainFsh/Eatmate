package com.example.eatmate.ui.home

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.eatmate.domain.model.Meal
import com.example.eatmate.ui.theme.BrandOrange
import com.example.eatmate.ui.theme.BrandPeach
import com.example.eatmate.ui.theme.CarbYellow
import com.example.eatmate.ui.theme.FatCoral
import com.example.eatmate.ui.theme.ProteinPurple
import com.example.eatmate.ui.theme.SurfaceLight
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    onNavigateToCamera: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Refresh when returning to this screen
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("早上好 ☀️", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(4.dp))
        Text("今天吃对了吗？", style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.height(24.dp))

        // Today summary
        val nutrition = uiState.todayNutrition
        Surface(color = BrandPeach, shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("今日摄入", style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF5C4322))
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = if (nutrition.calories > 0) "${nutrition.calories.roundToInt()}" else "--",
                        style = MaterialTheme.typography.displayLarge,
                        color = Color(0xFF3D2E1F)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("千卡", style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF7A6650),
                        modifier = Modifier.padding(bottom = 6.dp))
                }
                Spacer(Modifier.height(4.dp))
                Text("目标 1,800 千卡", style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF7A6650))
            }
        }

        Spacer(Modifier.height(20.dp))

        // Quick camera action
        Card(
            onClick = onNavigateToCamera,
            colors = CardDefaults.cardColors(containerColor = BrandOrange),
            shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()
        ) {
            Row(Modifier.fillMaxWidth().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color.White.copy(alpha = 0.25f),
                    shape = CircleShape, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Filled.CameraAlt, null,
                        tint = Color.White, modifier = Modifier.padding(12.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("拍照识别食物", style = MaterialTheme.typography.titleMedium,
                        color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("一键获取热量与营养素", style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Macros
        Text("今日营养素", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MacroCard("蛋白质", formatGrams(nutrition.protein), ProteinPurple, Modifier.weight(1f))
            MacroCard("碳水", formatGrams(nutrition.carb), CarbYellow, Modifier.weight(1f))
            MacroCard("脂肪", formatGrams(nutrition.fat), FatCoral, Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))

        // Recent meals
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("最近记录", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = { /* navigate to diary */ }) {
                Text("查看全部")
            }
        }

        Spacer(Modifier.height(8.dp))
        if (uiState.recentMeals.isEmpty()) {
            Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Restaurant, null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(40.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("还没有记录", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("拍张照片开始记录吧", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
        } else {
            uiState.recentMeals.forEach { meal ->
                RecentMealItem(meal)
            }
        }
    }
}

@Composable
private fun MacroCard(label: String, value: String, color: Color, modifier: Modifier) {
    Surface(color = SurfaceLight, shape = RoundedCornerShape(16.dp), modifier = modifier) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(color = color.copy(alpha = 0.25f), shape = CircleShape,
                modifier = Modifier.size(12.dp)) {}
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun RecentMealItem(meal: Meal) {
    Surface(color = SurfaceLight, shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = BrandOrange.copy(alpha = 0.1f),
                shape = RoundedCornerShape(10.dp), modifier = Modifier.size(40.dp)) {
                Text("🍽", modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(meal.dishes.firstOrNull()?.name ?: "餐食",
                    style = MaterialTheme.typography.titleMedium)
                Text("${meal.nutrition.calories.roundToInt()} 千卡",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            val typeLabels = mapOf("breakfast" to "早","lunch" to "午","dinner" to "晚","snack" to "加")
            Surface(color = BrandPeach, shape = RoundedCornerShape(6.dp)) {
                Text(typeLabels[meal.mealType] ?: "", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall, color = Color(0xFF5C4322))
            }
        }
    }
}

private fun formatGrams(value: Float): String =
    if (value > 0) "${value.roundToInt()} g" else "-- g"
