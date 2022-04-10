package com.rerere.iwara4a.ui.modifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import coil.compose.AsyncImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}

@Deprecated(
    message = "shimmer似乎有问题，某些情况下无法对visible做出反应，导致持续处于visible状态",
    replaceWith = ReplaceWith("")
)
fun Modifier.coilShimmer(painter: AsyncImagePainter): Modifier = composed {
    this.placeholder(
        visible = painter.state is AsyncImagePainter.State.Loading,
        highlight = PlaceholderHighlight.shimmer()
    )
}