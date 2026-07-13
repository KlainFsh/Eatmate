# Eatmate (智食) 项目长期记忆

## 项目定位
- 垂直面向减脂/健身人群的 AI 饮食管理 App
- 核心模块: AI 食物拍照识别 + 热量营养智能分析
- 次要功能: 一键定制一日三餐
- 目标用户: 白领、学生（外卖、食堂、校园餐场景）

## 技术决策
- 平台: Android 原生 (Kotlin + Jetpack Compose + M3)
- minSdk: 26 (Android 8.0)
- 架构: MVVM + Clean Architecture
- DI: Hilt
- 导航: Navigation Compose
- 网络: Retrofit + OkHttp + Kotlinx Serialization
- 数据库: Room + DataStore
- 图片: Coil 3
- 相机: CameraX
- 后台: WorkManager
- AI 识别: 阿里百炼 qwen-vl-plus 多模态大模型 (MaaS 工作空间, 图文统一, 国内直连)
- AI 降级: Gemini 2.5 Flash 作为能力最强兜底 (需中转, 可选)

## 品牌视觉
- 主色: 蜜橙 #F9BC53 (替代旧的健康绿 #4CAF50)
- 辅色: 嫩绿 #D2E0AA, 蜜桃 #FCCEB4
- 中性: 暖白 #F9F2EF (背景), 纯白 #FFFFFF (卡片)
- 信息色: 天空蓝 #ABD7FB
- 风格: 插画风柔色板 (治愈系、生活感), 大圆角 20-28dp, 阴影克制

## 导航架构
- 5 Tab: 首页 / 日记 / 拍照(导航栏居中) / 趋势 / 我的
- 首页: 今日摘要卡 + 营养素卡片 + 拍照快捷入口 + 最近记录
- 拍照: 二级页面, 通过底部导航栏拍照项进入
- 趋势: 周热量柱状图 + 体重变化曲线占位

## 开发阶段
- Phase 1 (MVP): 拍照识别 + 营养分析 + 餐食记录
- Phase 2: 膳食建议 + 饮食日记 + 用户目标
- Phase 3: 三餐定制 + 趋势分析 + 厂商推送
