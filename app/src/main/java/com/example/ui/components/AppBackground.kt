package com.example.ui.components

import com.example.ui.theme.AppColors

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R

private data class StarParticle(
    val xRatio: Float,
    val yRatio: Float,
    val radiusPx: Float,
    val baseAlpha: Float,
    val phaseOffset: Float,
    val twinkleSpeed: Float,
    val color: Color
)

private data class AuroraBlob(
    val xRatio: Float,
    val yRatio: Float,
    val radiusRatio: Float,
    val color: Color,
    val phaseOffset: Float,
    val driftX: Float,
    val driftY: Float
)

@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFF06030E),
    activeTab: String = "chats"
) {
    val stars = remember {
        val rand = Random(42)
        val list = mutableListOf<StarParticle>()
        val colors = listOf(
            AppColors.White,
            AppColors.PurpleSoft,
            AppColors.Hex_BAE6FD,
            AppColors.YellowLight,
            Color(0xFFF0ABFC)
        )
        for (i in 0 until 65) {
            list.add(
                StarParticle(
                    xRatio = rand.nextFloat(),
                    yRatio = rand.nextFloat(),
                    radiusPx = rand.nextFloat() * 1.3f + 0.6f,
                    baseAlpha = rand.nextFloat() * 0.22f + 0.06f,
                    phaseOffset = rand.nextFloat() * (2f * PI.toFloat()),
                    twinkleSpeed = 0.6f + rand.nextFloat() * 0.9f,
                    color = colors[i % colors.size]
                )
            )
        }
        for (i in 0 until 26) {
            list.add(
                StarParticle(
                    xRatio = rand.nextFloat(),
                    yRatio = rand.nextFloat(),
                    radiusPx = rand.nextFloat() * 2.2f + 1.2f,
                    baseAlpha = rand.nextFloat() * 0.30f + 0.12f,
                    phaseOffset = rand.nextFloat() * (2f * PI.toFloat()),
                    twinkleSpeed = 0.4f + rand.nextFloat() * 0.6f,
                    color = colors[i % colors.size]
                )
            )
        }
        list
    }

    val auroraBlobs = remember {
        listOf(
            AuroraBlob(0.25f, 0.14f, 0.62f, Color(0xFF7C3AED), 0f,   +0.06f, +0.04f),
            AuroraBlob(0.74f, 0.24f, 0.50f, Color(0xFF4C1D95), 1.2f, -0.05f, +0.03f),
            AuroraBlob(0.50f, 0.60f, 0.72f, Color(0xFF1E1B4B), 2.4f, +0.03f, -0.05f),
            AuroraBlob(0.14f, 0.52f, 0.44f, Color(0xFF5B21B6), 3.6f, +0.05f, +0.03f),
            AuroraBlob(0.84f, 0.76f, 0.48f, Color(0xFF6D28D9), 4.8f, -0.04f, -0.05f),
        )
    }

    val infinite = rememberInfiniteTransition(label = "bg")

    val auroraTime by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auroraTime"
    )

    val twinkleTime by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkleTime"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseColor)
    ) {
        // Photorealistic top-right dark-purple cratered planet & diagonal cosmic nebula
        if (activeTab != "templates") {
            Image(
                painter = painterResource(id = R.drawable.img_botlist_cosmic_bg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            if (w <= 0f || h <= 0f) return@Canvas

            // ── Layer 1: Subtle ambient aurora blobs ─────────────────────────
            auroraBlobs.forEachIndexed { idx, blob ->
                val phase = auroraTime + blob.phaseOffset
                val driftX = sin(phase * blob.driftX * 4f) * w * 0.05f
                val driftY = cos(phase * blob.driftY * 4f) * h * 0.04f
                val cx = blob.xRatio * w + driftX
                val cy = blob.yRatio * h + driftY
                val radius = blob.radiusRatio * w
                val alpha = 0.038f + 0.020f * sin(phase + idx)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            blob.color.copy(alpha = alpha * 1.4f),
                            blob.color.copy(alpha = alpha * 0.45f),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = radius
                    ),
                    radius = radius,
                    center = Offset(cx, cy)
                )
            }

            // ── Layer 2: Twinkling starfield ─────────────────────────────────
            stars.forEach { star ->
                val sx = star.xRatio * w
                val sy = star.yRatio * h
                val twinkle = 0.5f + 0.5f * sin(twinkleTime * star.twinkleSpeed + star.phaseOffset)
                val alpha = (star.baseAlpha * (0.35f + 0.85f * twinkle)).coerceIn(0f, 1f)

                if (star.radiusPx > 1.9f && twinkle > 0.72f) {
                    drawCircle(
                        color = star.color.copy(alpha = alpha * 0.22f),
                        radius = star.radiusPx * 2.8f,
                        center = Offset(sx, sy)
                    )
                }

                drawCircle(
                    color = star.color.copy(alpha = alpha),
                    radius = star.radiusPx,
                    center = Offset(sx, sy)
                )
            }

            // ── Layer 3: Subtle bottom ambient reflection ────────────────────
            val bottomCenter = Offset(w * 0.5f, h * 1.0f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4C1D95).copy(alpha = 0.16f),
                        Color.Transparent
                    ),
                    center = bottomCenter,
                    radius = w * 0.65f
                ),
                center = bottomCenter,
                radius = w * 0.65f
            )
        }
    }
}
