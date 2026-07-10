# 智食 Qwen3.6-Plus 食物识别测试方案

> 目的: 验证 Qwen3.6-Plus 在真实中餐场景下的识别能力
> 前置: 已注册阿里云百炼并获取 API Key

---

## 一、测试图片准备（6 张，覆盖核心场景）

| # | 场景 | 有什么菜 | 测试点 |
|---|------|------|--------|
| 1 | 🥡 **外卖套餐** | 黄焖鸡米饭、配蔬菜 | 单一主食+配菜，塑料盒装 |
| 2 | 🍱 **食堂餐盘** | 番茄炒蛋 + 红烧肉 + 米饭 | 多菜品同盘，不锈钢餐盘 |
| 3 | 🥘 **家常炒菜** | 青椒肉丝、炒空心菜 | 家常菜，陶瓷碗碟 |
| 4 | 🍜 **汤面类** | 牛肉面/麻辣烫 | 液体+固体混合，碗装 |
| 5 | 🥗 **减脂餐** | 鸡胸肉+西兰花+糙米饭 | 健身人群高频场景 |
| 6 | 🌃 **暗光环境** | 任意外卖（不开灯拍） | 低光照下的识别鲁棒性 |

---

## 二、测评 Prompt（复制直接用）

### Prompt A — 完整营养分析（主力）

```text
你是一位持有国家认证的资深营养师。请仔细分析这张食物照片，返回严格的 JSON 格式，不要包含 markdown 代码块标记，不要有任何解释性文字：

{
  "dishes": [
    {
      "name": "中文菜名",
      "estimated_weight_g": 预估重量克数,
      "calories_kcal": 预估总热量千卡,
      "protein_g": 蛋白质克数,
      "carb_g": 碳水化合物克数,
      "fat_g": 脂肪克数,
      "confidence": 0-1之间的置信度,
      "ingredients": ["主要食材1", "主要食材2"],
      "cooking_method": "炒/蒸/煮/炸/烤/炖/凉拌/其他"
    }
  ],
  "total_calories_kcal": 所有菜品总热量,
  "total_protein_g": 总蛋白质,
  "total_carb_g": 总碳水化合物,
  "total_fat_g": 总脂肪,
  "dining_scene": "外卖/食堂/家常/聚餐/其他",
  "nutrition_assessment": "一句话营养评价",
  "health_score": 1-10健康评分
}

注意事项：
- 菜名使用中国大陆通用名称
- 如果照片中有多个菜品，请全部识别
- 如果识别不确定，confidence 适当降低并在 nutrition_assessment 中说明
- 外卖/食堂的份量参考实际常见份量估算
```

### Prompt B — 简洁版（快速验证用）

```text
识别这张食物照片中的所有菜品。返回 JSON：

{
  "dishes": [{"name": "菜名", "calories_kcal": 千卡, "confidence": 0.0-1.0}],
  "total_calories_kcal": 总热量
}
```

### Prompt C — 健身人群版（后续产品用）

```text
你是一位健身营养教练。我正在减脂/增肌，请分析这顿饭：

返回 JSON：
{
  "dishes": [{"name": "", "estimated_weight_g": 0, "calories_kcal": 0, "protein_g": 0, "carb_g": 0, "fat_g": 0}],
  "total": {"calories": 0, "protein": 0, "carb": 0, "fat": 0},
  "fit_goal": {"rating": "优秀/还行/不合适", "reason": "一句理由"},
  "improvement": "如何改进这道菜的建议（减脂版侧重少油少碳，增肌版侧重加蛋白）"
}
```

---

## 三、评分维度

每张照片测试后，按以下维度打分（1-5 分）：

| 评分维度 | 看什么 |
|----------|--------|
| **菜品识别** | 菜名正确吗？多菜品都识别到了吗？ |
| **热量合理** | 热量数大致靠谱吗？（eg. 一份黄焖鸡约 500-700kcal） |
| **营养比例** | 蛋白质/碳水/脂肪比例符合常识吗？ |
| **重量估算** | 重量估算在合理范围吗？（外卖一份菜通常 200-400g） |
| **JSON 格式** | 返回的是纯 JSON 吗？有没有多出 markdown 包裹？ |
| **低光照表现** | 暗光环境是否仍有合理的识别结果？ |

### 常见热量参考（帮你判断合理性）

| 菜品 | 一份大约热量 | 一份大约重量 |
|------|:---:|:---:|
| 黄焖鸡米饭 | 600-800 kcal | 500g |
| 番茄炒蛋 | 200-300 kcal | 300g |
| 红烧肉 | 500-700 kcal | 250g |
| 牛肉面 | 500-700 kcal | 600g |
| 麻辣烫(荤素) | 400-800 kcal | 500g |
| 鸡胸肉西兰花 | 300-400 kcal | 350g |
| 青椒肉丝 | 300-400 kcal | 300g |

---

## 四、测试记录表格

| # | 场景 | 菜名对？ | 热量合理？ | 多菜品？ | JSON 干净？ | 暗光？ | 备注 |
|---|------|:---:|:---:|:---:|:---:|:---:|------|
| 1 | 外卖 | | | | | - | |
| 2 | 食堂 | | | | | - | |
| 3 | 家常 | | | | | - | |
| 4 | 汤面 | | | | | - | |
| 5 | 减脂 | | | | | - | |
| 6 | 暗光 | | | | | ✅ | |

**通过标准**: 6 张照片中 ≥5 张菜品名正确，≥4 张热量在合理范围，JSON 100% 纯净无 markdown 包裹。

---

## 五、快速测试脚本（可选 Python CLI）

```python
# test_food_recognition.py
# 用法: python test_food_recognition.py 照片.jpg
# 需要: pip install openai requests

import sys, base64, json
from openai import OpenAI

API_KEY = "你的百炼API Key"
BASE_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1"

PROMPT = """你是一位资深营养师。分析这张食物照片，返回严格的 JSON：
{"dishes":[{"name":"","estimated_weight_g":0,"calories_kcal":0,"protein_g":0,"carb_g":0,"fat_g":0,"confidence":0}],"total_calories_kcal":0,"dining_scene":"","health_score":0}
不要 markdown 代码块，只要纯 JSON。"""

def test_food(image_path):
    with open(image_path, "rb") as f:
        img_b64 = base64.b64encode(f.read()).decode()

    client = OpenAI(api_key=API_KEY, base_url=BASE_URL)

    resp = client.chat.completions.create(
        model="qwen3.6-plus",
        messages=[{
            "role": "user",
            "content": [
                {"type": "text", "text": PROMPT},
                {"type": "image_url", "image_url": {"url": f"data:image/jpeg;base64,{img_b64}"}}
            ]
        }],
        timeout=30
    )

    content = resp.choices[0].message.content
    # 尝试剥离可能的 markdown 包裹
    if content.startswith("```"):
        content = content.split("\n", 1)[1].rsplit("```", 1)[0]

    result = json.loads(content)
    print(json.dumps(result, ensure_ascii=False, indent=2))

    # 简单合理性检查
    for d in result.get("dishes", []):
        cal = d.get("calories_kcal", 0)
        name = d.get("name", "")
        if cal < 50:
            print(f"⚠️ {name}: 热量 {cal}kcal 偏低，可能估算不足")
        elif cal > 1500:
            print(f"⚠️ {name}: 热量 {cal}kcal 偏高，可能估算过多")
        else:
            print(f"✅ {name}: {cal}kcal ✓")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("用法: python test_food_recognition.py 照片.jpg")
        sys.exit(1)
    test_food(sys.argv[1])
```

---

## 六、通过标准与决策

| 结果 | 决策 |
|------|------|
| 6 张全过，JSON 稳定 | ✅ 直接集成 Qwen3.6-Plus，信心满满 |
| 菜名对但热量偏 20-30% | ✅ 可接受，Prompt 微调后再测一轮 |
| JSON 偶尔被 markdown 包裹 | ⚠️ 解析层做防御处理，不影响集成 |
| 暗光表现差或多菜品遗漏严重 | ⚠️ 考虑加 Gemini 2.5 Flash 兜底 |
| 菜名大量错误 | ❌ 换 Qwen3-VL-Plus 或 Gemini 重测 |

> **掌中灵 - 移动应用开发工程师 | 2026-07-03**
