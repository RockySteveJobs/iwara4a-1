package com.rerere.iwara4a.ui.screen.image

import android.content.ContentValues
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.model.detail.image.ImageDetail
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.MediaRepo
import com.rerere.iwara4a.util.okhttp.await
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val mediaRepo: MediaRepo
) : ViewModel() {
    var imageDetail by mutableStateOf(ImageDetail.LOADING)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf(false)

    fun load(imageId: String) = viewModelScope.launch {
        isLoading = true
        error = false

        val response = mediaRepo.getImageDetail(sessionManager.session, imageId)
        if (response.isSuccess()) {
            imageDetail = response.read()
        } else {
            error = true
        }

        isLoading = false
    }

    fun saveImages(onFinished: () -> Unit) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val okHttpClient =
                        OkHttpClient.Builder().readTimeout(Duration.ofSeconds(10)).build()
                    val contentResolver = AppContext.instance.contentResolver
                    imageDetail.imageLinks.forEach { link ->
                        try {
                            val request = Request.Builder().url(link).build()
                            val response = okHttpClient.newCall(request).await()
                            require(response.isSuccessful)
                            val inputStream = response.body!!.byteStream()
                            val paddingValues = ContentValues().apply {
                                put(MediaStore.MediaColumns.DISPLAY_NAME, "${imageDetail.title}.png")
                                put(MediaStore.MediaColumns.MIME_TYPE, "image/*")
                                // put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                            }
                            // TODO: 为什么安卓的MediaStore这么脑瘫啊？？？？
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                onFinished()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}