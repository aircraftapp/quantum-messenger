package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.QuantumCyan
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.WarningAmber

@Composable
fun RealtimeAudioWaveformVisualizer(
    isTransmitting: Boolean,
    isReceiving: Boolean = false,
    barCount: Int = 28,
    activeColor: Color = if (isTransmitting) AlertCrimson else if (isReceiving) TacticalEmerald else QuantumCyan,
    height: Dp = 36.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")

    // Generate bar pulse animation values with staggered durations for organic audio frequency look
    val animPhases = List(barCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.15f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 250 + (index * 53) % 450,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_pulse_$index"
        )
    }

    val isActive = isTransmitting || isReceiving

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .testTag("audio_waveform_visualizer_canvas")
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = (canvasWidth / (barCount * 1.5f)).coerceAtLeast(3f)
        val gap = barWidth * 0.5f

        for (i in 0 until barCount) {
            val amplitudeFactor = if (isActive) animPhases[i].value else 0.12f
            val barHeight = (canvasHeight * amplitudeFactor * 0.95f).coerceAtLeast(4f)
            val x = i * (barWidth + gap) + gap
            val y = (canvasHeight - barHeight) / 2f

            drawRoundRect(
                color = if (isActive) activeColor else activeColor.copy(alpha = 0.35f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}
