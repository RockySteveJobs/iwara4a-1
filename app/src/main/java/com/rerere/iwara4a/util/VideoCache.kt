package com.rerere.iwara4a.util

import android.content.Context
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

/**
 * 视频缓存
 */
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
                .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
                .setCacheKeyFactory {
                    it.key ?: it.uri.toString()
                        .substringAfter("file=")
                        .substringBefore("&")
                }
        }
        return videoCache!!
    }
}