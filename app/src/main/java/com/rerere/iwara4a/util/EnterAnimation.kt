package com.rerere.iwara4a.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@ExperimentalAnimationApi
@Composable
@Deprecated("no longer needed")
fun EnterAnimation(content: @Composable () -> Unit) {
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    AnimatedVisibility(
        modifier = Modifier.fillMaxSize(),
        visibleState = state,
        enter = fadeIn(initialAlpha = 0.3f, animationSpec = tween(800)),
        exit = fadeOut()
    ) {
        content()
    }
}