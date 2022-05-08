package com.rerere.iwara4a.ui.states

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberPrimaryClipboardState(): MutableState<ClipData?> {
    val context = LocalContext.current
    val service = remember {
        context.getSystemService(ClipboardManager::class.java)
    }
    val state = remember {
        mutableStateOf(service.primaryClip)
    }
    DisposableEffect(Unit) {
        val listener = ClipboardManager.OnPrimaryClipChangedListener {
            state.value = service.primaryClip
        }
        service.addPrimaryClipChangedListener(listener)
        onDispose {
            service.removePrimaryClipChangedListener(listener)
        }
    }
    return object : MutableState<ClipData?> {
        override var value: ClipData?
            get() = state.value
            set(value) {
                value?.let {
                    service.setPrimaryClip(it)
                } ?: kotlin.run {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        service.clearPrimaryClip()
                    } else {
                        service.setPrimaryClip(ClipData.newPlainText("",""))
                    }
                }
            }

        override fun component1(): ClipData? = value

        override fun component2(): (ClipData?) -> Unit = { value = it }
    }
}