# CLAUDE.md — 智食 (Eatmate) 项目工作指引

> 版本: 1.0 | 更新: 2026-07-03
> 用途: 为 AI 助手提供项目全貌、文件路径和开发规范

---

## 一、项目概览

**智食 (Eatmate)** — 垂直面向减脂/健身人群的 AI 饮食管理 Android App。

- **核心功能**: AI 食物拍照识别 + 热量营养智能分析
- **辅助功能**: 一键定制一日三餐
- **目标用户**: 白领、学生（外卖、食堂、校园餐场景）

## 二、技术栈速查

| 层 | 选型 |
|---|------|
| 平台 | Android Native (Kotlin + Jetpack Compose) |
| minSdk / compileSdk | 26 / 37 |
| 架构 | MVVM + Clean Architecture |
| DI | Hilt |
| 导航 | Navigation Compose |
| 网络 | Retrofit + OkHttp + Kotlinx Serialization |
| 数据库 | Room + DataStore |
| 图片加载 | Coil 3 |
| 相机 | CameraX |
| 后台 | WorkManager |
| AI 识别 | **阿里百炼 Qwen3.6-Plus** (0.8元/千tokens 图文统一) |
| AI 降级 | Gemini 2.5 Flash (需中转, 可选) |

## 三、品牌视觉速查

```
主色:  蜜橙   #F9BC53  (按钮、CTA、热量数字)
辅色:  嫩绿   #D2E0AA  (健康标签、辅助元素)
蜜桃:  蜜桃   #FCCEB4  (卡片高亮、汇总背景)
中性:  暖白   #F9F2EF  (页面背景)
       纯白   #FFFFFF  (卡片表面)
信息:  天空蓝 #ABD7FB
```

- 大圆角系统: 20-28dp
- 阴影克制: 优先 0dp + 暖灰描边, 仅快门按钮用 12dp
- 情绪基调: 治愈系 / 生活感 (非工具感)

---

## 四、文件导航

### 4.1 需求与产品文档

| 文件 | 路径 | 说明 |
|------|------|------|
| 产品需求文档 | `docs/requirements/product-requirements.md` | 功能范围、用户场景、MVP 定义 |
| 数据模型设计 | `docs/tech/data-model.md` | Entity、Room 表结构、Domain Model |

### 4.2 技术规范

| 文件 | 路径 | 说明 |
|------|------|------|
| 架构规范 | `docs/tech/architecture.md` | 分层架构、模块划分、命名约定 |
| API 规范 | `docs/tech/api-spec.md` | Retrofit 接口定义、Qwen API 调用规范 |
| 代码规范 | `docs/tech/coding-standards.md` | Kotlin 风格、Compose 最佳实践 |

### 4.3 设计规范

| 文件 | 路径 | 说明 |
|------|------|------|
| 品牌设计系统 | `docs/design/brand-system.md` | 色板、字体、形状、组件配色 |
| UI 组件目录 | `docs/design/component-catalog.md` | 所有可复用 Compose 组件的用法 |
| 页面设计说明 | `docs/design/screen-specs.md` | 各页面的布局规划与交互细节 |

### 4.4 流程与计划

| 文件 | 路径 | 说明 |
|------|------|------|
| 开发路线图 | `docs/process/roadmap.md` | 三阶段开发计划、里程碑、优先级 |
| 开发日志 | `dev-log/YYYY-MM-DD.md` | 每日工作记录（自动维护） |
| 测试方案 | `docs/process/testing-plan.md` | AI 识别测试、UI 测试、性能测试方案 |

### 4.5 调研档案（仅供查阅）

| 文件 | 路径 |
|------|------|
| 开发前评估与建议 | `.workbuddy/artifacts/pre-dev-advice.md` |
| AI 服务商全网对比 | `.workbuddy/artifacts/all-models-comparison.md` |
| AI 最终选型策略 | `.workbuddy/artifacts/final-ai-strategy.md` |
| 百度 API 文档分析 | `.workbuddy/artifacts/baidu-api-analysis.md` |
| Prompt 工程指南 | `.workbuddy/artifacts/prompt-engineering-guide.md` |
| 食物识别测试方案 | `.workbuddy/artifacts/test-food-recognition.md` |
| 品牌视觉系统 | `.workbuddy/artifacts/brand-design-system.md` |

---

## 五、开发原则

1. **每次只做一个功能模块** — 完成、测试、提交，再做下一个
2. **先跑通核心闭环** — 相机拍照 → AI 分析 → 结果展示 → 保存记录
3. **UI 做完一个再做一个** — 不在多个页面间跳跃
4. **写代码前先确认依赖** — Hilt、Room、Retrofit 等基础库一次性配好
5. **每次改完跑 `./gradlew assembleDebug`** — 确保不因依赖断裂而编译失败
6. **每日结束前更新 dev-log** — 记录完成什么、卡在哪里、明天做什么

---

## 六、开发前检查清单

- [ ] `app/build.gradle.kts` 中 minSdk=26, compileSdk=37
- [ ] `gradle/libs.versions.toml` 中 Hilt/Room/Retrofit/CameraX 版本已添加
- [ ] 主题文件 Color/Theme/Shape/Type 已替换为品牌色板
- [ ] `AndroidManifest.xml` 中相机权限已声明
- [ ] 百度/阿里云 API Key 已配置在 `local.properties`

---

## 七、当前状态 (2026-07-03)

- [x] 项目评估与技术选型完成
- [x] AI 方案确定: Qwen3.6-Plus
- [x] 品牌视觉 v2.0 确定
- [x] 主题文件重写并编译通过
- [x] ShutterButton 组件完成
- [x] 项目标准化文件创建
- [ ] Phase 1 开发启动
