package com.rerere.iwara4a.data.api.google

import com.google.gson.JsonParser
import com.rerere.iwara4a.util.okhttp.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*
import javax.inject.Inject

class TranslatorAPI @Inject constructor(
    private val httpClient: OkHttpClient
) {
    suspend fun translate(text: String): String? {
        val targetLanguage = when (Locale.getDefault().language) {
            Locale.SIMPLIFIED_CHINESE.language -> "zh"
            Locale.JAPANESE.language -> "ja"
            Locale.KOREAN.language -> "ko"
            else -> "en"
        }
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=$targetLanguage&dt=t&q=$text")
                .get()
                .build()
            try {
                val response = httpClient.newCall(request).await()
                val result = JsonParser().parse(response.body!!.string())
                    .asJsonArray[0]
                    .asJsonArray
                    .joinToString(
                        separator = ""
                    ) {
                        it.asJsonArray[0].asString
                    }
                result
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}