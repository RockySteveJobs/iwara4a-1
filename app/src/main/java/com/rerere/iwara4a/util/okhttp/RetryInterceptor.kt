package com.rerere.iwara4a.util.okhttp

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class Retry(
    val maxRetryTimes: Int = 3
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        var retryNum = 0
        while (!response.isSuccessful && retryNum < maxRetryTimes) {
            retryNum++
            Log.i("RetryInterceptor", "retry num:$retryNum")
            response = chain.proceed(request)
        }
        return response
    }
}