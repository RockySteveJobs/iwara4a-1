package com.rerere.iwara4a.ui.modifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import me.rerere.compose_setting.preference.rememberBooleanPreference

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

fun Modifier.nsfw() = composed {
    val demoMode by rememberBooleanPreference(
        key = "demoMode",
        default = false
    )
    if(demoMode) this.blur(5.dp) else Modifier
}