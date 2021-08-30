package com.rerere.iwara4a.util

import kotlin.math.roundToInt

fun lerp(
    start: Float,
    stop: Float,
    fraction: Float
): Float {
    return start + fraction * (stop - start)
}

fun Float.format() = (this * 1000).roundToInt() / 1000.0
fun Float.formatToString() = format().toString()