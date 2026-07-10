# 智食 编码规范

> 版本: 1.0 | 更新: 2026-07-03

---

## 一、Kotlin 风格

### 基本规则

- 遵循 [官方 Kotlin 编码规范](https://kotlinlang.org/docs/coding-conventions.html)
- 缩进: 4 空格 (不用 Tab)
- 行宽: 建议 ≤120 字符
- 文件编码: UTF-8

### 命名

```kotlin
// 类: PascalCase
class MealRepositoryImpl

// 函数/属性: camelCase
fun analyzeFood(image: ByteArray): Result<FoodAnalysis>
val totalCalories: Float

// 常量: UPPER_SNAKE_CASE
const val MAX_IMAGE_SIZE = 512 * 1024L

// 伴生对象常量可放 companion object
companion object {
    const val DEFAULT_MODEL = "qwen3.6-plus"
}
```

### 表达式

```kotlin
// ✅ 优先 when 而非长 if-else
when (goalType) {
    GoalType.LOSE_FAT -> "减脂"
    GoalType.GAIN_MUSCLE -> "增肌"
    GoalType.MAINTAIN -> "维持"
}

// ✅ 优先 ?.let ?: 而非 if!=null
imagePath?.let { File(it) } ?: error("no image")
```

---

## 二、Compose 规范

### 组件结构

```kotlin
@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel(),
    onNavigateToResult: (ByteArray) -> Unit
) {
    // 1. 状态收集
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 2. LaunchedEffect / SideEffect
    LaunchedEffect(Unit) {
        viewModel.checkCameraPermission()
    }

    // 3. UI
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(modifier = Modifier.fillMaxSize())
        ShutterButton(
            onClick = { /* trigger capture */ },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
```

### State

```kotlin
// ✅ 用 remember/mutableStateOf 管理组件级状态
var isPressed by remember { mutableStateOf(false) }

// ✅ 用 ViewModel + StateFlow 管理页面级状态
val uiState by viewModel.uiState.collectAsStateWithLifecycle()

// ❌ 不要在 Composable 中直接发起网络请求
// ❌ 不要用全局可变变量管理 UI 状态
```

### Modifier 顺序

```kotlin
Modifier
    .fillMaxWidth()       // 1. 布局
    .padding(16.dp)       // 2. 间距
    .background(White)    // 3. 背景
    .border(1.dp, Gray)   // 4. 描边
    .clip(RoundedCorner(20.dp))  // 5. 裁剪 (在 onClick 前)
    .clickable { ... }    // 6. 交互 (最后)
```

### 预览

```kotlin
@Preview(showBackground = true, backgroundColor = 0xFFF9F2EF)
@Composable
private fun CameraScreenPreview() {
    EatmateTheme {
        CameraScreen(onNavigateToResult = {})
    }
}
```

---

## 三、Hilt 规范

```kotlin
// ✅ 构造函数注入
@HiltViewModel
class CameraViewModel @Inject constructor(
    private val repo: MealRepository
) : ViewModel()

// ✅ Module 中用 @Provides 提供第三方类
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()...
}

// ❌ 不要用 field injection
// ❌ 不要在 ViewModel 中 @Inject lateinit var
```

---

## 四、Room 规范

```kotlin
// ✅ DAO 方法用 suspend
@Dao
interface MealDao {
    @Insert
    suspend fun insert(meal: MealEntity): Long

    @Query("SELECT * FROM meal_records WHERE date = :date ORDER BY timestamp DESC")
    suspend fun getMealsByDate(date: String): List<MealEntity>
}

// ✅ 数据库迁移用 AutoMigration
@Database(
    entities = [MealEntity::class],
    version = 1,
    autoMigrations = []
)
abstract class AppDatabase : RoomDatabase()
```

---

## 五、协程规范

```kotlin
// ✅ ViewModel 中用 viewModelScope
viewModelScope.launch {
    val result = repo.analyze(image)
    _uiState.update { it.copy(result = result) }
}

// ✅ 需要切换线程时用 withContext
viewModelScope.launch {
    val compressed = withContext(Dispatchers.Default) {
        ImageCompressor.compress(bytes)
    }
    val result = repo.analyze(compressed)
    _uiState.update { it.copy(result = result) }
}

// ❌ 不要用 GlobalScope
// ❌ 不要在 Dispatchers.Main 上执行 IO
```

---

## 六、错误处理

```kotlin
// ✅ 使用 sealed class 表达结果
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
}

// ✅ Log 使用分类
Log.d(TAG, "Image compressed: ${bytes.size} bytes")
Log.e(TAG, "Analysis failed", exception)

// ❌ 不要 printStackTrace()
// ❌ 不要吞异常 (空的 catch)
```

---

## 七、提交规范

### Commit Message

```
<type>(<scope>): <简短描述>

类型: feat / fix / refactor / docs / style / chore
范围: camera / diary / theme / build / docs

例如:
feat(camera): add CameraX preview and shutter button
fix(theme): correct brand orange hex value
docs: add architecture spec and data model
```

### 每次提交前

```bash
./gradlew assembleDebug   # 必须通过
```
