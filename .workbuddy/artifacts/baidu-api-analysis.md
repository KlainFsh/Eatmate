# 百度 AI 菜品识别 API 文档解读 & 智食集成建议

> 文档来源: [百度智能云 - 菜品识别](https://cloud.baidu.com/doc/IMAGERECOGNITION/s/tk3bcxbb0)
> 分析日期: 2026-07-03

---

## 一、API 核心参数速查

### 1.1 请求

| 项 | 值 |
|---|-----|
| **方法** | `POST` |
| **URL** | `https://aip.baidubce.com/rest/2.0/image-classify/v2/dish` |
| **认证** | `?access_token=xxx` (URL 参数，通过 API Key + Secret Key 换取，30 天有效) |
| **Content-Type** | `application/x-www-form-urlencoded` |

**关键参数**:

| 参数 | 必选 | 类型 | 说明 | 智食建议 |
|------|------|------|------|----------|
| `image` | 二选一 | Base64 string | 图片，base64 ≤4M，最长边 ≤4096px | ✅ 用这个，CameraX 拍完直接编码 |
| `url` | 二选一 | string | 图片 URL，≤1024 字节 | ❌ 不推荐，多一次上传 |
| `top_num` | 否 | int | 返回 Top-N 结果，默认 5 | 设为 **3** 足够（减少带宽） |
| `filter_threshold` | 否 | float | 置信度阈值，默认 0.95 | 设为 **0.7**（外卖/食堂场景识别率会下降） |
| `baike_num` | 否 | int | 返回百科信息条数 | 可设为 **1**（展示菜品背景） |

### 1.2 响应

```json
{
  "log_id": 7357081719365269362,
  "result_num": 5,
  "result": [{
    "calorie": "119",         // ⚠️ 注意：是 String，不是 Number！
    "has_calorie": true,      // 是否有热量数据
    "name": "酸汤鱼",          // 菜名
    "probability": "0.396",   // ⚠️ 注意：也是 String！
    "baike_info": { ... }     // 百科信息（可选）
  }]
}
```

---

## 二、对智食的关键影响分析

### 2.1 ⚠️ 致命限制：只支持单菜品识别

**这是文档里没有明说但 API 行为上最大的限制。** 标准版 `v2/dish` 是为**单盘菜**拍照设计的，如果你拍一张满桌子三四道菜的照片，效果会非常差——它只会给出一个置信度最高的菜名。

**智食的核心场景恰恰是多菜品场景：外卖套餐、食堂餐盘、聚餐。** 这对你的产品体验是致命的。

#### 解决方案对比

| 方案 | 说明 | 可行性 |
|------|------|--------|
| **A: 百度自定义菜品识别** | 上传菜品图片训练模型，支持多菜品+位置检测 | ⭐⭐⭐⭐⭐ |
| B: 引导用户单菜品拍照 | 交互设计上要求用户逐盘拍 | ⭐⭐ (体验差) |
| C: 多次裁剪+并发请求 | 手机端先做目标检测切割,再逐张发请求 | ⭐⭐⭐ (技术复杂) |
| D: 换用多模态大模型 | 腾讯混元 / 通义千问 Vision 版 | ⭐⭐⭐ (成本高、不稳定) |

> **建议：标准 v2/dish 只作为 MVC 和初始冷启动使用。正式版务必升级到"自定义菜品识别"API，支持多菜品 + 定位检测。**

**自定义菜品识别 API**: `POST /rest/2.0/image-classify/v2/advanced_general` 或专门的 `custom_dish` 接口，支持：
- 一张照片识别多个菜品
- 返回每个菜品在图中的位置（bounding box）
- 支持上传自有菜品图片扩充模型

### 2.2 ⚠️ 热量数据的不确定性

```json
"calorie": "119"        // 每 100g 的卡路里
"has_calorie": true     // 但可能为 false
```

三个问题：

1. **类型陷阱**: `calorie` 是 `"119"` (String) 不是 `119` (Number)，需要用 `toFloat()` 解析
2. **可能为空**: `has_calorie` 可能是 `false`，9000 种菜品并非全有热量数据
3. **没有份量**: 只给每 100g 的热量，不估算这盘菜的实际克数

#### 需要补全的数据链路

```
百度 API 返回  → 热量/100g + 菜名
       ↓
本地 Room 营养库 → 蛋白质、碳水、脂肪 (按100g)
       ↓
份量估算模块   → 估算实际克数 (最核心但没有现成方案的问题)
       ↓
计算总营养素   → (实际克数 / 100) × 营养/100g
```

### 2.3 ⚠️ filter_threshold 的取舍

默认 `filter_threshold = 0.95` 是在**保证准确度**，但如果降到 0.7：
- ✅ 能识别更多菜品（外卖混合菜等）
- ❌ 误识别率上升

**建议**: MVP 阶段设 `0.85`，外加一个 **"结果不对？手动修正"** 的入口，把准确率和覆盖率平衡掉。

### 2.4 ⚠️ access_token 管理

- access_token 通过 API Key + Secret Key 向 `https://aip.baidubce.com/oauth/2.0/token` 换取
- **有效期 30 天**，到期需重新获取
- SDK 示例里每次请求都重新获取（浪费），实际需要缓存

**Android 端方案**: 用 `DataStore` 缓存 token + 过期时间，请求前判断是否过期，过期则先刷新。

---

## 三、Android 集成关键代码

### 3.1 依赖引入

百度官方 Java SDK 已经是古早产物了（不是 Android 原生），**建议自己封装 Retrofit 调用**，不依赖官方 SDK。

```kotlin
// 自己封装比官方 SDK 更干净
interface BaiduDishApi {
    @POST("rest/2.0/image-classify/v2/dish")
    @FormUrlEncoded
    suspend fun recognizeDish(
        @Query("access_token") token: String,
        @Field("image") imageBase64: String,
        @Field("top_num") topNum: Int = 3,
        @Field("filter_threshold") threshold: Float = 0.85f,
        @Field("baike_num") baikeNum: Int = 1
    ): DishRecognitionResponse
}

// 响应 DTO（注意 String 类型）
@Serializable
data class DishRecognitionResponse(
    val log_id: Long,
    val result_num: Int,
    val result: List<DishResult>
)

@Serializable
data class DishResult(
    val name: String,
    val calorie: String,       // ⚠️ String, 需要 toFloat()
    val has_calorie: Boolean,
    val probability: String,   // ⚠️ String, 需要 toFloat()
    val baike_info: BaikeInfo? = null
)
```

### 3.2 Token 管理器

```kotlin
@Singleton
class BaiduTokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore
    
    suspend fun getAccessToken(): String {
        val cached = dataStore.data.first()
        val token = cached[TOKEN_KEY] ?: ""
        val expiresAt = cached[TOKEN_EXPIRES_KEY] ?: 0L
        
        // 提前 1 天刷新，避免临界过期
        if (token.isNotEmpty() && System.currentTimeMillis() < expiresAt - 86400000) {
            return token
        }
        return refreshToken()
    }
    
    private suspend fun refreshToken(): String {
        // POST https://aip.baidubce.com/oauth/2.0/token
        // grant_type=client_credentials&client_id=API_KEY&client_secret=SECRET_KEY
        val response = authApi.getToken("client_credentials", API_KEY, SECRET_KEY)
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = response.access_token
            prefs[TOKEN_EXPIRES_KEY] = System.currentTimeMillis() + response.expires_in * 1000
        }
        return response.access_token
    }
}
```

### 3.3 图片处理流水线

```kotlin
// 拍照 → 压缩 → Base64 → URLEncode → 发送
class ImagePreprocessor @Inject constructor() {
    
    fun prepareForApi(imageBytes: ByteArray): String {
        // 1. 解码
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        
        // 2. 缩放（最长边 ≤ 1024px，远小于 API 的 4096px 限制）
        val resized = resizeBitmap(bitmap, maxEdge = 1024)
        
        // 3. 压缩为 JPEG Q=80
        val output = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 80, output)
        val compressed = output.toByteArray()
        
        // 4. Base64 编码（去掉 data:image 前缀）+ URLEncode
        val base64 = Base64.encodeToString(compressed, Base64.NO_WRAP)
        return URLEncoder.encode(base64, "UTF-8")
    }
}
```

---

## 四、风险矩阵与兜底策略

| 风险 | 触发条件 | 概率 | 影响 | 兜底 |
|------|----------|------|------|------|
| 多菜品照片只识别一个 | 用户拍外卖/餐盘 | 🔴 高 | 记录不完整 | 升级到自定义菜品识别 API |
| `has_calorie=false` | 冷门菜品 | 🟡 中 | 无热量数据 | 降级本地 Room 营养库匹配 |
| 识别结果置信度低 | 混合菜/灯光暗 | 🟡 中 | 结果不准确 | 展示多候选 + 手动修正入口 |
| access_token 过期 | 30 天未使用/网络故障 | 🟠 低 | API 不可用 | DataStore 缓存 + 自动刷新 |
| API QPS 超限 | 突发流量 | 🟠 低 | 429 限流 | Retrofit 重试 + 退避策略 |
| 无营养素(蛋白/碳水/脂肪) | 所有请求 | 🔴 确定 | 功能不完整 | **必须**配本地 Room 营养库 |

---

## 五、修正后的核心链路设计

```
CameraX 拍照
    │
    ├── 图片预处理 (max 1024px, JPEG Q=80, ≤200KB)
    │
    ├── Base64 + URLEncode
    │
    ├── POST v2/dish (filter_threshold=0.85, top_num=3)
    │
    ▼
百度 API 返回 {name, calorie(per 100g), has_calorie, probability}
    │
    ├── has_calorie=true?
    │   ├── YES → 使用 calorie 值
    │   └── NO  → 匹配本地 Room 营养库
    │
    ├── probability > 0.7?
    │   ├── YES → 直接展示结果
    │   └── NO  → 展示多候选 + "选一个正确的" 交互
    │
    ├── 本地 Room 匹配蛋白/碳水/脂肪(per 100g)
    │
    ├── 用户确认/估算份量
    │
    └── 计算总营养素 → 展示 + 膳食建议
```

---

## 六、总结：该做什么、不该做什么

### ✅ 该做

1. **立即申请自定义菜品识别 API**（不是标准 v2/dish），这是多菜品场景的刚需
2. **建立本地 Room 营养数据库**，补全蛋白/碳水/脂肪数据
3. **设计份额估算交互**（"这盘大概几两？"），这是卡路里准确度的命门
4. **自己封装 Retrofit 而非依赖官方 Java SDK**
5. **DataStore 缓存 access_token**，带自动刷新逻辑

### ❌ 不该做

1. **不要直接依赖标准 v2/dish 作为唯一方案**——单菜品限制会严重影响体验
2. **不要假设 calorie 字段总是存在**——必须用 `has_calorie` 做判断
3. **不要忽略 String 类型陷阱**——`calorie` 和 `probability` 是字符串不是数字
4. **不要每次请求都获取新 token**——30 天有效期，缓存复用
5. **不要直接把 CameraX 原图上传**——必须先压缩到 1024px 以下

---

> **掌中灵 - 移动应用开发工程师 | 2026-07-03**
