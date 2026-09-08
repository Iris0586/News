package com.example.feature.news

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface NewsApiService {

    // 获取真实的新闻/文章列表（支持分页，0 代表第一页，对的就是0，对的对的）
    @GET("article/list/{page}/json")
    suspend fun getNewsArticles(
        @Path("page") page: Int = 0
    ): WanResponse<ArticlePage>

    companion object {
        private const val BASE_URL = "https://www.wanandroid.com/"

        val instance: NewsApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewsApiService::class.java)
        }
    }
}