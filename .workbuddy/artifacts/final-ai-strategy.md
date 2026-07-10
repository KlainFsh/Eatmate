# 智食 AI 识别方案终版对比：专用 API vs 多模态大模型

> 调研日期: 2026-07-03 | 结论: **直接上多模态大模型，跳过百度专用 API**

---

## 一、三种方案全景对比

| 维度 | 百度 v2/dish | 阿里云食物识别(聚美) | 直接调多模态大模型 |
|------|:---:|:---:|:---:|
| **本质** | 专用图像分类 API | 千问大模型包装的专用 API | 裸调 Qwen3-VL / 混元 Vision |
| **多菜品识别** | ❌ 单菜品 | ✅ 支持 | ✅ 支持 |
| **热量返回** | ✅ cal/100g (String) | ✅ cal + GI | ✅ 可让 AI 直接给 |
| **蛋白质/碳水/脂肪** | ❌ 不返回 | ✅ 返回 | ✅ 让 AI 一次性全部给 |
| **重量估算** | ❌ 不估算 | ✅ 预估重量 | ✅ 可让 AI 估算 |
| **GI 血糖指数** | ❌ 无 | ✅ 返回 | ✅ 可选要 |
| **输出格式** | 固定 JSON | 固定 JSON | **你定 JSON Schema** |
| **单次成本** | 0.0007 元 | 0.025 元 | ~0.003-0.01 元 |
| **万次成本** | 7 元 | 220 元 | ~30-100 元 |
| **维护风险** | ✅ 稳定 | ✅ 稳定 | ✅ 稳定 |
| **灵活性** | ⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **需要建营养库** | 必须 | 不需要 | 不需要 |

---

## 二、为什么多模态大模型是最好的选择

### 2.1 数据完整性的碾压

百度 API 你面对的是这个问题：

```
百度返回: { name: "宫保鸡丁", calorie: "180" }
                          ↓
                      只给了一个数
                          ↓
    ┌─────────┬─────────┬─────────┬─────────┐
    │ 蛋白质?  │ 碳水?   │ 脂肪?   │ 多重?   │
    │   ❓    │   ❓    │   ❓    │   ❓    │
    └─────────┴─────────┴─────────┴─────────┘
            ↓ 全都要自己建库 + 自己估算
```

多模态大模型你面对的是这个：

```
你的 Prompt:
"分析这张食物照片。返回 JSON:
{ dishes: [{ name, calories, protein_g, carb_g, fat_g, estimated_weight_g, confidence }] }"

AI 返回:
{
  "dishes": [{
    "name": "宫保鸡丁",
    "calories": 320,
    "protein_g": 22.5,
    "carb_g": 15.8,
    "fat_g": 18.2,
    "estimated_weight_g": 280,
    "confidence": 0.92
  }]
}
```

**一步到位。不需要营养库、不需要份量估算模块、不需要二次匹配。** 这对 MVP 开发速度是降维打击。

### 2.2 成本依然可控

| 方案 | 万次成本 | 千 DAU 月费(人均拍3张) |
|------|----------|------------------------|
| 百度 v2/dish | 7 元 | ~0.6 元 |
| 直接调 VL | ~50 元 | ~4.5 元 |
| 阿里云食物识别 | 220 元 | ~20 元 |

日活 1000 人，月费 4.5 元，**比一杯奶茶还便宜**。即使到 10 万 DAU，月费也才 450 元。在 MVP 和早期阶段，这点成本完全可以忽略。

### 2.3 演进能力完全不同

```
百度专用 API 路线:
  单菜品 → [卡住] → 换 API → 重构

多模态大模型路线:
  v1: 菜品+热量
  v2: 菜品+热量+营养+份量
  v3: 菜品+热量+营养+份量+膳食建议+忌口提醒
  v4: 菜品+热量+营养+份量+个性化建议+历史对比
  ...
  全部只改 Prompt，不改代码
```

---

## 三、推荐方案与实现路径

### 推荐: 阿里云百炼 Qwen3-VL-Plus

| 维度 | 选择理由 |
|------|----------|
| **模型** | `qwen3-vl-plus` — 视觉理解能力强、支持多图、输出格式可控 |
| **价格** | 图片输入 1.5 元/千tokens，输出 4.5 元/千tokens |
| **免费额度** | 新用户 100 万 tokens（够跑数百次测试） |
| **生态** | 与阿里云 OSS、函数计算无缝对接 |

### 核心 Prompt 设计

```kotlin
// Android 端调用的核心 Prompt
val FOOD_ANALYSIS_PROMPT = """
你是一个专业的营养师。分析这张食物照片，返回严格的 JSON 格式，不要包含任何其他文字。

要求：
1. 识别照片中所有的食物/菜品
2. 估算每道菜的实际重量（克）
3. 计算每道菜的总热量（千卡）和宏量营养素（蛋白质、碳水、脂肪，单位克）
4. 如果识别不确定，confidence 给低分（0-1）

返回格式（必须是合法 JSON）：
{
  "dishes": [
    {
      "name": "菜品名称",
      "estimated_weight_g": 300,
      "calories_kcal": 280,
      "protein_g": 22.5,
      "carb_g": 35.0,
      "fat_g": 12.0,
      "confidence": 0.92,
      "notes": "识别备注（如有不确定可说明）"
    }
  ],
  "total_calories_kcal": 560,
  "dining_scene": "食堂/外卖/家常/聚餐"
}
""".trimIndent()
```

### Android 端调用架构

```kotlin
// 1. API 接口（百炼 OpenAI 兼容模式）
interface BailianVisionApi {
    @POST("compatible-mode/v1/chat/completions")
    suspend fun chat(
        @Header("Authorization") auth: String,
        @Body request: ChatRequest
    ): ChatResponse
}

@Serializable
data class ChatRequest(
    val model: String = "qwen3-vl-plus",
    val messages: List<Message>,
    val response_format: ResponseFormat? = null  // JSON mode
)

@Serializable  
data class Message(
    val role: String,
    val content: List<ContentPart>
)

@Serializable
data class ContentPart(
    val type: String,  // "text" or "image_url"
    val text: String? = null,
    val image_url: ImageUrl? = null
)

// 2. 使用示例
class FoodAnalysisService @Inject constructor(
    private val api: BailianVisionApi
) {
    suspend fun analyzeFood(imageBase64: String): FoodAnalysisResult {
        val request = ChatRequest(
            model = "qwen3-vl-plus",
            messages = listOf(
                Message(
                    role = "user",
                    content = listOf(
                        ContentPart(type = "text", text = FOOD_ANALYSIS_PROMPT),
                        ContentPart(
                            type = "image_url",
                            image_url = ImageUrl("data:image/jpeg;base64,$imageBase64")
                        )
                    )
                )
            )
        )
        
        val response = api.chat(
            auth = "Bearer $DASHSCOPE_API_KEY",
            request = request
        )
        
        return jsonParser.parse(response.choices[0].message.content)
    }
}
```

### 降级策略（可选）

```
主链路：Qwen3-VL-Plus (多菜品 + 全营养)
   ↓ 失败 / 超时 (3s)
降级 1：百度 v2/dish (单菜品 + 热量，成本兜底)
   ↓ 也失败
降级 2：本地 Room 营养库手动搜索
```

---

## 四、为什么选阿里百炼而不是混元/豆包

| | 阿里百炼 Qwen3-VL | 腾讯混元 | 字节豆包 |
|---|---|---|---|
| 视觉模型成熟度 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ |
| 中文食物识别实测 | Qwen 有 FoodLMM 学术基础 | 无公开 benchmark | 无专门食物评测 |
| JSON 结构化输出 | ✅ response_format | ✅ 支持 | ⚠️ 不稳定 |
| 免费额度 | 100 万 tokens | 10 万 tokens | 50 万 tokens |
| API 稳定性 | 99.9% SLA | 99.5% | 99.5% |
| 价格(图输入) | 1.5 元/千tokens | 未公开 | 未公开 |

Qwen-VL 在中文多模态领域有明确的学术积累（FoodLMM 就是复旦+阿里合作的成果），在食物识别这个细分场景上有先天优势。

---

## 五、总结：果断选多模态大模型

原来推荐百度专用 API 是因为没挖到更好的方案。现在看了完整版图：

| | 百度 v2/dish | 多模态大模型 |
|---|---|---|
| 开发速度 | 慢（要建营养库、份量估算） | **快**（Prompt 一次搞定全部） |
| 数据完整度 | 弱（只有热量/100g） | **强**（全营养素+重量+GI） |
| 多菜品支持 | 不支持 | **支持** |
| 成本 | 便宜 | 可接受（千 DAU 月费 5 元） |
| 演进能力 | 差（只能换 API） | **强**（改 Prompt 即可） |

**结论: 直接用阿里云百炼 Qwen3-VL-Plus，跳过百度。**

理由很简单：你做的不是"菜品识别"，是"饮食分析"。多模态大模型一步到位给出了用户真正关心的东西——这盘菜多重、多少卡、多少蛋白质。百度只给了个菜名和 100g 热量，中间差的环节全都是你要自己建的工程。

> **掌中灵 - 移动应用开发工程师 | 2026-07-03**
