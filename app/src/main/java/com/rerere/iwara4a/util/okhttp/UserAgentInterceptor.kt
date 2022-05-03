package com.rerere.iwara4a.util.okhttp

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

const val USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36"

class UserAgentInterceptor(private val userAgent: String = USER_AGENT) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val userAgentRequest: Request = chain.request()
            .newBuilder()
            .header("User-Agent", userAgent)
            .build()
        return chain.proceed(userAgentRequest)
    }
}