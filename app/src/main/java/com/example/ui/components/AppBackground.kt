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
    baseColor: Color = Color(0xFF070510),
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

    val centralPulse by infinite.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "centralPulse"
    )

    val planetFloatY by infinite.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "planetFloatY"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseColor)
    ) {
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
                val alpha = 0.045f + 0.025f * sin(phase + idx)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            blob.color.copy(alpha = alpha * 1.5f),
                            blob.color.copy(alpha = alpha * 0.5f),
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

            // ── Layer 3: Seamless Top-Right Cosmic Planet & Atmospheric Nebula ─
            if (activeTab == "books") {
                // Exact top-right cosmic planet limb & crescent rim from Kozmik Sürükleniş reference
                val planetCenter = Offset(w * 0.96f, h * 0.042f + planetFloatY.dp.toPx())
                val planetRadius = w * 0.235f

                // Soft cosmic nebula mist behind top-right pill & planet limb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF7C3AED).copy(alpha = 0.26f * centralPulse),
                            Color(0xFF4C1D95).copy(alpha = 0.14f * centralPulse),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.80f, h * 0.068f),
                        radius = w * 0.44f
                    ),
                    center = Offset(w * 0.80f, h * 0.068f),
                    radius = w * 0.44f
                )

                // Planet sphere body seamlessly blended into deep space
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF28125A).copy(alpha = 0.92f),
                            Color(0xFF160836).copy(alpha = 0.95f),
                            Color(0xFF090418).copy(alpha = 0.98f)
                        ),
                        center = Offset(
                            planetCenter.x - planetRadius * 0.28f,
                            planetCenter.y + planetRadius * 0.22f
                        ),
                        radius = planetRadius * 1.1f
                    ),
                    center = planetCenter,
                    radius = planetRadius
                )

                // Subtle surface cloud texture inside planet limb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x38A855F7),
                            Color(0x186366F1),
                            Color.Transparent
                        ),
                        center = Offset(
                            planetCenter.x - planetRadius * 0.42f,
                            planetCenter.y + planetRadius * 0.35f
                        ),
                        radius = planetRadius * 0.65f
                    ),
                    center = Offset(
                        planetCenter.x - planetRadius * 0.42f,
                        planetCenter.y + planetRadius * 0.35f
                    ),
                    radius = planetRadius * 0.65f
                )

                // Soft outer atmospheric crescent glow along bottom-left limb
                drawIntoCanvas { canvas ->
                    val rimGlowPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        style = android.graphics.Paint.Style.STROKE
                        strokeWidth = 6.dp.toPx()
                        strokeCap = android.graphics.Paint.Cap.ROUND
                        color = Color(0xFFA855F7).copy(alpha = 0.52f * centralPulse).toArgb()
                        maskFilter = android.graphics.BlurMaskFilter(
                            10.dp.toPx(),
                            android.graphics.BlurMaskFilter.Blur.NORMAL
                        )
                    }
                    val rect = android.graphics.RectF(
                        planetCenter.x - planetRadius,
                        planetCenter.y - planetRadius,
                        planetCenter.x + planetRadius,
                        planetCenter.y + planetRadius
                    )
                    canvas.nativeCanvas.drawArc(rect, 102f, 118f, false, rimGlowPaint)
                }

                // Crisp illuminated crescent edge arc
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFFC084FC).copy(alpha = 0.72f * centralPulse),
                            Color(0xFFF3E8FF).copy(alpha = 0.92f * centralPulse),
                            Color(0xFF9333EA).copy(alpha = 0.58f * centralPulse),
                            Color.Transparent
                        ),
                        start = Offset(planetCenter.x - planetRadius, planetCenter.y),
                        end = Offset(planetCenter.x, planetCenter.y + planetRadius)
                    ),
                    startAngle = 104f,
                    sweepAngle = 114f,
                    useCenter = false,
                    topLeft = Offset(planetCenter.x - planetRadius, planetCenter.y - planetRadius),
                    size = Size(planetRadius * 2f, planetRadius * 2f),
                    style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                )
            } else if (activeTab != "templates") {
                // Seamless 3D dark purple cratered moon & diagonal cosmic mist for Botlarım header
                val moonCenter = Offset(w * 0.745f, h * 0.088f + planetFloatY.dp.toPx())
                val moonRadius = w * 0.115f

                // Diagonal nebula mist behind moon
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF6D28D9).copy(alpha = 0.24f * centralPulse),
                            Color(0xFF4C1D95).copy(alpha = 0.12f * centralPulse),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.66f, h * 0.085f),
                        radius = w * 0.36f
                    ),
                    center = Offset(w * 0.66f, h * 0.085f),
                    radius = w * 0.36f
                )

                // 3D Moon sphere shaded from top-left violet to bottom-right deep shadow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF3B1A78).copy(alpha = 0.88f),
                            Color(0xFF1F0C47).copy(alpha = 0.92f),
                            Color(0xFF0A041A).copy(alpha = 0.96f)
                        ),
                        center = Offset(
                            moonCenter.x - moonRadius * 0.35f,
                            moonCenter.y - moonRadius * 0.25f
                        ),
                        radius = moonRadius * 1.35f
                    ),
                    center = moonCenter,
                    radius = moonRadius
                )

                // Subtle surface crater/nebula mottling inside the sphere
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x339333EA),
                            Color.Transparent
                        ),
                        center = Offset(moonCenter.x - moonRadius * 0.25f, moonCenter.y + moonRadius * 0.2f),
                        radius = moonRadius * 0.55f
                    ),
                    center = Offset(moonCenter.x - moonRadius * 0.25f, moonCenter.y + moonRadius * 0.2f),
                    radius = moonRadius * 0.55f
                )

                // Soft crescent rim along top-left & bottom-left of the moon
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xAA9333EA),
                            Color(0xCCD8B4FE),
                            Color(0x887C3AED),
                            Color.Transparent
                        ),
                        start = Offset(moonCenter.x - moonRadius, moonCenter.y - moonRadius),
                        end = Offset(moonCenter.x, moonCenter.y + moonRadius)
                    ),
                    startAngle = 115f,
                    sweepAngle = 155f,
                    useCenter = false,
                    topLeft = Offset(moonCenter.x - moonRadius, moonCenter.y - moonRadius),
                    size = Size(moonRadius * 2f, moonRadius * 2f),
                    style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // ── Layer 4: Subtle bottom ambient reflection ────────────────────
            val bottomCenter = Offset(w * 0.5f, h * 1.0f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4C1D95).copy(alpha = 0.18f),
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
