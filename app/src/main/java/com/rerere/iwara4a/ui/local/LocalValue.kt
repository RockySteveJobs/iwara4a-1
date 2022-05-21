package com.rerere.iwara4a.ui.local

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import com.rerere.iwara4a.data.model.user.Self

val LocalNavController = compositionLocalOf<NavController> {
    error("Not Init")
}

val LocalSelfData = compositionLocalOf { Self.GUEST }

val LocalDarkMode = compositionLocalOf { false }