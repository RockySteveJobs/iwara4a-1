package com.rerere.iwara4a.ui.screen.self

import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.rerere.iwara4a.R
import com.rerere.iwara4a.repo.SelfId
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.local.LocalSelfData

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SelfScreen(
    selfViewModel: SelfViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    var progress by remember {
        mutableStateOf(0)
    }
    Scaffold(
        topBar = {
            Md3TopBar(
                title = {
                    Text(text = stringResource(id = R.string.screen_self_topbar_title))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) {
        val self = LocalSelfData.current
        Box {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    WebView(it).apply {
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                progress = newProgress
                            }
                        }
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true

                        CookieManager.getInstance().let { manager ->
                            manager.acceptCookie()
                            manager.acceptThirdPartyCookies(this)
                            manager.setCookie(
                                ".iwara.tv",
                                selfViewModel.sessionManager.session.toString()
                            )
                        }

                        loadUrl("https://ecchi.iwara.tv/user/${self.numId}/edit")
                    }
                }
            )

            AnimatedVisibility(
                modifier = Modifier.align(Alignment.TopCenter),
                visible = progress < 100
            ) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(),progress = progress / 100f)
            }
        }
    }
}