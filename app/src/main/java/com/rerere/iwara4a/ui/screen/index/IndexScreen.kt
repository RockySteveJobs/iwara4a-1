package com.rerere.iwara4a.ui.screen.index

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.outlined.FeaturedVideo
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.public.AppBarStyle
import com.rerere.iwara4a.ui.public.Md3BottomNavigation
import com.rerere.iwara4a.ui.public.Md3TopBar
import com.rerere.iwara4a.ui.screen.index.page.ImageListPage
import com.rerere.iwara4a.ui.screen.index.page.RecommendPage
import com.rerere.iwara4a.ui.screen.index.page.SubPage
import com.rerere.iwara4a.ui.screen.index.page.VideoListPage
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.getVersionName
import com.rerere.iwara4a.util.openUrl
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch

@Composable
fun IndexScreen(navController: NavController, indexViewModel: IndexViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(0)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    NavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            IndexDrawer(navController, indexViewModel, drawerState)
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(drawerState, indexViewModel)
            },
            bottomBar = {
                BottomBar(
                    currentPage = pagerState.currentPage,
                    scrollToPage = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(it)
                        }
                    }
                )
            }
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                count = 4
            ) { page ->
                when (page) {
                    0 -> {
                        SubPage(navController, indexViewModel)
                    }
                    1 -> {
                        RecommendPage(indexViewModel)
                    }
                    2 -> {
                        VideoListPage(navController, indexViewModel)
                    }
                    3 -> {
                        ImageListPage(navController, indexViewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(drawerState: DrawerState, indexViewModel: IndexViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val context = LocalContext.current
    val updateDialog = rememberMaterialDialogState()
    val currentVersion = remember {
        context.getVersionName()
    }
    val update by indexViewModel.updateChecker.collectAsState()
    MaterialDialog(
        dialogState = updateDialog,
        buttons = {
            button(stringResource(id = R.string.screen_index_button_update_github)) {
                updateDialog.hide()
                context.openUrl("https://github.com/re-ovo/iwara4a/releases/latest")
            }
            button(stringResource(id = R.string.screen_index_button_update_neglect)) {
                updateDialog.hide()
            }
        }
    ) {
        if (update is DataState.Success) {
            title("${stringResource(id = R.string.screen_index_update_title)}: ${update.read().name}")
            message("${stringResource(id = R.string.screen_index_update_message)}:\n${update.read().body}")
        }
    }
    Md3TopBar(
        appBarStyle = AppBarStyle.Small,
        title = {
            Text(text = stringResource(R.string.app_name))
        },
        navigationIcon = {
            IconButton(onClick = {
                coroutineScope.launch {
                    drawerState.open()
                }
            }) {
                val painter = rememberImagePainter(indexViewModel.self.profilePic)
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .placeholder(
                            visible = painter.state is ImagePainter.State.Loading,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painter,
                        contentDescription = null
                    )
                }
            }
        },
        actions = {
            AnimatedVisibility(visible = update is DataState.Success && update.read().name != currentVersion) {
                IconButton(onClick = {
                    updateDialog.show()
                }) {
                    Icon(Icons.Default.Update, null)
                }
            }

            IconButton(onClick = {
                navController.navigate("message")
            }) {
                BadgedBox(
                    badge = {
                        androidx.compose.animation.AnimatedVisibility(visible = indexViewModel.self.messages > 0) {
                            Badge {
                                Text(text = indexViewModel.self.messages.toString())
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Message, null)
                }
            }

            IconButton(onClick = { navController.navigate("search") }) {
                Icon(Icons.Default.Search, null)
            }
        }
    )
}

@Composable
private fun BottomBar(currentPage: Int, scrollToPage: (Int) -> Unit) {
    Md3BottomNavigation {
        NavigationBarItem(
            selected = currentPage == 0,
            onClick = {
                scrollToPage(0)
            },
            icon = {
                Icon(imageVector = Icons.Outlined.Subscriptions, contentDescription = null)
            },
            label = {
                Text(text = stringResource(R.string.screen_index_bottom_sub))
            }
        )
        NavigationBarItem(
            selected = currentPage == 1,
            onClick = {
                scrollToPage(1)
            },
            icon = {
                Icon(imageVector = Icons.Outlined.Sort, contentDescription = null)
            },
            label = {
                Text(text = stringResource(R.string.screen_index_bottom_sort))
            }
        )

        NavigationBarItem(
            selected = currentPage == 2,
            onClick = {
                scrollToPage(2)
            },
            icon = {
                Icon(imageVector = Icons.Outlined.FeaturedVideo, contentDescription = null)
            },
            label = {
                Text(text = stringResource(R.string.screen_index_bottom_video))
            }
        )

        NavigationBarItem(
            selected = currentPage == 3,
            onClick = {
                scrollToPage(3)
            },
            icon = {
                Icon(imageVector = Icons.Outlined.Image, contentDescription = null)
            },
            label = {
                Text(text = stringResource(R.string.screen_index_bottom_image))
            }
        )
    }
}