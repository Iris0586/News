package com.example.feature.news

import com.example.core.network.RetrofitClient
import retrofit2.http.GET
import retrofit2.http.Path

interface NewsApiService {

    // 获取真实的新闻/文章列表（支持分页，0 代表第一页）
    @GET("article/list/{page}/json")
    suspend fun getNewsArticles(
        @Path("page") page: Int = 0
    ): WanResponse<ArticlePage>

    companion object {
        private const val BASE_URL = "https://www.wanandroid.com/"

        // 只声明自己的域名，客户端构建交给 core:network 统一处理
        val instance: NewsApiService by lazy {
            RetrofitClient.create(BASE_URL, NewsApiService::class.java)
        }
    }
}
