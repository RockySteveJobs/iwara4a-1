package com.rerere.iwara4a.ui.screen.message

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.component.BackIcon
import com.rerere.iwara4a.ui.component.ComposeWebview
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.util.stringResource

@Composable
fun MessageScreen(
    messageViewModel: MessageViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    var title by remember {
        mutableStateOf(context.stringResource(id = R.string.screen_message_topbar_title))
    }
    Scaffold(topBar = {
        Md3TopBar(
            title = {
                Text(text = title)
            },
            navigationIcon = {
                BackIcon()
            }
        )
    }) {
        ComposeWebview(
            link = "https://ecchi.iwara.tv/messages",
            session = messageViewModel.sessionManager.session
        ) {
            title = it
        }
    }
}