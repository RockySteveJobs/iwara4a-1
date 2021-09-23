package com.rerere.iwara4a.ui.public

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import soup.compose.material.motion.MaterialFade

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ListSnapToTop(
    listState: LazyListState,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Box(modifier = Modifier.wrapContentSize()) {
        content()
        AnimatedVisibility(
            visible = listState.firstVisibleItemIndex > 4,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        if (listState.firstVisibleItemIndex < 100) {
                            listState.animateScrollToItem(0)
                        } else {
                            listState.scrollToItem(0)
                        }
                        Toast.makeText(context, "已回到顶部", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Icon(Icons.Default.ArrowUpward, null)
            }
        }
    }
}