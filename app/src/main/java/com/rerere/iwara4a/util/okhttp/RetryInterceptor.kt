package com.rerere.iwara4a.util.okhttp

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class Retry(
    private val maxRetryTimes: Int = 3
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = try {
            chain.proceed(
                request.newBuilder().build()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        var retryNum = 0
        while (response?.isSuccessful != true && retryNum < maxRetryTimes) {
            retryNum++
            Log.i("RetryInterceptor", "retry ${request.url} for the $retryNum time")
            response = try {
                chain.proceed(request.newBuilder().build())
            } catch (e: Exception) {
                null
            }
        }
        return response ?: throw IOException("no response at all")
    }
}