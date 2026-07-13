package com.example.eatmate.ui.result

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.eatmate.domain.model.Food
import com.example.eatmate.domain.model.FoodAnalysis
import com.example.eatmate.domain.model.NutritionInfo
import com.example.eatmate.ui.camera.CameraViewModel
import com.example.eatmate.ui.theme.BrandOrange
import com.example.eatmate.ui.theme.BrandPeach
import com.example.eatmate.ui.theme.CarbYellow
import com.example.eatmate.ui.theme.FatCoral
import com.example.eatmate.ui.theme.ProteinPurple
import com.example.eatmate.ui.theme.SurfaceLight
import java.io.File
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    imagePath: String,
    onNavigateBack: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel(LocalActivity.current as androidx.activity.ComponentActivity)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("识别结果") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetState()
                        onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        val analysis = uiState.analysisResult

        if (analysis == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (uiState.error != null) "分析失败" else "暂无分析结果",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        uiState.error ?: "请拍照后重试",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (uiState.error != null)
                            MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    Surface(
                        onClick = onNavigateBack,
                        shape = RoundedCornerShape(16.dp),
                        color = BrandOrange
                    ) {
                        Text(
                            "返回重试",
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF3D2E1F)
                        )
                    }
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Photo thumbnail
            AsyncImage(
                model = File(imagePath),
                contentDescription = "拍摄的照片",
                modifier = Modifier.fillMaxWidth().height(220.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(16.dp))

            // Total calories card
            Surface(
                color = BrandPeach,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("总热量", style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF5C4322))
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${analysis.total.calories.roundToInt()}",
                            style = MaterialTheme.typography.displayLarge,
                            color = Color(0xFF3D2E1F)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("千卡", style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF7A6650),
                            modifier = Modifier.padding(bottom = 6.dp))
                    }
                    analysis.diningScene?.let { scene ->
                        Spacer(Modifier.height(4.dp))
                        Text("场景: $scene", style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF7A6650))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Macro nutrients
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MacroChip("蛋白质", "${analysis.total.protein.roundToInt()}g", ProteinPurple, Modifier.weight(1f))
                MacroChip("碳水", "${analysis.total.carb.roundToInt()}g", CarbYellow, Modifier.weight(1f))
                MacroChip("脂肪", "${analysis.total.fat.roundToInt()}g", FatCoral, Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            // Dish list
            Text(
                text = "识别到的菜品",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))

            analysis.dishes.forEach { dish ->
                DishCard(dish, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp))
            }

            // Advice
            analysis.advice?.let { advice ->
                Spacer(Modifier.height(16.dp))
                Surface(
                    color = SurfaceLight,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("饮食建议", style = MaterialTheme.typography.labelLarge,
                            color = BrandOrange)
                        Spacer(Modifier.height(4.dp))
                        Text(advice, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Save button
            Button(
                onClick = {
                    viewModel.saveMeal(imagePath, analysis, "lunch")
                },
                enabled = uiState.savedMealId == null,
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp)
            ) {
                Text(
                    text = if (uiState.savedMealId != null) "已保存 ✅" else "记录这顿饭",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF3D2E1F)
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MacroChip(label: String, value: String, color: Color, modifier: Modifier) {
    Surface(
        color = SurfaceLight,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(color = color.copy(alpha = 0.3f), shape = CircleShape, modifier = Modifier.size(10.dp)) {}
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DishCard(dish: Food, modifier: Modifier) {
    Surface(
        color = SurfaceLight,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = BrandOrange.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🍽", style = MaterialTheme.typography.titleLarge)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(dish.name, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "${(dish.confidence * 100).roundToInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "${dish.caloriesKcal.roundToInt()} kcal · ${dish.estimatedWeightG}g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
