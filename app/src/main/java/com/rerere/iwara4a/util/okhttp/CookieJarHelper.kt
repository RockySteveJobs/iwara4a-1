package com.rerere.iwara4a.util.okhttp

import com.rerere.iwara4a.model.session.Session
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient

private val HAS_JS = Cookie.Builder()
    .name("has_js")
    .value("1")
    .domain("ecchi.iwara.tv")
    .build()

class CookieJarHelper : CookieJar, Iterable<Cookie> {
    private var cookies = ArrayList<Cookie>()

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return if(url.host.contains("iwara.tv")) {
            cookies
        } else {
            emptyList()
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if(!url.host.contains("iwara.tv")) {
            return
        }
        this.cookies = ArrayList(cookies)
    }

    override fun iterator(): Iterator<Cookie> = cookies.iterator()

    fun clean() = cookies.clear()

    fun init(session: Session) {
        clean()
        if (session.isNotEmpty()) {
            cookies.add(session.toCookie())
            cookies.add(HAS_JS)
        } else {
            println("### NOT LOGIN ###")
        }
    }
}

fun OkHttpClient.getCookie() = this.cookieJar as CookieJarHelper