package com.example.ui.dialog

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CardSlateBorder
import com.example.ui.theme.CyanBright
import com.example.ui.theme.DarkNavySlate
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen() {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        val duration = 5100f // 5.1 seconds exactly matching HTML logic
        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            val p = (elapsed / duration).coerceIn(0f, 1f)
            // Ease out quad matching HTML e=1-Math.pow(1-p,2)
            val eased = 1f - (1f - p) * (1f - p)
            progress = eased
            if (p >= 1f) break
            delay(16)
        }
    }

    val percentage = (progress * 100).toInt().coerceIn(0, 100)
    val message = when {
        percentage < 25 -> "Menyiapkan aplikasi…"
        percentage < 50 -> "Memuat materi…"
        percentage < 75 -> "Menyiapkan simulasi…"
        else -> "Hampir selesai…"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "loading_anim")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )

    val pulseAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha1"
    )

    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            initialStartOffset = androidx.compose.animation.core.StartOffset(1400),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )

    val pulseAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            initialStartOffset = androidx.compose.animation.core.StartOffset(1400),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF0F3A44), DeepObsidian),
                    radius = 1100f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Stage with pulsing rings, spinning dashed ring, and MRI center matching HTML design
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                // Pulse 1
                Box(
                    modifier = Modifier
                        .size((160 * pulseScale1).dp)
                        .background(CyanBright.copy(alpha = pulseAlpha1), CircleShape)
                )
                // Pulse 2
                Box(
                    modifier = Modifier
                        .size((160 * pulseScale2).dp)
                        .background(CyanBright.copy(alpha = pulseAlpha2), CircleShape)
                )

                // Spin dashed ring matching HTML spec
                Canvas(
                    modifier = Modifier
                        .size(200.dp)
                        .rotate(spinAngle)
                ) {
                    drawCircle(
                        color = CyanBright.copy(alpha = 0.9f),
                        radius = size.minDimension / 2f,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 160f), 0f)
                        )
                    )
                }

                // Center Teal Circle with clean MRI icon PNG using ContentScale.Fit
                Surface(
                    modifier = Modifier.size(150.dp),
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(3.dp, CyanBright)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF8AE5D9), Color(0xFF52B9D8))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mri_icon_bersih),
                            contentDescription = "MRI Icon Bersih",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Title: Belajar MRI (MRI in cyan) matching HTML
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Belajar",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "MRI",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanBright
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Radiologi Medis · MRI EDU",
                fontSize = 13.sp,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Progress Bar (Width 280.dp matching HTML)
            Column(modifier = Modifier.width(280.dp)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    shape = RoundedCornerShape(99.dp),
                    color = DarkNavySlate,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardSlateBorder)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .height(6.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF14917F), CyanBright)
                                    ),
                                    shape = RoundedCornerShape(99.dp)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$percentage%",
                        fontSize = 12.sp,
                        color = CyanBright,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
