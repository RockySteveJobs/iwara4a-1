package com.rerere.iwara4a

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.rerere.iwara4a.util.okhttp.SmartDns
import com.rerere.iwara4a.util.okhttp.UserAgentInterceptor
import dagger.hilt.android.HiltAndroidApp
import me.rerere.compose_setting.preference.initComposeSetting
import okhttp3.OkHttpClient
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory
import xyz.doikki.videoplayer.player.ProgressManager
import xyz.doikki.videoplayer.player.VideoViewConfig
import xyz.doikki.videoplayer.player.VideoViewManager
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class AppContext : Application(), ImageLoaderFactory {
    companion object {
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        initComposeSetting()

        // 初始化DKPlayer
        VideoViewManager.setConfig(
            VideoViewConfig.newBuilder()
                .setPlayerFactory(ExoMediaPlayerFactory.create())
                .setEnableAudioFocus(true)
                .setProgressManager(object : ProgressManager() {
                    // 使用内存缓存播放进度
                    private val cache = mutableMapOf<String, Long>()

                    override fun saveProgress(url: String?, progress: Long) {
                        if (url == null) return
                        runCatching {
                            val fileName = url.substringAfter("file=").substringBefore("&")
                            cache[fileName] = progress
                        }

                        // 好蠢, 这里应该使用基于时间的缓存算法
                        // Guava Cache 已经实现了这个功能，但是考虑包体积，暂时不使用
                        if (cache.size > 100) cache.clear()
                    }

                    override fun getSavedProgress(url: String?): Long {
                        if (url == null) return 0L
                        val fileName = runCatching {
                            url.substringAfter("file=").substringBefore("&")
                        }.getOrNull()
                        return cache[fileName] ?: 0L
                    }
                })
                .build()
        )

        // 初始化日志框架
        XLog.init(
            LogConfiguration.Builder()
                .tag("iwara4a")
                .logLevel(
                    if (BuildConfig.DEBUG) LogLevel.ALL
                    else LogLevel.WARN
                )
                .enableThreadInfo()
                .build(),
            FilePrinter.Builder(filesDir.absolutePath + "/log")
                .fileNameGenerator(DateFileNameGenerator())
                .backupStrategy(NeverBackupStrategy())
                .cleanStrategy(FileLastModifiedCleanStrategy(TimeUnit.DAYS.toMillis(3)))
                .build()
        )

        XLog.i("APP初始化完成")
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor(UserAgentInterceptor())
                    .retryOnConnectionFailure(true)
                    .dns(SmartDns)
                    .build()
            }
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("images"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
    }
}

/**
 * 使用顶层函数直接获取 SharedPreference
 *
 * @param name SharedPreference名字
 * @return SharedPreferences实例
 */
fun sharedPreferencesOf(name: String): SharedPreferences =
    AppContext.instance.getSharedPreferences(name, Context.MODE_PRIVATE)