package com.rerere.iwara4a.ui.util

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

@Composable
@ReadOnlyComposable
fun stringResourceByName(name: String): String {
    val contex = LocalContext.current
    val resources = resources()
    val id = resources.getIdentifier(name, "string", contex.packageName)
    if(id == 0) return name
    return resources.getString(
        id
    )
}

@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}