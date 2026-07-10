# 智食 设计规范

> 版本: 2.0 | 更新: 2026-07-03
> 基于: 插画风柔色板 (蜜橙+嫩绿+蜜桃+暖白)

---

## 一、色板

### 品牌主色

| 角色 | 代码名 | 色值 | 用途 |
|------|--------|------|------|
| **Primary** | `BrandOrange` | `#F9BC53` | 主按钮、CTA、热量大数字 |
| **Secondary** | `BrandGreen` | `#D2E0AA` | 健康标签、辅助元素(不做按钮) |
| **Tertiary** | `BrandInfo` | `#ABD7FB` | 信息提示、链接 |
| **Accent** | `BrandPeach` | `#FCCEB4` | 卡片高亮背景、日汇总卡片 |

### 语义色 (营养素)

| 营养素 | 代码名 | 色值 |
|--------|--------|------|
| 蛋白质 | `ProteinPurple` | `#C8B6E2` |
| 碳水 | `CarbYellow` | `#FFD89A` |
| 脂肪 | `FatCoral` | `#FFB4A2` |
| 健康 | `HealthyGreen` | `#B5C99A` |
| 警告 | `WarningOrange` | `#F9BC53` (复用主色) |
| 危险 | `DangerRed` | `#E78A8A` (柔和红，非正红) |

### 中性色 (光模式)

| 角色 | 代码名 | 色值 |
|------|--------|------|
| 页面背景 | `BackgroundLight` | `#F9F2EF` (暖白) |
| 卡片 | `SurfaceLight` | `#FFFFFF` (纯白) |
| 次要卡片 | `SurfaceVariantLight` | `#F4EAE3` |
| 描边 | `OutlineLight` | `#E5D5C7` |
| 主文字 | `TextPrimaryLight` | `#3D2E1F` (暖深棕) |
| 次文字 | `TextSecondaryLight` | `#7A6650` |

---

## 二、圆角系统

| 等级 | 值 | 用途 |
|------|-----|------|
| `extraSmall` | 8dp | 小标签、Chip |
| `small` | 12dp | 按钮、输入框 |
| **`medium`** | **20dp** | **卡片 (最常用)** |
| `large` | 28dp | 模态、大容器 |
| `extraLarge` | 36dp | 超大容器 |

---

## 三、阴影策略

**原则**: 扁平优先，仅关键交互元素用阴影。

| 场景 | 阴影 |
|------|------|
| 普通卡片 | 0dp + 1dp 暖灰描边 (`outline.copy(alpha=0.3f)`) |
| **快门按钮** | **12dp 蜜橙阴影** (唯一重阴影) |
| 按下态 | 4dp 阴影 (按压反馈) |
| Dialog/FAB | 6dp 轻微阴影 |

---

## 四、字体排版

### 层级

| 样式 | 字号 | 字重 | 行高 | 用途 |
|------|:---:|------|:---:|------|
| `displayLarge` | 36sp | Bold | 44sp | 热量大数字 |
| `displayMedium` | 28sp | Bold | 36sp | 页面标题数字 |
| `headlineLarge` | 24sp | SemiBold | 32sp | 页面大标题 |
| `headlineMedium` | 20sp | SemiBold | 28sp | 卡片标题 |
| `titleLarge` | 18sp | SemiBold | 26sp | 区块标题 |
| `titleMedium` | 16sp | Medium | 24sp | 列表项标题 |
| `bodyLarge` | 16sp | Normal | 24sp | 正文 |
| `bodyMedium` | 14sp | Normal | 20sp | 辅助说明 |
| `labelLarge` | 14sp | Medium | 20sp | 标签 |
| `labelMedium` | 12sp | Medium | 16sp | 小标签 |
| `labelSmall` | 10sp | Medium | 14sp | 极小标签 |

### 用色规则

- **主文字永远用 `onSurface`** — 不要直接硬编码 `#3D2E1F`
- **次级文字用 `onSurfaceVariant`** — 自动适配暗色模式
- **链接/可点击文字用 `primary`** — 蜜橙
- **禁用文字用 `onSurface.copy(alpha = 0.38f)`**

---

## 五、组件配色速查

| 组件 | 背景色 | 文字色 | 描边 | 圆角 |
|------|--------|--------|------|:---:|
| 主按钮 | `primary` | `onPrimary` | 无 | `small` |
| 次按钮 | 透明 | `primary` | `primary` 1dp | `small` |
| 卡片 | `surface` | `onSurface` | `outline` 0.3α 1dp | `medium` |
| 日汇总卡 | `primaryContainer`(蜜桃) | `onPrimaryContainer` | 无 | `large` |
| Chip(营养) | 语义色 0.2α | `onSurface` | 无 | `extraSmall` |
| 输入框 | `surface` | `onSurface` | `outline` 1dp | `small` |

---

## 六、间距系统

| 间距 | 值 | 用途 |
|------|:---:|------|
| xs | 4dp | 图标与文字间 |
| sm | 8dp | 同类元素间 |
| md | 12dp | Chip 间、列表项内 |
| **lg** | **16dp** | **卡片内边距、页面边距** |
| xl | 24dp | 大区块间 |
| xxl | 32dp | 页面顶部/底部留白 |

---

## 七、主题配置要点

```kotlin
// Theme.kt 中必须
EatmateTheme(
    dynamicColor = false,  // 强制品牌色，不用系统动态色
)
```

- 状态栏颜色跟随 `background`
- 光模式: 深色状态栏图标 (`isAppearanceLightStatusBars = true`)
- 暗模式: 浅色状态栏图标 (`isAppearanceLightStatusBars = false`)
