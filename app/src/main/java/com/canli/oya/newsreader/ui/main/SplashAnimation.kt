package com.canli.oya.newsreader.ui.main

import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.canli.oya.newsreader.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@Composable
fun AnimatedSplashScreen(
    modifier: Modifier = Modifier,
    rectWidth: Dp = 250.dp,
    rectHeight: Dp = 100.dp,
    strokeWidth: Float = 10f
) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var firstAnimStarted by remember { mutableStateOf(false) }
    var secondAnimStarted by remember { mutableStateOf(false) }
    var thirdAnimStarted by remember { mutableStateOf(false) }
    var fourthAnimStarted by remember { mutableStateOf(false) }
    var textAnimStarted by remember { mutableStateOf(false) }

    val lineAnimDuration = 300
    val animInterval = 300L
    val textAnimDuration = 1000

    BoxWithConstraints(
        Modifier
            .fillMaxSize()
    ) {
        // Calculate x positions to center horizontally
        val rectWidthFloat = with(LocalDensity.current) {
            rectWidth.toPx()
        }
        val maxWidthFloat = with(LocalDensity.current) {
            maxWidth.toPx()
        }
        val x0: Float = (maxWidthFloat - rectWidthFloat) / 2
        val x1 = x0 + rectWidthFloat

        // Calculate y positions to center vertically
        val rectHeightFloat = with(LocalDensity.current) {
            rectHeight.toPx()
        }
        val maxHeightFloat = with(LocalDensity.current) {
            maxHeight.toPx()
        }
        val y0 = (maxHeightFloat - rectHeightFloat) / 2
        val y1 = y0 + rectHeightFloat

        val topRightCornerX by animateFloatAsState(
            targetValue = if (firstAnimStarted) x1 else 0f,
            animationSpec = TweenSpec(lineAnimDuration, easing = LinearEasing)
        )

        val topLeftCornerY by animateFloatAsState(
            targetValue = if (secondAnimStarted) y0 else maxHeightFloat,
            animationSpec = TweenSpec(lineAnimDuration, easing = LinearEasing)
        )

        val bottomLeftCornerX by animateFloatAsState(
            targetValue = if (thirdAnimStarted) x0 else maxWidthFloat,
            animationSpec = TweenSpec(lineAnimDuration, easing = LinearEasing)
        )

        val bottomRightCornerY by animateFloatAsState(
            targetValue = if (fourthAnimStarted) y1 else 0f,
            animationSpec = TweenSpec(lineAnimDuration, easing = LinearEasing)
        )

        val textAlpha by animateIntAsState(
            targetValue = if(textAnimStarted) 255 else 0,
            animationSpec = TweenSpec(textAnimDuration, easing = LinearEasing)
        )

        Canvas(modifier = modifier.fillMaxSize()) {

            // Top horizontal line
            drawLine(
                start = Offset(x = topRightCornerX - rectWidthFloat, y = y0),
                end = Offset(x = topRightCornerX, y = y0),
                color = Color.White,
                strokeWidth = strokeWidth
            )

            // Left vertical line
            drawLine(
                start = Offset(x = x0, y = topLeftCornerY),
                end = Offset(x = x0, y = topLeftCornerY + rectHeightFloat),
                color = Color.White,
                strokeWidth = strokeWidth
            )

            // Bottom horizontal line
            drawLine(
                start = Offset(x = bottomLeftCornerX, y = y1),
                end = Offset(x = bottomLeftCornerX + rectWidthFloat, y = y1),
                color = Color.White,
                strokeWidth = strokeWidth
            )

            // Right vertical line
            drawLine(
                start = Offset(x = x1, y = bottomRightCornerY - rectHeightFloat),
                end = Offset(x = x1, y = bottomRightCornerY),
                color = Color.White,
                strokeWidth = strokeWidth
            )

            // Draw text in the middle
            val textPaint = Paint().asFrameworkPaint().apply {
                isAntiAlias = true
                textSize = 34.sp.toPx()
                color = android.graphics.Color.WHITE
                alpha = textAlpha
                typeface = ResourcesCompat.getFont(context, R.font.montserrat_semibold)
            }

            val textPadding = 16.dp.toPx()

            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "NewsReader",
                    x0 + textPadding, // x coordinate of the starting point
                    (y1 - y0) * 0.65f + y0, // y coordinate of the baseline
                    textPaint
                )
            }
        }

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                delay(100)
                firstAnimStarted = true
                delay(animInterval)
                secondAnimStarted = true
                delay(animInterval)
                thirdAnimStarted = true
                delay(animInterval)
                fourthAnimStarted = true
                delay(animInterval)
                textAnimStarted = true
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun BrandText(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 2000, easing = LinearOutSlowInEasing)
        )
    ) {
        Text(
            text = "NewsReader",
            style = MaterialTheme.typography.h4,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun HorizontalLine(lineLength: Dp, strokeWidth: Float = 10f) {
    Canvas(modifier = Modifier.width(lineLength)) {
        val canvasWidth = size.width

        drawLine(
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = canvasWidth, y = 0f),
            color = Color.White,
            strokeWidth = strokeWidth
        )
    }
}
