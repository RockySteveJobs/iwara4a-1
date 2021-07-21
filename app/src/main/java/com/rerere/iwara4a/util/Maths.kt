package com.rerere.iwara4a.util

fun lerp(
    start: Float,
    stop: Float,
    fraction: Float
) : Float {
    return start + fraction * (stop - start)
}