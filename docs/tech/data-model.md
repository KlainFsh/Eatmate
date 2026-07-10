# 智食 数据模型设计

> 版本: 1.0 | 更新: 2026-07-03

---

## 一、Room Entities

### MealEntity (餐食记录)

```kotlin
@Entity(tableName = "meal_records")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "meal_type")
    val mealType: String,          // "breakfast" | "lunch" | "dinner" | "snack"

    val date: String,              // "2026-07-03"

    val timestamp: Long,           // System.currentTimeMillis()

    @ColumnInfo(name = "image_path")
    val imagePath: String?,        // 本地照片路径

    @ColumnInfo(name = "dishes_json")
    val dishesJson: String,        // JSON: [{"name":"宫保鸡丁","calories":320,...}]

    @ColumnInfo(name = "total_calories")
    val totalCalories: Float,

    @ColumnInfo(name = "total_protein")
    val totalProtein: Float,

    @ColumnInfo(name = "total_carb")
    val totalCarb: Float,

    @ColumnInfo(name = "total_fat")
    val totalFat: Float
)
```

### UserProfileEntity (用户档案 — Phase 4)

```kotlin
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,               // 单例

    @ColumnInfo(name = "goal_type")
    val goalType: String,          // "lose_fat" | "gain_muscle" | "maintain"

    @ColumnInfo(name = "daily_calorie_target")
    val dailyCalorieTarget: Int,

    @ColumnInfo(name = "protein_target")
    val proteinTarget: Float,

    @ColumnInfo(name = "carb_target")
    val carbTarget: Float,

    @ColumnInfo(name = "fat_target")
    val fatTarget: Float
)
```

---

## 二、Domain Models

### Food (单菜品分析结果)

```kotlin
data class Food(
    val name: String,
    val estimatedWeightG: Int,
    val caloriesKcal: Float,
    val proteinG: Float,
    val carbG: Float,
    val fatG: Float,
    val confidence: Float
)
```

### NutritionInfo (营养素汇总)

```kotlin
data class NutritionInfo(
    val calories: Float,
    val protein: Float,
    val carb: Float,
    val fat: Float
)
```

### Meal (一顿饭的完整记录)

```kotlin
data class Meal(
    val id: Long = 0,
    val mealType: String,
    val date: String,
    val timestamp: Long,
    val imagePath: String?,
    val dishes: List<Food>,
    val nutrition: NutritionInfo
)
```

### FoodAnalysis (AI 分析结果)

```kotlin
data class FoodAnalysis(
    val dishes: List<Food>,
    val total: NutritionInfo,
    val diningScene: String?,      // "外卖" | "食堂" | "家常" | "餐厅" | "其他"
    val healthScore: Int?,         // 1-10 (Phase 5)
    val advice: String?            // 膳食建议 (Phase 5)
)
```

### UserGoal (用户目标)

```kotlin
data class UserGoal(
    val type: GoalType,
    val dailyCalories: Int,
    val dailyProtein: Float,
    val dailyCarb: Float,
    val dailyFat: Float
)

enum class GoalType {
    LOSE_FAT, GAIN_MUSCLE, MAINTAIN
}
```

---

## 三、API DTOs

### Qwen API 请求

```kotlin
@Serializable
data class ChatRequest(
    val model: String = "qwen3.6-plus",
    val messages: List<Message>
)

@Serializable
data class Message(
    val role: String,
    val content: List<ContentPart>
)

@Serializable
data class ContentPart(
    val type: String,               // "text" | "image_url"
    val text: String? = null,
    @SerialName("image_url")
    val imageUrl: ImageUrl? = null
)

@Serializable
data class ImageUrl(
    val url: String                 // "data:image/jpeg;base64,xxx"
)
```

### Qwen API 响应

```kotlin
@Serializable
data class ChatResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: ResponseMessage
)

@Serializable
data class ResponseMessage(
    val content: String             // JSON 字符串, 需手动解析
)
```

---

## 四、DataStore Keys

```kotlin
// 用户偏好 (Preferences DataStore)
object PreferenceKeys {
    val GOAL_TYPE = stringPreferencesKey("goal_type")
    val DAILY_CALORIE_TARGET = intPreferencesKey("daily_calorie_target")
    val PROTEIN_TARGET = floatPreferencesKey("protein_target")
    val QWEN_API_KEY = stringPreferencesKey("qwen_api_key")
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val TOKEN_EXPIRES_AT = longPreferencesKey("token_expires_at")
}
```
