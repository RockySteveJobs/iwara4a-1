package com.rerere.iwara4a.ui.screen.about

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.component.AppBarStyle
import com.rerere.iwara4a.ui.component.BackIcon
import com.rerere.iwara4a.ui.component.Centered
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.util.openUrl

private val ThirdPartyLibraries = listOf(
    "Accompanist" to "https://github.com/google/accompanist",
    "Okhttp" to "https://github.com/square/okhttp",
    "Retrofit" to "https://github.com/square/retrofit",
    "Hilt" to "https://dagger.dev/hilt/",
    "Java-Websocket" to "https://github.com/TooTallNate/Java-WebSocket",
    "XLog" to "https://github.com/elvishew/xLog",
    "DKPlayer" to "https://github.com/Doikki/DKVideoPlayer",
    "compose-material-dialogs" to "https://github.com/vanpra/compose-material-dialogs",
    "Coil" to "https://github.com/coil-kt/coil",
    "Jsoup" to "https://jsoup.org/"
)

@Composable
fun AboutScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec = rememberSplineBasedDecay()
    )
    Scaffold(
        topBar = {
            Md3TopBar(
                title = {
                    Text("关于")
                },
                scrollBehavior = scrollBehavior,
                appBarStyle = AppBarStyle.Large,
                navigationIcon = {
                    BackIcon()
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = WindowInsets.navigationBars.asPaddingValues()
        ) {
            item {
                Centered(Modifier.fillMaxWidth()) {
                    Image(
                        modifier = Modifier
                            .size(100.dp),
                        painter = painterResource(R.drawable.compose_logo),
                        contentDescription = null
                    )
                }
            }

            item {
                Category(
                    modifier = Modifier.fillMaxWidth(),
                    title = {
                        Text("Overview")
                    }
                ) {
                    Text("完全基于Jetpack Compose开发的 iwara 安卓app, 采用Material You设计, 支持安卓6.0以上版本, 无多余权限请求")
                }
            }

            item {
                Category(
                    modifier = Modifier.fillMaxWidth(),
                    title = {
                        Text("Open Source Libraries")
                    }
                ) {
                    ThirdPartyLibraries.forEach {
                        ThirdPartyLibrary(it.first, it.second)
                    }
                }
            }
        }
    }
}

@Composable
private fun Category(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProvideTextStyle(TextStyle(
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp
        )) {
            title()
        }
        content()
    }
}

@Composable
private fun ThirdPartyLibrary(
    name: String,
    link: String
) {
    val context = LocalContext.current
    Card(
        onClick = {
            context.openUrl(link)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
            Text(
                text = link,
                maxLines = 1
            )
        }
    }
}