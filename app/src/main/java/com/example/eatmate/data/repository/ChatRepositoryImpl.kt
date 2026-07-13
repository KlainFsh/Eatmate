package com.example.eatmate.data.repository

import android.util.Log
import com.example.eatmate.data.local.EnenProfileManager
import com.example.eatmate.data.local.dao.ChatMessageDao
import com.example.eatmate.data.local.dao.MealDao
import com.example.eatmate.data.local.dao.UserGoalDao
import com.example.eatmate.data.local.entity.ChatMessageEntity
import com.example.eatmate.data.local.entity.MealEntity
import com.example.eatmate.data.remote.ImageCompressor
import com.example.eatmate.data.remote.QwenApiService
import com.example.eatmate.data.remote.dto.ChatRequest
import com.example.eatmate.data.remote.dto.ContentPart
import com.example.eatmate.data.remote.dto.FoodAnalysisResponse
import com.example.eatmate.data.remote.dto.ImageUrl
import com.example.eatmate.data.remote.dto.Message
import com.example.eatmate.domain.repository.ChatRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatMessageDao,
    private val vlApi: QwenApiService,
    private val compressor: ImageCompressor,
    private val enenProfile: EnenProfileManager,
    private val goalDao: UserGoalDao,
    private val mealDao: MealDao
) : ChatRepository {

    private val logTag = "Eatmate.Chat"
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    override suspend fun getAllMessages() = chatDao.observeAllMessages().first()

    override suspend fun getRecentMessages(count: Int) = chatDao.getRecentMessages(count)

    override suspend fun getMessageCount() = chatDao.getMessageCount()

    override suspend fun saveMessage(message: ChatMessageEntity) = chatDao.insertMessage(message)

    override suspend fun updateMessage(id: Long, foodDataJson: String) {
        chatDao.updateFoodDataJson(id, foodDataJson)
    }

    override suspend fun sendMessage(userText: String?, imageBytes: ByteArray?, existingUserMsgId: Long): ChatMessageEntity {
        Log.d(logTag, "sendMessage: text=$userText, hasImage=${imageBytes != null}, bytes=${imageBytes?.size ?: 0}")

        // Step 1: VL analysis
        var foodJson: String? = null
        if (imageBytes != null) {
            try {
                val base64 = compressor.compressAndEncode(imageBytes)
                Log.d(logTag, "VL: compressed, base64 len=${base64.length}")
                val vlRequest = ChatRequest(
                    model = "qwen-vl-plus",
                    messages = listOf(Message("user", listOf(
                        ContentPart("text", FOOD_VISION_PROMPT),
                        ContentPart("image_url", imageUrl = ImageUrl("data:image/jpeg;base64,$base64"))
                    )))
                )
                Log.d(logTag, "VL: calling API...")
                val vlResponse = vlApi.chat(vlRequest)
                var rawJson = vlResponse.choices.firstOrNull()?.message?.content ?: ""
                rawJson = rawJson.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                foodJson = rawJson
                Log.d(logTag, "VL: response received, json len=${foodJson.length}")

                // Parse and save to meal_records
                try {
                    val foodAnalysis = json.parseToFoodAnalysis(foodJson)
                    if (foodAnalysis != null && foodAnalysis.dishes.isNotEmpty()) {
                        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(java.util.Date())
                        val meal = MealEntity(
                            mealType = "snack", // chat default, user can change later
                            date = today,
                            timestamp = System.currentTimeMillis(),
                            imagePath = null,
                            dishesJson = foodJson,
                            totalCalories = foodAnalysis.totalCaloriesKcal,
                            totalProtein = foodAnalysis.totalProteinG,
                            totalCarb = foodAnalysis.totalCarbG,
                            totalFat = foodAnalysis.totalFatG,
                            diningScene = foodAnalysis.diningScene
                        )
                        mealDao.insert(meal)
                        Log.d(logTag, "Saved meal: ${foodAnalysis.dishes.first().name} ${foodAnalysis.totalCaloriesKcal} kcal")
                    }
                } catch (e: Exception) {
                    Log.e(logTag, "Failed to save meal from foodJson", e)
                }
            } catch (e: Exception) {
                Log.e(logTag, "VL API failed", e)
                throw e
            }
        }

        // Step 2: Save/update user message
        if (existingUserMsgId > 0 && foodJson != null) {
            chatDao.updateFoodDataJson(existingUserMsgId, foodJson)
            Log.d(logTag, "Updated user msg $existingUserMsgId with foodJson")
        } else if (existingUserMsgId == 0L) {
            val userMsg = ChatMessageEntity(
                timestamp = System.currentTimeMillis(),
                role = "user",
                content = userText ?: "",
                imagePath = null,
                foodDataJson = foodJson
            )
            Log.d(logTag, "Saving user msg: content=${userMsg.content}, hasJson=${foodJson != null}")
            chatDao.insertMessage(userMsg)
        }

        // Step 3: Build context
        val enenName = enenProfile.getName()
        val history = chatDao.getRecentMessages(10).reversed()
        val goal = goalDao.getGoal()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(java.util.Date())
        val todayCalories = mealDao.getDailyTotalCalories(today)
        val systemPrompt = buildSystemPrompt(enenName, goal, todayCalories)

        val messages = mutableListOf<Message>()
        // System prompt as first user message (some models don't support system role)
        messages.add(Message("user", listOf(ContentPart("text", systemPrompt))))
        messages.add(Message("assistant", listOf(ContentPart("text", "好的，我记住了～"))))
        history.forEach { msg ->
            val role = if (msg.role == "enen") "assistant" else "user"
            val ctx = if (msg.foodDataJson != null) {
                "[用户分享了食物照片，分析数据：${msg.foodDataJson}] ${msg.content}"
            } else msg.content
            messages.add(Message(role, listOf(ContentPart("text", ctx))))
        }

        // Step 4: Text chat — qwen-plus via ChatRequest (reliable serialization)
        Log.d(logTag, "Text: ${messages.size} messages to qwen-plus")
        val chatRequest = ChatRequest(model = "qwen-plus", messages = messages)
        val chatResponse = vlApi.chat(chatRequest)
        val replyContent = chatResponse.choices.firstOrNull()?.message?.content
            ?: "啊网络不太好...等会儿再跟我说说嘛～"
        Log.d(logTag, "Text: reply len=${replyContent.length}")

        // Step 5: Save reply
        val enenMsg = ChatMessageEntity(
            timestamp = System.currentTimeMillis(),
            role = "enen",
            content = replyContent,
            imagePath = null,
            foodDataJson = null
        )
        chatDao.insertMessage(enenMsg)

        return enenMsg
    }

    private fun buildSystemPrompt(
        enenName: String,
        goal: com.example.eatmate.data.local.entity.UserGoalEntity?,
        todayCalories: Float
    ): String {
        val goalText = if (goal != null) {
            val type = when (goal.goalType) {
                "lose_fat" -> "减脂"
                "gain_muscle" -> "增肌"
                else -> "维持体重"
            }
            "目标：$type，每日热量上限 ${goal.dailyCalorieTargetKcal} 千卡"
        } else "用户还没有设定目标"

        val todayText = if (todayCalories > 0) {
            "今日已摄入 ${todayCalories.toInt()} 千卡"
        } else ""

        return """
你是"$enenName"，一位专属私人营养师，同时也是用户贴心的伙伴。

## 核心人设
- 温暖、亲切、甜甜的，像在跟喜欢的人聊天
- 会夸用户的食物选择，给予情绪价值
- 在甜蜜中融入专业营养知识
- 不油腻、不说教、不扫兴

## 专业背景
- 你精通营养学，知道各种中餐的热量和营养
- 你能从营养角度给出温和的建议
$goalText
$todayText

## 对话原则
1. 开头可以带 😋🍚✨ 等表情，但要克制
2. 如果用户分享食物，先真诚夸赞——颜色、搭配、看起来有多好吃
3. 自然地提到营养数据（热量、蛋白质等），不是念数据而是融入聊天
4. 如果用户今天还没达成目标，给予正向鼓励
5. 如果热量偏高，温和提醒但不让用户感到压力——"今天吃得很丰盛呢～😊"
6. 回复长度控制在2-4句话，保持聊天的轻松感
7. 记住：你是"报备对象"，不是冷冰冰的营养计算器

## 禁止
- 不要提"我是AI"或"我是语言模型"
- 不要说教或让用户有压力
- 不要用正式的营养学术报告口吻
- 回复不要超过4句话
""".trimIndent()
    }

    companion object {
        val FOOD_VISION_PROMPT = """
分析这张食物照片。返回严格JSON（不要markdown包裹）。

识别规则：调味料不算独立菜品，米饭馒头需单独列出。
份量估算：外卖主菜300g/食堂一格菜200g/家常一碗250g。
热量公式：荤炸250kcal/100g，荤蒸150kcal/100g，炒素100kcal/100g，凉拌素50kcal/100g，米饭116kcal/100g，面条110kcal/100g。
注意油光。

额外输出画面信息：dish_appearance（颜色、摆盘、看起来好不好吃）、cooking_style（做法描述，如红烧、清蒸）。

返回JSON:
{"dishes":[{"name":"","estimated_weight_g":0,"calories_kcal":0,"protein_g":0,"carb_g":0,"fat_g":0,"confidence":0.9}],"total_calories_kcal":0,"total_protein_g":0,"total_carb_g":0,"total_fat_g":0,"dining_scene":"","dish_appearance":"","cooking_style":"","advice":""}

纯JSON。
""".trimIndent()
    }

    private fun Json.parseToFoodAnalysis(raw: String): FoodAnalysisResponse? {
        return try {
            this.decodeFromString<FoodAnalysisResponse>(raw)
        } catch (e: Exception) {
            Log.w(logTag, "Failed to parse food JSON: ${e.message}")
            null
        }
    }
}
