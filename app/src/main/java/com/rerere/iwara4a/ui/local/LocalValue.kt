package com.rerere.iwara4a.ui.local

import android.content.res.Configuration
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

val LocalScreenOrientation = compositionLocalOf { Configuration.ORIENTATION_PORTRAIT }

val LocalNavController = compositionLocalOf<NavController> {
    error("Not Init")
}