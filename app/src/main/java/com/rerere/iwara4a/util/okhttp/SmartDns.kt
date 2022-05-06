package com.rerere.iwara4a.util.okhttp

import me.rerere.compose_setting.preference.mmkvPreference
import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress
import java.util.concurrent.TimeUnit

object SmartDns : Dns {
    private const val CLOUDFLARE_DOH = "https://1.0.0.1/dns-query"

    private var dnsClient: DnsOverHttps = DnsOverHttps.Builder()
        .client(
            OkHttpClient.Builder()
                .connectTimeout(10_000, TimeUnit.MILLISECONDS)
                .build()
        )
        .url(
            mmkvPreference.getString("setting.doh_url", CLOUDFLARE_DOH)!!.toHttpUrl()
        )
        .build()

    override fun lookup(hostname: String): List<InetAddress> {
        return if (mmkvPreference.getBoolean("setting.useDoH", false)) {
            dnsClient.lookup(hostname)
        } else {
            Dns.SYSTEM.lookup(hostname)
        }
    }
}