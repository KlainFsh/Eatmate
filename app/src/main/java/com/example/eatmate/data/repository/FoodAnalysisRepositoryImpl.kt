package com.example.eatmate.data.repository

import android.util.Log
import com.example.eatmate.data.remote.ImageCompressor
import com.example.eatmate.data.remote.QwenApiService
import com.example.eatmate.data.remote.dto.ChatRequest
import com.example.eatmate.data.remote.dto.ContentPart
import com.example.eatmate.data.remote.dto.FoodAnalysisResponse
import com.example.eatmate.data.remote.dto.ImageUrl
import com.example.eatmate.data.remote.dto.Message
import com.example.eatmate.domain.model.Food
import com.example.eatmate.domain.model.FoodAnalysis
import com.example.eatmate.domain.model.NutritionInfo
import com.example.eatmate.domain.repository.FoodAnalysisRepository
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodAnalysisRepositoryImpl @Inject constructor(
    private val api: QwenApiService,
    private val compressor: ImageCompressor
) : FoodAnalysisRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    override suspend fun analyzeFood(imageBytes: ByteArray): Result<FoodAnalysis> {
        Log.d("Eatmate", "analyzeFood called, bytes=${imageBytes.size}")
        return try {
            val base64 = compressor.compressAndEncode(imageBytes)
            Log.d("Eatmate", "Image compressed, base64 len=${base64.length}")
            if (base64.isEmpty()) {
                Log.e("Eatmate", "Compression produced empty result")
                return Result.failure(Exception("图片压缩失败"))
            }

            val request = ChatRequest(
                model = "qwen-vl-plus",
                messages = listOf(
                    Message(
                        role = "user",
                        content = listOf(
                            ContentPart(
                                type = "text",
                                text = FOOD_ANALYSIS_PROMPT
                            ),
                            ContentPart(
                                type = "image_url",
                                imageUrl = ImageUrl(
                                    url = "data:image/jpeg;base64,$base64"
                                )
                            )
                        )
                    )
                )
            )

            Log.d("Eatmate", "Calling Qwen API...")
            val response = api.chat(request)
            Log.d("Eatmate", "API response received, choices=${response.choices.size}")
            val content = response.choices.firstOrNull()
                ?.message?.content
                ?: return Result.failure(Exception("AI 返回为空"))

            // Strip markdown code block if present
            val cleanJson = content
                .trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val parsed = json.decodeFromString<FoodAnalysisResponse>(cleanJson)

            val foods = parsed.dishes.map { d ->
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

            val result = FoodAnalysis(
                dishes = foods,
                total = NutritionInfo(
                    calories = parsed.totalCaloriesKcal,
                    protein = parsed.totalProteinG,
                    carb = parsed.totalCarbG,
                    fat = parsed.totalFatG
                ),
                diningScene = parsed.diningScene,
                advice = parsed.advice
            )

            Result.success(result)
        } catch (e: Exception) {
            Log.e("Eatmate", "Analysis failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    companion object {
        val FOOD_ANALYSIS_PROMPT = """
分析这张食物照片。返回严格JSON（不要markdown包裹）。识别规则：调味料不算独立菜品，米饭馒头需单独列出。份量估算：外卖主菜300g/食堂一格菜200g/家常一碗250g。热量公式：荤炸250kcal/100g，荤蒸150kcal/100g，炒素100kcal/100g，凉拌素50kcal/100g，米饭116kcal/100g，面条110kcal/100g。注意油光。

返回JSON格式:
{"dishes":[{"name":"","estimated_weight_g":0,"calories_kcal":0,"protein_g":0,"carb_g":0,"fat_g":0,"confidence":0.9}],"total_calories_kcal":0,"total_protein_g":0,"total_carb_g":0,"total_fat_g":0,"dining_scene":"食堂/外卖/家常/餐厅","advice":""}

纯JSON，首字符{末字符}。
""".trimIndent()
    }
}
