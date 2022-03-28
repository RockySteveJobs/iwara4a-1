package com.rerere.iwara4a.ui.component

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import com.rerere.iwara4a.ui.local.LocalNavController

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
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    request?.url?.let {
                        if (it.host == "ecchi.iwara.tv") {
                            val path = it.path ?: ""
                            if(path.startsWith("/videos/")) {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("iwara4a://video/${path.substringAfter("/videos/")}")
                                }
                                if(context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                                    context.startActivity(intent)
                                    return true
                                }
                            }
                        }
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }
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