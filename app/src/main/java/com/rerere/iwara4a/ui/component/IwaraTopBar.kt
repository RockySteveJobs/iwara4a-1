package com.rerere.iwara4a.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.rerere.iwara4a.ui.local.LocalNavController

@Composable
fun Md3TopBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = WindowInsets.statusBars.asPaddingValues(),
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.smallTopAppBarColors(),
    appBarStyle: AppBarStyle = AppBarStyle.Small,
    scrollBehavior: TopAppBarScrollBehavior? = null
){
    val scrollFraction = scrollBehavior?.scrollFraction ?: 0f
    val appBarContainerColor by colors.containerColor(scrollFraction)

    Surface(modifier = modifier, color = appBarContainerColor) {
        when(appBarStyle){
            AppBarStyle.Small -> {
                SmallTopAppBar(
                    modifier = Modifier.padding(contentPadding),
                    title = title,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    colors = colors,
                    scrollBehavior = scrollBehavior
                )
            }
            AppBarStyle.Medium -> {
                MediumTopAppBar(
                    modifier = Modifier.padding(contentPadding),
                    title = title,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    colors = colors,
                    scrollBehavior = scrollBehavior
                )
            }
            AppBarStyle.Large -> {
                LargeTopAppBar(
                    modifier = Modifier.padding(contentPadding),
                    title = title,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    colors = colors,
                    scrollBehavior = scrollBehavior
                )
            }
        }
    }
}

@Composable
fun Md3BottomNavigation(
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        tonalElevation = 3.dp
    ) {
        CompositionLocalProvider(
            LocalAbsoluteTonalElevation provides LocalAbsoluteTonalElevation.current - 3.dp
        ) {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding()
            ) {
                content()
            }
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
        Icon(Icons.Rounded.ArrowBack, null)
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
    Medium,
    Large
}