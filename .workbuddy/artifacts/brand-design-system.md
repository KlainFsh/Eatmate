# 智食 (Eatmate) 品牌视觉系统 v2.0

> 设计灵感: 插画风柔色板 — 暖白 + 嫩绿 + 蜜橙
> 设计日期: 2026-07-03
> 适用版本: 全量替换 v1.0 的"健康绿+暖橙"硬绿方案

---

## 一、设计理念

### 从"健身App"到"治愈App"

| v1.0 (废弃) | v2.0 (本方案) |
|---|---|
| 冷硬绿 #4CAF50 + 工业橙 | **暖蜜橙 + 嫩绿 + 暖白** |
| 工具感强、专业度高 | **温柔治愈、生活感强** |
| 适合健身/医疗App | **适合饮食/生活/减脂App** |

**为什么改**: 智食面向"白领/学生减脂"，用户的痛点是"想好好吃饭，不是节食"。产品的情绪基调应该是**鼓励、温柔、不焦虑**。硬绿+橙更像医院/工具，太严肃了。

### 核心情绪

- 🧡 温柔（蜜橙）
- 🌱 新生（嫩绿）
- 🤍 干净（暖白）
- ☁️ 安静（天空蓝作信息色）

---

## 二、完整色板

### 2.1 主色板

| 角色 | 色值 | 说明 |
|------|------|------|
| **Brand Primary** | `#F9BC53` | 蜜橙 — 主按钮、行动元素 |
| **Brand Secondary** | `#D2E0AA` | 嫩绿 — 强调健康、新鲜 |
| **Brand Surface** | `#F9F2EF` | 暖白 — 页面背景 |
| **Brand Info** | `#ABD7FB` | 天空蓝 — 信息提示 |
| **Brand Warm** | `#FCCEB4` | 蜜桃 — 卡片高亮、插画辅助 |

### 2.2 语义色

| 角色 | 色值 | 用途 |
|------|------|------|
| **Healthy** | `#D2E0AA` | 健康饮食推荐 ✅ |
| **Warning** | `#F9BC53` | 热量超标 ⚠️ |
| **Danger** | `#E78A8A` | 严重超标（柔和红，非正红） |
| **Protein** | `#C8B6E2` | 蛋白质（柔紫） |
| **Carb** | `#FFD89A` | 碳水（柔米黄） |
| **Fat** | `#FFB4A2` | 脂肪（柔珊瑚） |
| **Success** | `#B5C99A` | 成功提示 |

### 2.3 中性色（基于暖白基底）

| 角色 | 色值 |
|------|------|
| **Background** | `#F9F2EF`（暖白基底） |
| **Surface** | `#FFFFFF`（纯白卡片） |
| **Surface Variant** | `#F4EAE3`（次级卡片） |
| **Outline** | `#E5D5C7`（淡暖灰描边） |
| **Text Primary** | `#3D2E1F`（暖深棕） |
| **Text Secondary** | `#7A6650`（暖中棕） |
| **Text Disabled** | `#B9A998` |

### 2.4 暗色模式

| 角色 | 色值 |
|------|------|
| **Background** | `#1F1812`（深暖棕） |
| **Surface** | `#2A2018` |
| **Surface Variant** | `#382A1E` |
| **Brand Primary** | `#F9BC53`（保持不变） |
| **Brand Secondary** | `#D2E0AA`（保持不变） |
| **Text Primary** | `#F9F2EF` |
| **Text Secondary** | `#B9A998` |

> 主色在暗色下不调整，因为蜜橙和嫩绿本身亮度足够，反差自然够用。

---

## 三、Color.kt 实现

```kotlin
// app/src/main/java/com/example/eatmate/ui/theme/Color.kt
package com.example.eatmate.ui.theme

import androidx.compose.ui.graphics.Color

// === Brand 主色 ===
val BrandOrange = Color(0xFFF9BC53)        // 蜜橙
val BrandGreen = Color(0xFFD2E0AA)         // 嫩绿
val BrandInfo = Color(0xFFABD7FB)          // 天空蓝
val BrandPeach = Color(0xFFFCCEB4)         // 蜜桃
val BrandWarm = Color(0xFFF9F2EF)          // 暖白

// === 语义色 ===
val HealthyGreen = Color(0xFFB5C99A)       // 健康提示
val WarningOrange = Color(0xFFF9BC53)      // 警告（=主色）
val DangerRed = Color(0xFFE78A8A)          // 柔和红
val ProteinPurple = Color(0xFFC8B6E2)      // 蛋白质
val CarbYellow = Color(0xFFFFD89A)         // 碳水
val FatCoral = Color(0xFFFFB4A2)           // 脂肪
val SuccessGreen = Color(0xFFB5C99A)

// === Light Theme 中性色 ===
val BackgroundLight = Color(0xFFF9F2EF)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF4EAE3)
val OutlineLight = Color(0xFFE5D5C7)
val TextPrimaryLight = Color(0xFF3D2E1F)
val TextSecondaryLight = Color(0xFF7A6650)
val TextDisabledLight = Color(0xFFB9A998)

// === Dark Theme 中性色 ===
val BackgroundDark = Color(0xFF1F1812)
val SurfaceDark = Color(0xFF2A2018)
val SurfaceVariantDark = Color(0xFF382A1E)
val OutlineDark = Color(0xFF554334)
val TextPrimaryDark = Color(0xFFF9F2EF)
val TextSecondaryDark = Color(0xFFB9A998)
val TextDisabledDark = Color(0xFF6B5A48)
```

---

## 四、Theme.kt 配置

```kotlin
// app/src/main/java/com/example/eatmate/ui/theme/Theme.kt
package com.example.eatmate.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = BrandOrange,         // 主色
    onPrimary = Color(0xFF3D2E1F),
    primaryContainer = BrandPeach, // 主色容器
    onPrimaryContainer = Color(0xFF3D2E1F),

    secondary = BrandGreen,        // 辅色
    onSecondary = Color(0xFF3D2E1F),
    secondaryContainer = Color(0xFFE8F0D2),
    onSecondaryContainer = Color(0xFF3D2E1F),

    tertiary = BrandInfo,          // 信息色
    onTertiary = Color(0xFF1B3A52),

    background = BackgroundLight,
    onBackground = TextPrimaryLight,

    surface = SurfaceLight,
    onSurface = TextPrimaryLight,

    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,

    outline = OutlineLight,
    outlineVariant = Color(0xFFEFE3D6),

    error = DangerRed,
    onError = Color(0xFFFFFFFF)
)

private val DarkColors = darkColorScheme(
    primary = BrandOrange,
    onPrimary = Color(0xFF3D2E1F),
    primaryContainer = Color(0xFF5C4322),
    onPrimaryContainer = BrandOrange,

    secondary = BrandGreen,
    onSecondary = Color(0xFF1F2E12),
    secondaryContainer = Color(0xFF2D3E1B),
    onSecondaryContainer = BrandGreen,

    tertiary = BrandInfo,
    onTertiary = Color(0xFF0D2740),

    background = BackgroundDark,
    onBackground = TextPrimaryDark,

    surface = SurfaceDark,
    onSurface = TextPrimaryDark,

    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,

    outline = OutlineDark,
    outlineVariant = Color(0xFF3D2E20),

    error = DangerRed,
    onError = Color(0xFFFFFFFF)
)

@Composable
fun EatmateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 注意: 关闭动态颜色，使用品牌色
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EatmateTypography,
        shapes = EatmateShapes,
        content = content
    )
}
```

**关键点**:
- `dynamicColor = false` — 关闭 M3 动态取色，强制使用品牌色
- `statusBarColor` 跟随 background — 让状态栏融入页面

---

## 五、Shape.kt 与 Typography.kt

### 5.1 圆润形状

```kotlin
// app/src/main/java/com/example/eatmate/ui/theme/Shape.kt
package com.example.eatmate.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val EatmateShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),    // 小标签
    small = RoundedCornerShape(12.dp),        // 按钮
    medium = RoundedCornerShape(20.dp),       // 卡片
    large = RoundedCornerShape(28.dp),       // 模态
    extraLarge = RoundedCornerShape(36.dp)   // 巨型容器
)
```

### 5.2 字体排版

```kotlin
// app/src/main/java/com/example/eatmate/ui/theme/Type.kt
package com.example.eatmate.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val SansFamily = FontFamily.Default

val EatmateTypography = Typography(
    // 大标题
    displayLarge = TextStyle(
        fontFamily = SansFamily, fontWeight = FontWeight.Bold,
        fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = SansFamily, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = (-0.3).sp
    ),
    // 主标题
    headlineLarge = TextStyle(
        fontFamily = SansFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp, lineHeight = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SansFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 28.sp
    ),
    // 副标题
    titleLarge = TextStyle(
        fontFamily = SansFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SansFamily, fontWeight = FontWeight.Medium,
        fontSize = 16.sp, lineHeight = 24.sp
    ),
    // 正文
    bodyLarge = TextStyle(
        fontFamily = SansFamily, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SansFamily, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SansFamily, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp
    ),
    // 标签
    labelLarge = TextStyle(
        fontFamily = SansFamily, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SansFamily, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SansFamily, fontWeight = FontWeight.Medium,
        fontSize = 10.sp, lineHeight = 14.sp
    )
)
```

---

## 六、核心组件配色指南

### 6.1 主按钮 (FilledButton)

```kotlin
Button(
    onClick = { },
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,  // 蜜橙
        contentColor = MaterialTheme.colorScheme.onPrimary
    ),
    shape = MaterialTheme.shapes.large
) {
    Text("拍照识别", style = MaterialTheme.typography.titleMedium)
}
```

### 6.2 营养素卡 (菜品结果展示)

```kotlin
@Composable
fun NutritionChip(label: String, value: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.2f),  // 20%透明版作背景
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Box(
                Modifier
                    .size(8.dp)
                    .background(color, RoundedCornerShape(4.dp))
            )
            Spacer(Modifier.width(6.dp))
            Text("$label $value", style = MaterialTheme.typography.labelMedium)
        }
    }
}
// 用法:
NutritionChip("蛋白质", "22.5g", ProteinPurple)
NutritionChip("碳水", "35g", CarbYellow)
NutritionChip("脂肪", "12g", FatCoral)
```

### 6.3 热量环形图

```kotlin
// 主色 = BrandOrange
// 进度背景 = BrandPeach.copy(alpha = 0.3f)
// 文字 = TextPrimary
// 中心数字用 headlineLarge，weight = Bold
```

### 6.4 卡片

```kotlin
Card(
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface  // 纯白
    ),
    shape = MaterialTheme.shapes.medium,  // 20dp 圆角
    elevation = CardDefaults.cardElevation(
        defaultElevation = 0.dp  // 用 border 而非阴影，保持柔和
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
) { ... }
```

### 6.5 拍照页（核心页面）

```kotlin
Box(
    modifier = Modifier
        .fillMaxSize()
        .background(BrandWarm)  // 暖白背景
) {
    // CameraX 预览全屏
    AndroidView(factory = { ... })
    
    // 顶部: 顶部半透明深棕栏
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3D2E1F).copy(alpha = 0.4f))
            .padding(top = 24.dp, bottom = 16.dp)
    ) {
        // 关闭按钮
        IconButton(onClick = onClose) {
            Icon(Icons.Default.Close, "关闭", tint = Color.White)
        }
        Spacer(Modifier.weight(1f))
        Text("智能识菜", color = Color.White, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.width(48.dp))
    }
    
    // 底部: 半透明蜜桃色快门区
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .background(BrandPeach.copy(alpha = 0.5f))
            .padding(vertical = 24.dp)
    ) {
        // 大圆快门按钮
        ShutterButton(...)
    }
}
```

### 6.6 日记页 (主背景 + 卡片)

```kotlin
// 页面背景: BrandWarm
// 顶部日汇总卡: 蜜桃色背景 + 大字
Surface(
    color = BrandPeach,  // 蜜桃色
    shape = MaterialTheme.shapes.large,
    modifier = Modifier.fillMaxWidth().padding(16.dp)
) {
    Column(modifier = Modifier.padding(20.dp)) {
        Text("今日摄入", style = MaterialTheme.typography.titleMedium,
             color = Color(0xFF5C4322))
        Spacer(Modifier.height(8.dp))
        Text("1,250", style = MaterialTheme.typography.displayLarge,
             color = Color(0xFF3D2E1F))
        Text("千卡 / 目标 1,800", style = MaterialTheme.typography.bodyMedium,
             color = Color(0xFF7A6650))
    }
}

// 餐食条目: 纯白卡片
items.forEach { meal ->
    Surface(
        color = Surface,  // 纯白
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // 左侧: 圆形菜品占位
            Box(Modifier.size(48.dp).background(BrandGreen, CircleShape))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(meal.name, style = MaterialTheme.typography.titleMedium)
                Text("${meal.calories} 千卡", style = MaterialTheme.typography.bodyMedium,
                     color = TextSecondary)
            }
        }
    }
}
```

---

## 七、插画风格指南

参考图里的元素都是**几何化插画**——不用复杂写实绘画，简化的几何形状+大色块+轻微渐变。

### 7.1 智食需要的插画场景

- 空状态（"还没记录，今天开始吧"）
- 成功反馈（"识别完成"）
- 加载占位（"AI 在分析中"）
- 引导提示（"对准食物拍照"）

### 7.2 制作方式

- **简单方案**: 准备 5-8 张 SVG/PNG，由设计师在 Figma 制作
- **自绘方案**: 用 Compose `Canvas` 绘制几何图形
- **更简单**: 用插画素材包（如 [unDraw](https://undraw.co/)）然后调色到品牌色

### 7.3 风格约束

- 圆角几何（无尖锐边缘）
- 渐变克制（最多 2 阶渐变）
- 阴影轻微（投影 2-4dp，不超过 6dp）
- 主色用 #F9BC53 和 #D2E0AA，辅色 #FCCEB4 偶尔点缀

---

## 八、图标风格

### 推荐方案

**Phosphor Icons** 或 **Lucide** (去色版)：

- 线条 1.5-2dp（非 Material 标准的 1.5dp）
- 圆角端点
- 暖色基底（不用纯黑 #000）

```kotlin
// 智食图标颜色策略
Icon(
    imageVector = Icons.Outlined.CameraAlt,
    contentDescription = "拍照",
    tint = MaterialTheme.colorScheme.primary,  // 蜜橙
    modifier = Modifier.size(28.dp)
)
```

---

## 九、关键页面 Mockup 描述

### 9.1 启动页

- 暖白底
- 居中 Logo: 圆形蜜橙渐变，中心是白色食物图标
- 底部 logo + 版权

### 9.2 主页 (拍照前)

- 顶部: "你好, 小明 🌱" + 头像
- 搜索框: 暖白底 + 蜜橙搜索按钮
- 4 个功能图标: 拍照/日记/计划/我的
- "今日餐食" 标题
- 大卡片: 蜜桃色背景，显示"今日总卡路里 1,250 / 目标 1,800"
- 餐食列表: 纯白卡片，左侧嫩绿圆圈 + 菜名

### 9.3 拍照页

- 全屏相机
- 顶部半透明深棕栏
- 中心: 虚线白框（引导用户对准食物）
- 底部: 蜜桃色半透明白区 + 蜜橙大快门按钮

### 9.4 结果页

- 顶部: 拍的照片缩略图
- 标题: "AI 识别结果" + 蜜橙小标签 "置信度 92%"
- 大数字: 总热量 (displayLarge, 蜜橙)
- 营养素横向: 蛋白质 22.5g / 碳水 35g / 脂肪 12g (彩色 Chip)
- 菜品列表: 卡片 + 嫩绿圆圈图标
- 底部: 蜜橙大按钮 "记录这顿饭"

### 9.5 饮食日记

- 顶部: 月份切换器
- 横向: 7 天日期条 (今日蜜橙高亮)
- 中心: 每日餐食时间线
- 底部: 卡路里环形图 (蜜橙 + 蜜桃底)

---

## 十、最后的设计守则

1. **永远用暖白 `#F9F2EF` 做背景**，不要用纯白 `#FFFFFF`（页面），纯白只用于卡片
2. **主色用蜜橙 `#F9BC53`**，不用饱和度高的红橙
3. **嫩绿 `#D2E0AA` 做辅助色**，永远不单独做按钮（亮度不足）
4. **柔色优先**：所有强调色都用饱和度 60-70% 的版本
5. **圆角 20dp 起**：所有卡片、按钮、模态都偏圆润
6. **阴影克制**：用 0dp 阴影 + 1dp 暖灰描边代替
7. **文案语气温和**：不用"请"、"必须"，用"试试看"、"一起加油"

---

> **掌中灵 - 移动应用开发工程师 | 2026-07-03**
> v1.0 的"健康绿+暖橙"是工具感，v2.0 的"暖白+蜜橙+嫩绿"是生活感。
> 智食的赛道是"减脂生活"，不是"健身训练"，所以从今天起就用 v2.0。
