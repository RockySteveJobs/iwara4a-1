package com.rerere.iwara4a.ui.screen.forum

import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.ui.component.BackIcon
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.util.stringResource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun ForumScreen(forumViewModel: ForumViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    var progress by remember {
        mutableStateOf(0)
    }
    var title by remember {
        mutableStateOf(context.stringResource(id = R.string.screen_forum_title))
    }
    Scaffold(
        topBar = {
            Md3TopBar(
                title = {
                    Text(text = title)
                },
                navigationIcon = {
                    BackIcon()
                }
            )
        }
    ) {
        Box(Modifier.padding(it)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    WebView(it).apply {
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                progress = newProgress
                            }

                            override fun onReceivedTitle(view: WebView?, title0: String?) {
                                title = title0 ?: context.stringResource(id = R.string.screen_forum_title)
                            }
                        }
                        webViewClient =  WebViewClient()
                        settings.javaScriptEnabled = true

                        CookieManager.getInstance().let { manager ->
                            manager.acceptCookie()
                            manager.acceptThirdPartyCookies(this)
                            manager.setCookie(
                                ".iwara.tv",
                                forumViewModel.sessionManager.session.toString()
                            )
                        }

                        loadUrl("https://ecchi.iwara.tv/forum")
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

@HiltViewModel
class ForumViewModel @Inject constructor(
    val sessionManager: SessionManager
) : ViewModel()