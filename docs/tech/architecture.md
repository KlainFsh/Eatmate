# 智食 架构规范

> 版本: 1.0 | 更新: 2026-07-03

---

## 一、分层架构 (Clean Architecture)

```
┌────────────────────────────────────┐
│              UI Layer              │  Compose Screens + ViewModels
│  camera/ result/ diary/ profile/   │
├────────────────────────────────────┤
│            Domain Layer            │  UseCases + Repository Interfaces
│  AnalyzeFood / SaveMeal / GetPlan  │
├────────────────────────────────────┤
│             Data Layer             │  Repository Impl + Room + Retrofit
│  local/ remote/ repository/        │
└────────────────────────────────────┘
```

### 依赖方向

**UI → Domain → Data**

- UI 层依赖 Domain 层的 UseCase 和 Model
- Data 层实现 Domain 层的 Repository 接口
- Domain 层不依赖任何框架（纯 Kotlin）

---

## 二、包结构

```
com.example.eatmate
├── EatmateApp.kt                    # @HiltAndroidApp Application
│
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt           # Room 数据库
│   │   ├── dao/MealDao.kt          # 餐食记录 DAO
│   │   ├── dao/FoodDao.kt          # 营养数据库 DAO (Phase 3+)
│   │   └── entity/MealEntity.kt    # Room Entity
│   ├── remote/
│   │   ├── BailianVisionApi.kt     # Qwen API 接口
│   │   └── dto/ChatRequest.kt     # API DTO
│   └── repository/
│       ├── MealRepositoryImpl.kt
│       └── FoodAnalysisRepositoryImpl.kt
│
├── domain/
│   ├── model/
│   │   ├── Food.kt                 # 菜品 Domain Model
│   │   ├── NutritionInfo.kt        # 营养素
│   │   ├── Meal.kt                 # 餐食
│   │   └── UserGoal.kt             # 用户目标
│   ├── repository/
│   │   ├── MealRepository.kt        # 接口
│   │   └── FoodAnalysisRepository.kt
│   └── usecase/
│       ├── AnalyzeFoodUseCase.kt
│       └── SaveMealUseCase.kt
│
├── ui/
│   ├── camera/
│   │   ├── CameraScreen.kt
│   │   └── CameraViewModel.kt
│   ├── result/
│   │   ├── ResultScreen.kt
│   │   └── ResultViewModel.kt
│   ├── diary/
│   │   ├── DiaryScreen.kt
│   │   └── DiaryViewModel.kt
│   ├── profile/
│   │   ├── ProfileScreen.kt
│   │   └── ProfileViewModel.kt
│   ├── components/
│   │   └── ShutterButton.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       ├── Shape.kt
│       └── Type.kt
│
├── navigation/
│   ├── EatmateNavGraph.kt           # NavHost
│   └── Screen.kt                    # 路由定义
│
└── di/
    ├── AppModule.kt                  # 全局绑定
    ├── DatabaseModule.kt             # Room
    ├── NetworkModule.kt              # Retrofit
    └── RepositoryModule.kt           # Repository 绑定
```

---

## 三、命名约定

### Kotlin 文件

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| Activity | `XxxActivity` | `MainActivity` |
| Screen | `XxxScreen` | `CameraScreen` |
| ViewModel | `XxxViewModel` | `CameraViewModel` |
| UseCase | `XxxUseCase` (动词开头) | `AnalyzeFoodUseCase` |
| Repository 接口 | `XxxRepository` | `MealRepository` |
| Repository 实现 | `XxxRepositoryImpl` | `MealRepositoryImpl` |
| DAO | `XxxDao` | `MealDao` |
| Entity (Room) | `XxxEntity` | `MealEntity` |
| DTO (网络) | `XxxRequest` / `XxxResponse` | `ChatRequest` |
| Domain Model | 名词 | `Food`, `Meal` |

### Compose 组件

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| 页面级 | `XxxScreen` | `CameraScreen` |
| 可复用组件 | `Xxx` (名词, PascalCase) | `ShutterButton`, `NutritionChip` |

---

## 四、ViewModel 规范

```kotlin
@HiltViewModel
class CameraViewModel @Inject constructor(
    private val analyzeFoodUseCase: AnalyzeFoodUseCase,
    private val saveMealUseCase: SaveMealUseCase
) : ViewModel() {

    // UI 状态 — 单一 StateFlow
    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    // 用户事件 — 方法
    fun onTakePhoto(imageBytes: ByteArray) { ... }
    fun onConfirmResult(meal: Meal) { ... }
}

data class CameraUiState(
    val isCapturing: Boolean = false,
    val isAnalyzing: Boolean = false,
    val analysisResult: FoodAnalysis? = null,
    val error: String? = null
)
```

**规则**:
- 一个 ViewModel 只有一个 `_uiState`
- UI 层只用 `collectAsStateWithLifecycle()` 订阅
- 不在 ViewModel 中引用 Context 或 View

---

## 五、模块间通信

| 场景 | 方式 |
|------|------|
| Screen → ViewModel | 方法调用 |
| ViewModel → Screen | StateFlow |
| ViewModel → Repository | 构造函数注入 |
| Screen 间数据传递 | Navigation 的 SavedStateHandle 或共享 ViewModel |
| 跨页面事件 | 暂不需要 (Phase 1-3 用共享 ViewModel) |

---

## 六、错误处理策略

```
UI 层:    展示 Snackbar / 错误文案
ViewModel: catch 异常 → 更新 uiState.error
UseCase:  返回 Result<T> 或 throw
Repository: 返回 Result<T>
网络层:   统一拦截器处理 4xx/5xx → 抛自定义异常
```

```kotlin
// 标准错误处理模式
sealed class UiResult<out T> {
    data class Success<T>(val data: T) : UiResult<T>()
    data class Error(val message: String) : UiResult<Nothing>()
    data object Loading : UiResult<Nothing>()
}
```
