package com.rerere.iwara4a.util

import android.util.Log
import com.rerere.iwara4a.BuildConfig

private const val TAG = "Util"

inline fun debug(scope: ()->Unit) {
    if(BuildConfig.DEBUG){
        scope()
    }
}

inline fun <R> codeRunDuration(name: String, code: () -> R): R{
    val start = System.currentTimeMillis()
    val result = code.invoke()
    Log.i("Util", "codeRunDuration[$name]: ${System.currentTimeMillis() - start}")
    return result
}