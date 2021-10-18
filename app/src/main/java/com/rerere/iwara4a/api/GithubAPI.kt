package com.rerere.iwara4a.api

import android.util.Log
import com.google.gson.Gson
import com.rerere.iwara4a.model.github.GithubRelease
import com.rerere.iwara4a.util.okhttp.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

private const val TAG = "GithubAPI"

class GithubAPI @Inject constructor(
    val okHttpClient: OkHttpClient
) {
    suspend fun getLatestRelease(): Response<GithubRelease> = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "getLatestRelease: Checking update...")

            val request = Request.Builder()
                .url("https://api.github.com/repos/re-ovo/iwara4a/releases/latest")
                .get()
                .build()

            val response = okHttpClient.newCall(request).await()
            require(response.isSuccessful)
            require(response.body != null)
            val githubRelease = Gson().fromJson(response.body?.string(), GithubRelease::class.java)
            Response.success(githubRelease)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.failed(e.javaClass.simpleName)
        }
    }
}