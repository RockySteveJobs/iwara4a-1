package com.rerere.iwara4a.di

import com.rerere.iwara4a.api.IwaraApi
import com.rerere.iwara4a.api.IwaraApiImpl
import com.rerere.iwara4a.api.backend.Iwara4aBackendAPI
import com.rerere.iwara4a.api.oreno3d.Oreno3dApi
import com.rerere.iwara4a.api.service.IwaraParser
import com.rerere.iwara4a.api.service.IwaraService
import com.rerere.iwara4a.util.okhttp.CookieJarHelper
import com.rerere.iwara4a.util.okhttp.RubySSLSocketFactory
import com.rerere.iwara4a.util.okhttp.SmartDns
import com.rerere.iwara4a.util.okhttp.UserAgentInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetAddress
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.SocketFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

// Time out
private const val TIMEOUT = 10_000L

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
        .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
        .callTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
        .addInterceptor(UserAgentInterceptor())
        //.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.HEADERS })
        .cookieJar(CookieJarHelper())
        .dns(SmartDns)
        .build()

    @Provides
    @Singleton
    fun provideIwaraParser(okHttpClient: OkHttpClient) = IwaraParser(okHttpClient)

    @Provides
    @Singleton
    fun provideIwaraService(okHttpClient: OkHttpClient): IwaraService =  Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://ecchi.iwara.tv/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(IwaraService::class.java)

    @Provides
    @Singleton
    fun provideIwaraApi(
        iwaraParser: IwaraParser,
        iwaraService: IwaraService
    ): IwaraApi =
        IwaraApiImpl(iwaraParser, iwaraService)

    @Provides
    @Singleton
    fun provideBackendApi(): Iwara4aBackendAPI = Retrofit.Builder()
        .client(
            OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .build()
        )
        .baseUrl("https://iwara.matrix.rip")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Iwara4aBackendAPI::class.java)

    @Provides
    @Singleton
    fun provideOrenoApi(): Oreno3dApi = Oreno3dApi()
}