package com.rerere.iwara4a.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.rerere.iwara4a.ui.local.LocalNavController

@Composable
fun Md3TopBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    appBarStyle: AppBarStyle = AppBarStyle.Small,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val colors = when (appBarStyle) {
        AppBarStyle.Small -> TopAppBarDefaults.smallTopAppBarColors()
        AppBarStyle.Medium -> TopAppBarDefaults.mediumTopAppBarColors()
        AppBarStyle.Large -> TopAppBarDefaults.largeTopAppBarColors()
        AppBarStyle.CenterAligned -> TopAppBarDefaults.centerAlignedTopAppBarColors()
    }
    when (appBarStyle) {
        AppBarStyle.Small -> {
            TopAppBar(
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
                colors = colors,
                scrollBehavior = scrollBehavior
            )
        }
        AppBarStyle.Medium -> {
            MediumTopAppBar(
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
                colors = colors,
                scrollBehavior = scrollBehavior
            )
        }
        AppBarStyle.Large -> {
            LargeTopAppBar(
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
                colors = colors,
                scrollBehavior = scrollBehavior
            )
        }
        AppBarStyle.CenterAligned -> {
            CenterAlignedTopAppBar(
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
                colors = colors,
                scrollBehavior = scrollBehavior
            )
        }
    }
}

@Composable
fun BackIcon() {
    val navController = LocalNavController.current
    IconButton(
        onClick = {
            navController.popBackStack()
        }
    ) {
        Icon(Icons.Outlined.ArrowBack, null)
    }
}


@Composable
fun SimpleIwaraTopBar(
    title: String
) {
    Md3TopBar(
        appBarStyle = AppBarStyle.Small,
        title = {
            Text(text = title)
        },
        navigationIcon = {
            BackIcon()
        }
    )
}

/**
 * 顶栏样式
 */
enum class AppBarStyle {
    Small,
    CenterAligned,
    Medium,
    Large
}