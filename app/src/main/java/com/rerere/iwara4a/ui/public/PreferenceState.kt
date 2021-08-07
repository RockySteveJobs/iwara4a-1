package com.rerere.iwara4a.ui.public

import androidx.compose.runtime.*
import androidx.core.content.edit
import com.rerere.iwara4a.sharedPreferencesOf

@Composable
fun rememberBooleanPreferenceState(key: String, init: Boolean = true): MutableState<Boolean> {
    val sharedPreferences = remember {
        sharedPreferencesOf(key)
    }
    val state = remember {
        mutableStateOf(sharedPreferences.getBoolean(key, init))
    }
    return remember {
        object : MutableState<Boolean> {
            override var value: Boolean
                get() = state.value
                set(value) {
                    state.value = value
                    sharedPreferences.edit {
                        putBoolean(key, value)
                    }
                }

            override fun component1(): Boolean = value

            override fun component2(): (Boolean) -> Unit = {
                value = it
            }
        }
    }
}