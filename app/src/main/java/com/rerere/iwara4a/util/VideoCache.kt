package com.rerere.iwara4a.util

import android.content.Context
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.rerere.iwara4a.util.okhttp.SmartDns
import com.rerere.iwara4a.util.okhttp.UserAgentInterceptor
import okhttp3.OkHttpClient
import java.io.File

object VideoCache {
    private const val MAX_SIZE = 1024 * 1024 * 512L // 512 M

    private var videoCache: DataSource.Factory? = null

    fun getCache(context: Context): DataSource.Factory {
        if (videoCache == null) {
            val directory = File(context.cacheDir, "exo")
            val cache = SimpleCache(
                directory,
                LeastRecentlyUsedCacheEvictor(MAX_SIZE), // LRU 缓存算法
                StandaloneDatabaseProvider(context)
            )
            videoCache = CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(OkHttpDataSource.Factory(
                    OkHttpClient.Builder()
                        .dns(SmartDns)
                        .retryOnConnectionFailure(true)
                        .addInterceptor(UserAgentInterceptor())
                        .build()
                ))
                .setCacheKeyFactory {
                    it.key ?: it.uri.toString()
                        .substringAfter("file=")
                        .substringBefore("&")
                }
        }
        return videoCache!!
    }
}