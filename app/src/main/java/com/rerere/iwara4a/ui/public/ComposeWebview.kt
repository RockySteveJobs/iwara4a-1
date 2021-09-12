package com.rerere.iwara4a.ui.public

import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.rerere.iwara4a.model.session.Session
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.SelfId
import com.rerere.iwara4a.ui.local.LocalNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ComposeWebview(
    modifier: Modifier = Modifier,
    link: String,
    session: Session?,
    onTitleChange: (String) -> Unit
) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    var progress by remember {
        mutableStateOf(0)
    }
    val webView = remember {
        WebView(context).apply {
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    progress = newProgress
                }

                override fun onReceivedTitle(view: WebView, title0: String) {
                    onTitleChange(title0)
                }
            }
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true

            session?.let {
                CookieManager.getInstance().let { manager ->
                    manager.acceptCookie()
                    manager.acceptThirdPartyCookies(this)
                    manager.setCookie(
                        ".iwara.tv",
                        it.toString()
                    )
                }
            }

            loadUrl(link)
        }
    }

    BackHandler {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            navController.popBackStack()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                webView
            }
        )

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopCenter),
            visible = progress < 100
        ) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), progress = progress / 100f)
        }
    }
}