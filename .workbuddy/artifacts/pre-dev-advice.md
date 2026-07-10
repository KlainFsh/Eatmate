# 智食 (Eatmate) — 开发前全面评估与建议

> **评估日期**: 2026-07-03 | **现有项目状态**: Android 基础模板 | **目标平台**: Android

---

## 一、现有项目诊断

### 1.1 项目现状

| 项目 | 当前配置 | 建议 | 优先级 |
|------|----------|------|--------|
| **minSdk** | 30 (Android 11) | 降为 **26 (Android 8.0)** | 🔴 高 |
| **compileSdk** | 36 | ✅ 保持 | - |
| **Kotlin** | 2.2.10 | ✅ 保持 | - |
| **Compose BOM** | 2026.02.01 | ✅ 保持 | - |
| **AGP** | 9.2.1 | ✅ 保持 | - |
| **DI** | 无 | 引入 **Hilt** | 🔴 高 |
| **导航** | 无 | 引入 **Navigation Compose** | 🔴 高 |
| **网络层** | 无 | 引入 **Retrofit + OkHttp + Kotlinx.Serialization** | 🔴 高 |
| **数据库** | 无 | 引入 **Room** | 🔴 高 |
| **图片加载** | 无 | 引入 **Coil** (Compose 原生) | 🟡 中 |
| **主题方案** | 默认 M3 (Dynamic Color) | 改为**品牌色方案** (健康/活力) | 🟡 中 |

### 1.2 minSdk 分析与建议

> **minSdk = 30 意味着放弃了中国市场约 15-20% 的 Android 用户。** 对于一款面向白领/学生的 C 端应用，这是不可接受的。

| minSdk | Android 版本 | 覆盖中国用户 | 风险 |
|--------|-------------|-------------|------|
| 30 | Android 11+ | ~80% | 丢失老旧设备用户 |
| **26** | **Android 8.0+** | **~97%** | 安全、CameraX 均支持 |
| 24 | Android 7.0+ | ~99% | 部分 API 需兼容处理 |

**建议: `minSdk = 26`**，CameraX、Room、WorkManager、Biometric API 等核心依赖均支持，覆盖率最优。

---

## 二、架构设计

### 2.1 整体架构: MVVM + Clean Architecture

```
┌─────────────────────────────────────────────┐
│                   UI Layer                   │
│  ┌──────────┐ ┌──────────┐ ┌─────────────┐  │
│  │  Camera  │ │  Result  │ │  FoodDiary  │  │
│  │  Screen  │ │  Screen  │ │   Screen    │  │
│  └────┬─────┘ └────┬─────┘ └──────┬──────┘  │
│       │            │              │         │
│  ┌────┴────────────┴──────────────┴──────┐  │
│  │           ViewModels                   │  │
│  │   (StateFlow<UiState>, user events)   │  │
│  └────────────────┬──────────────────────┘  │
├───────────────────┼─────────────────────────┤
│              Domain Layer                    │
│  ┌────────────────┴──────────────────────┐  │
│  │   UseCases / Domain Services           │  │
│  │   AnalyzeFood / SaveMeal / GetPlan     │  │
│  └────────────────┬──────────────────────┘  │
│  ┌────────────────┴──────────────────────┐  │
│  │   Repository Interfaces (contracts)   │  │
│  └────────────────┬──────────────────────┘  │
├───────────────────┼─────────────────────────┤
│                Data Layer                    │
│  ┌──────────┐     ┌──────────────────────┐  │
│  │  Room DB │     │  Remote API           │  │
│  │ (本地营养 │     │  (AI 图片识别服务)     │  │
│  │  数据库)  │     │   Retrofit + OkHttp  │  │
│  └──────────┘     └──────────────────────┘  │
└─────────────────────────────────────────────┘
```

### 2.2 模块划分

```
app/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt          # Room 数据库
│   │   ├── dao/
│   │   │   ├── MealDao.kt          # 餐食记录
│   │   │   ├── FoodDao.kt          # 食物营养库
│   │   │   └── UserProfileDao.kt   # 用户档案
│   │   ├── entity/
│   │   │   ├── MealEntity.kt
│   │   │   ├── FoodEntity.kt
│   │   │   ├── NutritionEntity.kt
│   │   │   └── UserProfileEntity.kt
│   │   └── preload/
│   │       └── FoodDatabaseFiller.kt  # 预装中华美食营养库
│   ├── remote/
│   │   ├── ApiService.kt           # Retrofit API 接口
│   │   ├── dto/
│   │   │   ├── FoodRecognitionRequest.kt
│   │   │   ├── FoodRecognitionResponse.kt
│   │   │   └── MealPlanResponse.kt
│   │   └── interceptor/
│   │       └── AuthInterceptor.kt
│   ├── repository/
│   │   ├── FoodRecognitionRepositoryImpl.kt
│   │   ├── MealRepositoryImpl.kt
│   │   └── UserRepositoryImpl.kt
│   └── mapper/
│       ├── FoodMapper.kt            # Entity ↔ Domain Model
│       └── MealMapper.kt
│
├── domain/
│   ├── model/
│   │   ├── Food.kt
│   │   ├── NutritionInfo.kt         # 热量、蛋白质、碳水、脂肪
│   │   ├── Meal.kt                  # 单餐记录
│   │   ├── DailyDiet.kt             # 日饮食汇总
│   │   └── UserGoal.kt              # 减脂/增肌目标
│   ├── repository/
│   │   ├── FoodRecognitionRepository.kt  # 接口
│   │   ├── MealRepository.kt
│   │   └── UserRepository.kt
│   └── usecase/
│       ├── AnalyzeFoodUseCase.kt     # 核心：拍照识别 + 营养分析
│       ├── SaveMealUseCase.kt        # 记录一餐
│       ├── GetDailySummaryUseCase.kt # 日营养汇总
│       ├── GenerateMealPlanUseCase.kt# 一键定制三餐
│       └── UpdateUserGoalUseCase.kt  # 更新目标
│
├── ui/
│   ├── camera/
│   │   ├── CameraScreen.kt          # 拍照界面
│   │   ├── CameraViewModel.kt
│   │   └── components/
│   │       ├── CameraPreview.kt      # CameraX 预览
│   │       ├── ShutterButton.kt
│   │       └── AnalysisOverlay.kt    # 扫描动画
│   ├── result/
│   │   ├── ResultScreen.kt          # 识别结果页
│   │   ├── ResultViewModel.kt
│   │   └── components/
│   │       ├── NutritionCard.kt      # 营养数据卡片
│   │       ├── DishItem.kt           # 单菜品展示
│   │       └── AdviceSection.kt      # 膳食优化建议
│   ├── diary/
│   │   ├── DiaryScreen.kt           # 饮食日记
│   │   ├── DiaryViewModel.kt
│   │   └── components/
│   │       ├── MealTimeline.kt       # 三餐时间线
│   │       ├── NutritionRing.kt      # 营养环形图
│   │       └── DaySummary.kt         # 日汇总卡片
│   ├── plan/
│   │   ├── PlanScreen.kt            # 三餐定制
│   │   └── PlanViewModel.kt
│   ├── profile/
│   │   ├── ProfileScreen.kt         # 个人资料与目标
│   │   └── ProfileViewModel.kt
│   └── theme/
│       ├── Color.kt                 # 品牌色 (健康绿 + 暖橙)
│       ├── Type.kt                  # 字体排版
│       ├── Shape.kt                 # 圆角风格
│       └── Theme.kt                 # M3 主题配置
│
├── navigation/
│   ├── EatmateNavGraph.kt           # 导航图
│   └── Screen.kt                    # 路由定义
│
├── di/
│   ├── AppModule.kt                 # Hilt 全局模块
│   ├── DatabaseModule.kt            # Room 提供
│   ├── NetworkModule.kt             # Retrofit 提供
│   └── RepositoryModule.kt          # Repository 绑定
│
└── util/
    ├── ImageCompressor.kt           # 图片压缩工具
    ├── NutritionCalculator.kt       # 营养素计算器
    └── DateUtils.kt
```

---

## 三、关键依赖选型

### 3.1 推荐引入的核心库

```toml
# gradle/libs.versions.toml 新增部分

[versions]
# 现有版本保持不变...

# --- 新增依赖版本 ---
hilt = "2.53.1"
navigationCompose = "2.9.0"
retrofit = "2.11.0"
okhttp = "4.12.0"
kotlinxSerialization = "1.7.3"
room = "2.7.0"
coil = "3.1.0"
cameraX = "1.5.0"
datastore = "1.1.3"
workManager = "2.10.0"

[libraries]
# --- DI ---
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.2.0" }

# --- Navigation ---
navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }

# --- Network ---
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-kotlinx-serialization = { group = "com.squareup.retrofit2", name = "converter-kotlinx-serialization", version.ref = "retrofit" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerialization" }

# --- Database ---
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }

# --- Image ---
coil-compose = { group = "io.coil-kt.coil3", name = "coil-compose", version.ref = "coil" }

# --- Camera ---
camerax-core = { group = "androidx.camera", name = "camera-core", version.ref = "cameraX" }
camerax-camera2 = { group = "androidx.camera", name = "camera-camera2", version.ref = "cameraX" }
camerax-lifecycle = { group = "androidx.camera", name = "camera-lifecycle", version.ref = "cameraX" }
camerax-view = { group = "androidx.camera", name = "camera-view", version.ref = "cameraX" }

# --- DataStore ---
datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }

# --- WorkManager ---
work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "workManager" }

# plugins 新增
hilt-plugin = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version = "2.2.10-1.0.29" }
```

### 3.2 选型理由

| 能力 | 选型 | 理由 |
|------|------|------|
| **DI** | Hilt | Google 官方推荐，与 Jetpack 生态深度集成 |
| **导航** | Navigation Compose | 类型安全、支持深层链接 |
| **网络** | Retrofit + Kotlinx Serialization | 轻量、无需 annotation processor |
| **本地存储** | Room + DataStore | Room 管理结构化数据（营养库），DataStore 管理偏好 |
| **图片加载** | Coil 3 | 原生 Compose 支持、轻量、支持预缓存 |
| **相机** | CameraX | 官方 Jetpack 库、生命周期感知、兼容性最好 |
| **后台任务** | WorkManager | 定时同步数据、营养库更新 |

---

## 四、核心模块设计要点

### 4.1 AI 食物识别流程 (最核心模块)

```
用户拍照 → 图片压缩(≤512KB) → 上传至 AI 识别 API
    → 返回菜品名称+置信度 → 本地营养库匹配
    → 计算热量/蛋白质/碳水/脂肪 → 展示结果 + 膳食建议
```

**关键设计决策**:

1. **云端识别 vs 端侧推理**: 
   - 现阶段建议**云端 API**，中餐菜品上千种，端侧模型部署复杂
   - 后续可考虑将常用菜品模型通过 ML Kit 本地部署，缩短延迟

2. **图片压缩策略**:
   - CameraX 拍照后用 `ImageCompressor` 压缩至最大边 ≤1024px，JPEG 质量 80%
   - 目标: 每张照片 ≤512KB，确保 4G 网络下 3 秒内上传

3. **离线兜底策略**:
   - 网络不可用时 → 提示用户手动搜索 + 加入离线队列
   - 恢复后自动重试上传识别

```kotlin
// 核心 UseCase 设计
class AnalyzeFoodUseCase @Inject constructor(
    private val foodRecognitionRepo: FoodRecognitionRepository,
    private val foodDatabaseRepo: FoodRepository,
    private val nutritionCalculator: NutritionCalculator,
    private val userRepo: UserRepository
) {
    suspend operator fun invoke(imageBytes: ByteArray): Result<FoodAnalysis> {
        // 1. 压缩图片
        val compressed = imageCompressor.compress(imageBytes)
        
        // 2. 调用 AI 识别 API
        val recognitionResult = foodRecognitionRepo.recognizeFood(compressed)
        
        // 3. 匹配本地营养库
        val nutritionInfo = foodDatabaseRepo.getNutrition(
            recognitionResult.dishName
        ) ?: foodRecognitionRepo.getNutritionFromApi(recognitionResult.dishName)
        
        // 4. 结合用户目标生成建议
        val userGoal = userRepo.getCurrentGoal()
        val advice = nutritionCalculator.generateAdvice(nutritionInfo, userGoal)
        
        return Result.success(
            FoodAnalysis(
                dishName = recognitionResult.dishName,
                confidence = recognitionResult.confidence,
                nutrition = nutritionInfo,
                advice = advice
            )
        )
    }
}
```

### 4.2 营养数据库设计 (本地 Room)

```kotlin
@Entity(tableName = "food_nutrition")
data class FoodEntity(
    @PrimaryKey val id: Long,
    val name: String,              // 菜品名称 (如 "宫保鸡丁")
    val alias: String?,            // 别名 (JSON array)
    val category: String,          // 分类: 荤菜/素菜/主食/汤品/小吃
    val caloriesPer100g: Float,    // 每100g 热量(kcal)
    val proteinPer100g: Float,     // 每100g 蛋白质(g)
    val carbPer100g: Float,        // 每100g 碳水(g)
    val fatPer100g: Float,         // 每100g 脂肪(g)
    val typicalPortion: Float,     // 典型份量(g) 
    val typicalPortionName: String,// "一份" / "一碟" / "一个"
    val imageUrl: String?,         // 菜品示例图片URL
    val tags: String?              // JSON: ["高蛋白","低脂","川菜"]
)
```

> 需要预装 **3000+ 道中华美食**的营养数据。数据来源可从权威营养数据库获取后本地打包。

### 4.3 餐食记录模型

```kotlin
@Entity(tableName = "meal_record")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,              // "breakfast" / "lunch" / "dinner" / "snack"
    val date: String,              // "2026-07-03"
    val timestamp: Long,
    val imagePath: String?,        // 本地照片路径
    val totalCalories: Float,
    val totalProtein: Float,
    val totalCarb: Float,
    val totalFat: Float,
    val dishes: String             // JSON: [{foodId, name, portion, calories, ...}]
)
```

### 4.4 CameraX 集成方案

```kotlin
// CameraScreen 中的 CameraX 配置要点:
// 1. 使用 PreviewView (Compose 互操作)
// 2. ImageCapture 设置为 JPEG 格式
// 3. 拍照后立即压缩，不等待用户确认
// 4. 自动闪光灯检测（环境光不足时自动开启）
```

### 4.5 用户目标系统

```kotlin
data class UserGoal(
    val type: GoalType,            // LOSE_FAT / GAIN_MUSCLE / MAINTAIN
    val dailyCalorieTarget: Int,   // 每日目标热量
    val proteinTarget: Float,      // 蛋白质目标(g)
    val carbTarget: Float,         // 碳水目标(g)
    val fatTarget: Float,          // 脂肪目标(g)
    val targetWeight: Float,       // 目标体重(kg)
    val weeklyGoal: Float          // 每周减脂/增重目标(kg)
)
```

---

## 五、性能与体验策略

### 5.1 首屏加载优化

| 指标 | 目标值 | 策略 |
|------|--------|------|
| 冷启动 | < 2.0s | 延迟初始化非关键服务、Splash Screen API |
| 拍照→结果展示 | < 3.0s | 图片压缩 + API 超时 5s + 骨架屏 |
| 日记录页面加载 | < 500ms | Room 索引优化、分页加载 |

### 5.2 图片处理流水线

```
CameraX 拍摄 (原始尺寸) 
    → 内存中压缩 (max 1024px, JPEG Q=80)
    → 持久化到 app 内部存储 (用于后续查看)
    → 上传到 AI API (最多重试 2 次)
    → 成功后删除本地临时文件
```

### 5.3 离线策略

| 功能 | 在线 | 离线 |
|------|------|------|
| AI 拍照识别 | ✅ API 识别 | ❌ 提示无网络，可手动搜索 |
| 营养库查询 | ✅ 在线+本地缓存 | ✅ 本地 Room 数据库 |
| 餐食记录 | ✅ 同步云端 | ✅ 本地存储，队列同步 |
| 饮食日记 | ✅ 全量数据 | ✅ 本地历史数据 |
| 三餐定制 | ✅ AI 生成 | ✅ 本地预设模板 |
| 用户档案 | ✅ 云端同步 | ✅ DataStore 本地 |
| 营养库更新 | ✅ 增量同步 | ❌ 待恢复 |

### 5.4 内存与电池优化

- 相机使用时避免 OOM：及时释放 Bitmap、使用 `inBitmap` 复用
- 营养库首次加载用预打包数据库（assets 直接拷贝，1-5MB）
- 网络请求合并和缓存：OkHttp Cache 10MB
- WorkManager 批量同步，间隔 ≥3小时

---

## 六、UI/UX 设计建议

### 6.1 品牌色方案 (健康 & 活力)

```kotlin
// 替换现有的 Purple 色板
// 主色 - 健康绿 (信任感、自然)
val PrimaryGreen = Color(0xFF4CAF50)
val PrimaryGreenDark = Color(0xFF388E3C)
val PrimaryGreenLight = Color(0xFFC8E6C9)

// 辅助色 - 暖橙 (活力、食欲)
val AccentOrange = Color(0xFFFF7043)
val AccentOrangeLight = Color(0xFFFFCCBC)

// 语义色 - 卡路里警示
val CalorieRed = Color(0xFFE53935)
val CalorieYellow = Color(0xFFFDD835)
val CalorieGreen = Color(0xFF43A047)

// 背景
val BackgroundWarm = Color(0xFFFAFAF5)  // 温暖米白
val SurfaceCard = Color(0xFFFFFFFF)
```

### 6.2 信息架构

```
Bottom Navigation (3 Tab):
├── 📷 拍照识别   (首页, 核心入口)
├── 📊 饮食日记   (历史记录)
└── 👤 我的        (目标/计划/设置)
```

### 6.3 关键交互设计

- **拍照页**: 全屏 CameraX 预览 + 底部快门按钮 + 美食框辅助线
- **结果页**: 营养环形图(热量为主视觉) + 菜品列表 + 膳食建议卡片
- **日记页**: 时间线条目 + 日汇总顶部卡片 + 周/月趋势图

### 6.4 Material Design 3 适配

根据 Skill 中的 M3 指南，智食属于 **Health/Wellness → 自然平静** 风格:
- 圆角组件 (24dp Medium)
- 柔和阴影
- 有机形状
- 温暖配色

---

## 七、中国市场需求适配

### 7.1 必须适配的特性

| 需求 | 方案 |
|------|------|
| **中餐菜品数据库** | Room 预装 3000+ 道中餐营养数据 |
| **外卖场景** | 支持拍照识别外卖菜品（混合菜品识别） |
| **食堂/校园餐** | 支持按窗口/档口预设菜品 |
| **计量单位** | 克(g)、千卡(kcal)为主，辅以"份/碟/碗" |
| **中国主流登录** | 微信登录 (WeChat SDK) + 手机号 |
| **国内推送** | 接入小米/华为/OPPO/vivo 厂商推送通道 |
| **应用市场** | 华为应用市场、小米应用商店、OPPO软件商店等 |

### 7.2 合规要点

- **《个人信息保护法》**: 相机权限使用说明、隐私政策
- **《数据安全法》**: 用户健康数据本地加密存储
- **《网络安全法》**: 用户实名认证方案
- 自启动权限说明（如使用厂商推送）

---

## 八、开发阶段建议

### Phase 1 — MVP (4-6 周)
- [ ] 项目配置: minSdk 降为 26、Hilt 集成、依赖引入
- [ ] 基础架构: MVVM 骨架、导航图、主题配置
- [ ] CameraX 拍照模块 (基础拍照 + 预览)
- [ ] AI 识别模块 (对接一个云 API，如百度AI/腾讯云)
- [ ] 营养计算结果展示页 (基础 UI)
- [ ] Room 数据库 + 基础营养数据 (500 道常见菜品)
- [ ] 餐食记录保存与查询

### Phase 2 — 核心完善 (4-6 周)
- [ ] 营养成分详细展示 (环形图、营养素分项)
- [ ] 膳食建议引擎 (结合用户目标)
- [ ] 饮食日记页面 (日视图)
- [ ] 用户目标系统 (减脂/增肌)
- [ ] 图片压缩优化
- [ ] 离线队列与同步
- [ ] 完整营养数据库 (3000+ 菜品)

### Phase 3 — 辅助功能 + 优化 (4 周)
- [ ] 一键定制三餐
- [ ] 周/月趋势分析
- [ ] 厂商推送集成
- [ ] 微信登录
- [ ] 启动性能优化
- [ ] 内测与灰度发布

---

## 九、风险与注意事项

| 风险 | 等级 | 应对策略 |
|------|------|----------|
| **AI 识别准确率不足** | 🔴 高 | 先选定一个成熟的中餐识别 API；准备手动修正入口 |
| **营养数据库不完整** | 🟡 中 | 优先覆盖高频菜品；开放用户贡献+审核机制 |
| **拍照上传速度慢** | 🟡 中 | 强力压缩策略；WiFi/4G 自适应 |
| **竞品入局** | 🟡 中 | 聚焦"3秒识菜"差异化，建立中餐识别壁垒 |
| **云 API 成本** | 🟠 低 | 初期选免费额度大的服务商；逐步自建模型 |
| **厂商推送接入复杂** | 🟠 低 | MVP 先用 FCM，后面再逐步接入各厂商 |

---

## 十、总结 —— 立即行动清单

1. **立刻修改 `minSdk = 26`**，确保用户覆盖
2. **引入 Hilt + Navigation Compose + Room + Retrofit + CameraX** 五件套
3. **确定 AI 识别服务商** (建议对比百度AI菜品识别、腾讯云图像分析、阿里云视觉智能)
4. **准备中餐营养数据库** (至少 500 道 MVP 用)
5. **设计品牌视觉方案** (健康绿 + 暖橙)
6. **搭建 Clean Architecture 骨架代码**

---

> **掌中灵 - 移动应用开发工程师 | 2026-07-03**
> 项目已具备良好的起点，关键在于核心识别链路的打磨和营养数据的积累。
> 建议 MVP 先跑通"拍照→识别→分析→建议"这条核心闭环，再逐步完善辅助功能。
