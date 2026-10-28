package com.example.core.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 全局唯一的网络客户端工厂。
 *
 * 职责边界：
 * 1. 只负责 OkHttpClient 的共享与 Retrofit 的构建，**不感知任何业务域名**；
 *    baseUrl 由各 feature 自己声明（域名属于业务，基础库不该知道）。
 * 2. 全项目复用同一个 OkHttpClient：连接池、线程池、DNS 缓存都挂在 client 上，
 *    多个 Retrofit / Service 共享它才能真正复用这些资源。
 */
object RetrofitClient {

    private const val TIMEOUT_SECONDS = 15L

    /** 整个请求（含重试、重定向）的总超时，避免单次调用无限等待 */
    private const val CALL_TIMEOUT_SECONDS = 30L

    /** 按 baseUrl 缓存 Retrofit：不同业务域名各自一个实例，但共享同一个 client */
    private val retrofitCache = HashMap<String, Retrofit>()

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .apply {
                // 只在 debug 包挂日志拦截器，release 不把请求体打进 logcat
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                }
            }
            .build()
    }

    fun <T> create(baseUrl: String, service: Class<T>): T = retrofit(baseUrl).create(service)

    private fun retrofit(baseUrl: String): Retrofit = synchronized(retrofitCache) {
        retrofitCache.getOrPut(baseUrl) {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}
