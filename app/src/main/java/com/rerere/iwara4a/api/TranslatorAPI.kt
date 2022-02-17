package com.rerere.iwara4a.api

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.okhttp.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class TranslatorAPI @Inject constructor(
    private val httpClient: OkHttpClient
) {
    private val gson = Gson()

    fun translate(text: String): Flow<DataState<String>> = flow {
        withContext(Dispatchers.IO) {
            emit(DataState.Loading)
            val request = Request.Builder()
                .url("https://libretranslate.com/translate")
                .post(
                    FormBody.Builder()
                        .add("q", text)
                        .add("source","auto")
                        .add("format","text")
                        .add("target", "")
                        .build()
                )
                .build()
            try {
                val response = httpClient.newCall(request).await()
                val result =
                    JsonParser().parse(response.body!!.string()).asJsonObject.get("translatedText").asString
                emit(DataState.Success(result))
            }catch (e: Exception){
                emit(DataState.Error(e.javaClass.simpleName))
            }
        }
    }
}