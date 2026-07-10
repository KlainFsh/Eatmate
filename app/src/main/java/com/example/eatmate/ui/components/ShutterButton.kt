package com.example.eatmate.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatmate.ui.theme.BrandOrange
import com.example.eatmate.ui.theme.SurfaceLight

/**
 * 智食核心拍照快门按钮。
 *
 * 设计要点:
 * - 蜜橙实心大圆，带柔和投影，视觉权重最重
 * - 3 层同心圆: 外圈(细环)、中圈(描边)、内核(实心)
 * - 按下时缩小到 92% + 颜色加深，松手弹回
 * - 下方辅助文字 "轻触拍照"，强化识别
 *
 * @param onClick 点击回调（触发 CameraX 拍照）
 * @param enabled 是否可点击（正在分析时禁用）
 * @param modifier 外部布局修饰符
 */
@Composable
fun ShutterButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 按下时缩放到 0.92，松手弹簧弹回
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 600f),
        label = "shutter_scale"
    )

    // 按下时颜色加深
    val buttonColor by animateColorAsState(
        targetValue = if (isPressed) BrandOrange.copy(alpha = 0.85f)
                       else if (enabled) BrandOrange
                       else BrandOrange.copy(alpha = 0.4f),
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "shutter_color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        // 大圆快门按钮
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .scale(scale)
                .size(88.dp)
                .shadow(
                    elevation = if (isPressed) 4.dp else 12.dp,
                    shape = CircleShape,
                    ambientColor = BrandOrange,
                    spotColor = BrandOrange
                )
                .clip(CircleShape)
                .background(buttonColor)
                .then(
                    if (enabled) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null,  // 用自己的动画，不用涟漪
                            onClick = onClick
                        )
                    } else Modifier
                )
                .border(4.dp, SurfaceLight.copy(alpha = 0.35f), CircleShape)
        ) {
            // 内圈 — 细窄白环，增加层次
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.5.dp, SurfaceLight.copy(alpha = 0.7f), CircleShape)
            )
            // 最内层 — 纯白小圆，呼应外描边
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(SurfaceLight.copy(alpha = 0.85f))
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 辅助文字
        Text(
            text = if (enabled) "轻触拍照" else "分析中…",
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            ),
            color = if (enabled)
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            else
                BrandOrange.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}
