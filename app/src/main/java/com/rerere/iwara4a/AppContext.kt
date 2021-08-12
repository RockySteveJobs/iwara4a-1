package com.rerere.iwara4a

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.rerere.iwara4a.dao.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class AppContext : Application() {
    companion object {
        lateinit var instance: Application
        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        val database by lazy {
            Room.databaseBuilder(
                instance,
                AppDatabase::class.java,
                "iwaradb"
            ).build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
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