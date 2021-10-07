package com.canli.oya.newsreader.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import com.canli.oya.newsreader.ui.theme.Red500
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@Preview
@Composable
fun SplashAnimation() {
    var visible by remember {
        mutableStateOf(false)
    }

    LeftLine(visible = visible)

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = "randomKey") {
        coroutineScope.launch {
            delay(500)
            visible = true
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun LeftLine(visible: Boolean) {
    AnimatedVisibility(
        visible,
        enter = slideInVertically(
            // Enters by sliding down from offset -fullHeight to 0.
            initialOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
        ),
    ) {
        SimpleVerticalLine()
    }
}

@Composable
fun SimpleVerticalLine() {
    Canvas(modifier = Modifier.fillMaxHeight()) {
        val canvasHeight = size.height

        drawLine(
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = 0f, y = canvasHeight),
            color = Red500,
            strokeWidth = 3F
        )
    }
}
