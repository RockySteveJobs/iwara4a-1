package com.rerere.iwara4a

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.XLog
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.rerere.iwara4a.ui.activity.CrashActivity
import com.rerere.iwara4a.util.LogEntry
import com.rerere.iwara4a.util.initComposeHacking
import com.rerere.iwara4a.util.okhttp.SmartDns
import com.rerere.iwara4a.util.okhttp.UserAgentInterceptor
import com.rerere.iwara4a.util.toast
import dagger.hilt.android.HiltAndroidApp
import me.rerere.compose_setting.preference.initComposeSetting
import okhttp3.OkHttpClient
import xcrash.ICrashCallback
import xcrash.XCrash
import java.io.File
import kotlin.time.Duration.Companion.days

@HiltAndroidApp
class AppContext : Application(), ImageLoaderFactory {
    companion object {
        @JvmStatic
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // xLog
        XLog.init(
            LogConfiguration.Builder()
                .disableThreadInfo()
                .build(),
            AndroidPrinter(true),
            FilePrinter.Builder(cacheDir.resolve("logs").path)
                .fileNameGenerator(DateFileNameGenerator())
                .backupStrategy(NeverBackupStrategy())
                .cleanStrategy(FileLastModifiedCleanStrategy(15.days.inWholeMilliseconds))
                .flattener { timeMillis, logLevel, tag, message ->
                    LogEntry(
                        time = timeMillis,
                        level = logLevel,
                        message = message,
                        thread = Thread.currentThread().name,
                        tag = tag
                    ).toString()
                }
                .build()
        )

        // Init MMKV
        initComposeSetting()

        // Init Compose Hacking
        initComposeHacking()
            .onFailure {
                toast("Failed to inject compose hacking")
            }
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
                    .maxSizePercent(0.05)
                    .build()
            }
            .build()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        // xCrash Handler
        val handler = ICrashCallback { logPath, _ ->
            val file = File(logPath)
            startActivity(
                Intent(this, CrashActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("stackTrace", file.readLines().joinToString("\n"))
                }
            )
            file.deleteOnExit()
        }
        XCrash.init(
            this, XCrash.InitParameters()
                .setAppVersion(BuildConfig.VERSION_NAME)
                .setLogDir(
                    getExternalFilesDir("crash")?.path
                )
                .setNativeCallback(handler)
                .setAnrCallback(handler)
                .setJavaCallback(handler)
        )
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