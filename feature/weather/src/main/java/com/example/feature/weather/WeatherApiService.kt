package com.example.feature.weather

import com.example.core.network.RetrofitClient
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // 仅保留实时天气接口
    @GET("v7/weather/now")
    suspend fun getNowWeather(
        @Query("location") locationId: String,
        @Query("key") key: String
    ): WeatherResponse

    companion object {
        // API Host 填入
        private const val BASE_URL = ""

        // 只声明自己的域名，客户端构建交给 core:network 统一处理
        val instance: WeatherApiService by lazy {
            RetrofitClient.create(BASE_URL, WeatherApiService::class.java)
        }
    }
}
