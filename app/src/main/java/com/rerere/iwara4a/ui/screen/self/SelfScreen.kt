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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.rerere.iwara4a.repo.SelfId
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.public.IwaraTopBar

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
            IwaraTopBar(
                title = {
                    Text(text = "编辑个人信息")
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

                        loadUrl("https://ecchi.iwara.tv/user/$SelfId/edit")
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