package com.rerere.iwara4a.ui.public

import android.util.Log
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.rerere.iwara4a.util.openUrl

private const val TAG = "SmartLinkText"

@Composable
fun SmartLinkText(modifier: Modifier = Modifier, text: String, maxLines: Int = Int.MAX_VALUE) {
    val elements = text.parseUrls()
    val context = LocalContext.current
    ClickableText(
        modifier = modifier,
        text = buildAnnotatedString {
            elements.forEach {
                if (it.isUrl) {
                    withStyle(style = SpanStyle(color = Color.Blue)) {
                        append(it.text)
                    }
                } else {
                    append(it.text)
                }
            }
        },
        maxLines = maxLines
    ) {
        var cursor = 0
        for (item in elements) {
            if (it >= cursor && it < cursor + item.text.length) {
                if (item.isUrl) {
                    Log.i(TAG, "SmartLinkText: Clicked Url: ${item.text}")
                    context.openUrl(item.text)
                }
                break
            }
            cursor += item.text.length
        }
    }
}

private val REGEX =
    Regex("(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})")
// Regex("http[s]?://(?:(?!http[s]?://)[a-zA-Z]|[0-9]|[\$\\-_@.&+/]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+")

fun String.parseUrls(): List<TextElement> {
    val allLinks = REGEX.findAll(this).map { it.value }.toList()
    val elements = arrayListOf<TextElement>()
    var str = this
    allLinks.forEach {
        elements += TextElement(str.substring(0, str.indexOf(it)), false)
        elements += TextElement(str.substring(str.indexOf(it), str.indexOf(it) + it.length), true)
        str = str.substring(str.indexOf(it) + it.length)
    }
    elements += TextElement(str, false)
    return elements
}

data class TextElement(
    val text: String,
    val isUrl: Boolean
) {
    fun isImage() = isUrl && text.let {
        it.endsWith(".png") || it.endsWith(".jpg")
    }
}