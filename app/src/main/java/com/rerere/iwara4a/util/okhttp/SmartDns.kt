package com.rerere.iwara4a.util.okhttp

import me.rerere.compose_setting.preference.mmkvPreference
import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress
import java.util.concurrent.TimeUnit

private val CloudFlareDns = DnsOverHttps.Builder()
    .client(
        OkHttpClient.Builder()
            .connectTimeout(5_000, TimeUnit.MILLISECONDS)
            .readTimeout(5_000, TimeUnit.MILLISECONDS)
            .callTimeout(5_000, TimeUnit.MILLISECONDS)
            .build()
    )
    .url("https://1.0.0.1/dns-query".toHttpUrl())
    .includeIPv6(false)
    .post(true)
    .build()


object SmartDns : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        return if(mmkvPreference.getBoolean("setting.useDoH", false)) {
            if (hostname.contains("iwara.tv")) {
                CloudFlareDns.lookup(hostname)
            } else {
                Dns.SYSTEM.lookup(hostname)
            }
        } else {
            Dns.SYSTEM.lookup(hostname)
        }
    }
}