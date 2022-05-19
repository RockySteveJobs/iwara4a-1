package com.rerere.iwara4a.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun <T> rememberMutableState(value: T) = remember {
    mutableStateOf(value)
}