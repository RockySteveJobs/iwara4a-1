package com.rerere.iwara4a.ui.screen.download

import androidx.lifecycle.ViewModel
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.dao.DownloadedVideoDao
import com.rerere.iwara4a.model.download.DownloadingVideo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel
@Inject constructor(
) : ViewModel() {
    val dao: DownloadedVideoDao = AppContext.database.getDownloadedVideoDao()

    val downloading = DownloadingList.downloading.receiveAsFlow()
}

object DownloadingList {
    val downloading = Channel<List<DownloadingVideo>>()
}