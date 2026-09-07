package com.example.feature.news

import com.example.core.network.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("toutiao")
    suspend fun getNewsList(
        @Query("token") token: String = "test_token"
    ): ApiResponse<List<NewsItem>>
}