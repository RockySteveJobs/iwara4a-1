@file:Suppress("UNCHECKED_CAST")

package com.rerere.iwara4a.ui.util

import androidx.compose.runtime.saveable.Saver
import java.util.*

private const val MAX_SIZE = 100
private val cache = hashMapOf<String, MemorySaverEntry<Any>>()

/**
 * 基于内存的Saver
 */
fun <T : Any> memSaver() = Saver<T, String>(
    save = {
        UUID.randomUUID().toString().also { key ->
            cache[key] = MemorySaverEntry(it, System.currentTimeMillis())
        }
    },
    restore = { key ->
        val value = cache[key]?.value

        // 移出map
        cache.remove(key)

        // 限制最大数量
        if(cache.size > MAX_SIZE) {
            val removes = cache.size - MAX_SIZE
            cache.entries
                .sortedBy { entry -> entry.value.saveTime }
                .take(removes)
                .forEach {
                    cache.remove(it.key)
                }
        }

        value as? T
    }
)

private class MemorySaverEntry<T>(
    val value: T,
    val saveTime: Long
)