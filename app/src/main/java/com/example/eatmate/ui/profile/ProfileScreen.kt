package com.example.eatmate.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eatmate.ui.theme.BrandOrange
import com.example.eatmate.ui.theme.BrandPeach
import com.example.eatmate.ui.theme.BrandWarm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("我的目标") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Enen name config
            Text("AI 营养师名字", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = uiState.enenName,
                onValueChange = { viewModel.updateEnenName(it) },
                label = { Text("给TA起个名字") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(
                onClick = { viewModel.saveEnenName() },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
            ) {
                Text("保存名字", color = BrandWarm)
            }

            Spacer(Modifier.height(8.dp))

            // Goal type selector
            Text("选择目标", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "lose_fat" to "减脂",
                    "gain_muscle" to "增肌",
                    "maintain" to "维持体重"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = uiState.goalType == key,
                        onClick = { viewModel.updateGoalType(key) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPeach
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Weight inputs
            Text("体重信息", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = if (uiState.currentWeightKg == 0f) "" else uiState.currentWeightKg.toInt().toString(),
                    onValueChange = { viewModel.updateCurrentWeight(it.toFloatOrNull() ?: 0f) },
                    label = { Text("当前体重 (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = if (uiState.targetWeightKg == 0f) "" else uiState.targetWeightKg.toInt().toString(),
                    onValueChange = { viewModel.updateTargetWeight(it.toFloatOrNull() ?: 0f) },
                    label = { Text("目标体重 (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Daily calorie target
            Text("每日热量目标", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = if (uiState.dailyCalorieKcal == 0) "" else uiState.dailyCalorieKcal.toString(),
                onValueChange = { viewModel.updateDailyCalorie(it.toIntOrNull() ?: 0) },
                label = { Text("每日摄入目标 (千卡)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandPeach),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "目标预览",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val diff = uiState.targetWeightKg - uiState.currentWeightKg
                    val diffText = when {
                        diff > 0 -> "增重 ${diff.toInt()} kg"
                        diff < 0 -> "减重 ${(-diff).toInt()} kg"
                        else -> "维持当前体重"
                    }
                    Text("${uiState.goalType.toGoalLabel()} · $diffText")
                    Text("每日 ${uiState.dailyCalorieKcal} 千卡")
                }
            }

            // Save button
            Button(
                onClick = { viewModel.saveGoal() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
            ) {
                Text(
                    if (uiState.isSaved) "已保存" else "保存目标",
                    color = BrandWarm
                )
            }

            if (uiState.isSaved) {
                Text(
                    "目标已保存，首页和日记将展示对比数据",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun String.toGoalLabel(): String = when (this) {
    "lose_fat" -> "减脂"
    "gain_muscle" -> "增肌"
    else -> "维持体重"
}
