package com.rerere.iwara4a.ui.screen.index.page

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.rerere.iwara4a.ui.public.MediaPreviewCard
import com.rerere.iwara4a.ui.public.PageList
import com.rerere.iwara4a.ui.public.rememberPageListPage
import com.rerere.iwara4a.ui.screen.index.IndexViewModel

@Composable
fun VideoListPage(navController: NavController, indexViewModel: IndexViewModel) {
    val pageListState = rememberPageListPage()
    PageList(
        state = pageListState,
        provider = indexViewModel.videoListPrvider,
        supportQueryParam = true
    ) {
        MediaPreviewCard(navController, it)
    }
}