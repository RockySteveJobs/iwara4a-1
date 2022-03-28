package com.rerere.iwara4a.ui.util

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun findCurrentActivity(): Activity {
    return LocalContext.current as Activity
}