package com.rerere.iwara4a

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.rerere.iwara4a.ui.activity.CrashActivity
import com.rerere.iwara4a.util.initComposeHacking
import com.rerere.iwara4a.util.okhttp.SmartDns
import com.rerere.iwara4a.util.okhttp.UserAgentInterceptor
import dagger.hilt.android.HiltAndroidApp
import me.rerere.compose_setting.preference.initComposeSetting
import okhttp3.OkHttpClient
import xcrash.XCrash
import java.io.File

@HiltAndroidApp
class AppContext : Application(), ImageLoaderFactory {
    companion object {
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Crash Handler
        XCrash.init(
            this, XCrash.InitParameters()
                .setAppVersion(BuildConfig.VERSION_NAME)
                .setLogDir(
                    getExternalFilesDir("crash")?.path
                )
                .setNativeCallback { logPath, emergency ->
                    val file = File(logPath)
                    startActivity(
                        Intent(instance, CrashActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            putExtra("stackTrace", file.readLines().joinToString("\n"))
                        }
                    )
                    file.deleteOnExit()
                }
                .setAnrCallback { logPath, _ ->
                    val file = File(logPath)
                    startActivity(
                        Intent(instance, CrashActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            putExtra("stackTrace", file.readLines().joinToString("\n"))
                        }
                    )
                    file.deleteOnExit()
                }
                .setJavaCallback { logPath, _ ->
                    val file = File(logPath)
                    startActivity(
                        Intent(instance, CrashActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            putExtra("stackTrace", file.readLines().joinToString("\n"))
                        }
                    )
                    file.deleteOnExit()
                }
        )

        // Init MMKV
        initComposeSetting()

        // Init Compose Hacking
        initComposeHacking()
            .onFailure {
                Toast.makeText(this, "Failed to inject compose hacking", Toast.LENGTH_SHORT).show()
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
}

/**
 * 使用顶层函数直接获取 SharedPreference
 *
 * @param name SharedPreference名字
 * @return SharedPreferences实例
 */
fun sharedPreferencesOf(name: String): SharedPreferences =
    AppContext.instance.getSharedPreferences(name, Context.MODE_PRIVATE)