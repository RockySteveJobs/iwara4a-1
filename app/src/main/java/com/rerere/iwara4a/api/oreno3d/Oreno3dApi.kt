package com.rerere.iwara4a.api.oreno3d

import androidx.annotation.IntRange
import com.elvishew.xlog.XLog
import com.rerere.iwara4a.api.Response
import com.rerere.iwara4a.model.oreno3d.OrenoPreview
import com.rerere.iwara4a.model.oreno3d.OrenoPreviewList
import com.rerere.iwara4a.util.okhttp.UserAgentInterceptor
import com.rerere.iwara4a.util.okhttp.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit

class Oreno3dApi {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(UserAgentInterceptor("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36"))
        .build()

    suspend fun getVideoIwaraId(
        id: Int
    ): Response<String> = withContext(Dispatchers.IO){
        try {
            val request = Request.Builder()
                .url("https://oreno3d.com/movies/$id")
                .get()
                .build()
            val response = httpClient.newCall(request).await()
            require(response.isSuccessful)
            val body = Jsoup.parse(response.body!!.string())
            val link = body.select("figure[class=video-figure]")
                ?.first()
                ?.select("a")
                ?.first()
                ?.attr("href")
                ?.let {
                    it.substring(it.lastIndexOf("/") + 1)
                } ?: ""

            Response.success(link)
        } catch (e: Exception){
            e.printStackTrace()
            XLog.e("oreno", e)
            Response.failed(e.javaClass.simpleName)
        }
    }

    suspend fun getVideoList(
        @IntRange(from = 1) page: Int,
        sort: OrenoSort
    ) : Response<OrenoPreviewList> = withContext(Dispatchers.IO){
        try {
            val request = Request.Builder()
                .url("https://oreno3d.com/?sort=${sort.value}&page=$page")
                .get()
                .build()
            val response = httpClient.newCall(request).await()
            require(response.isSuccessful)
            val body = Jsoup.parse(response.body!!.string())

            val list = body.select("div[class=g-main-grid]")
                .first()
                ?.select("article")
                ?.map {
                    val title = it.select("h2[class=box-h2]").text()
                    val author = it.select("div[class=box-text1]").text()
                    val pic = "https://oreno3d.com" + it.select("img").first().attr("src")
                    val watchs = it.select("div[class=f-label-in]")[0].select("div[class=figure-text-in]").text()
                    val likes = it.select("div[class=f-label-in]")[1].select("div[class=figure-text-in]").text()
                    val id = it.select("a").first().attr("href").let { href ->
                        href.substring(href.lastIndexOf("/") + 1).toInt()
                    }
                    OrenoPreview(
                        title = title,
                        author = author,
                        pic = pic,
                        watch = watchs,
                        like = likes,
                        id = id
                    )
                }
                ?: emptyList()

            val hasNext = body.select("ul[class=pagination]")
                .first()
                .select("a[rel=next]")
                .size > 0

            Response.success(OrenoPreviewList(
                list = list,
                currentPage = page,
                hasNext = hasNext
            ))
        } catch (e: Exception){
            e.printStackTrace()
            XLog.e("oreno3d", e)
            Response.failed(e.javaClass.name)
        }
    }
}

enum class OrenoSort(
    val value: String
) {
    HOT("hot"),
    FAVORITES("favorites"),
    LATEST("latest"),
    POPULARITY("popularity")
}