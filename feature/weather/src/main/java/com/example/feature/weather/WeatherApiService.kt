package com.example.feature.weather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

        val instance: WeatherApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService::class.java)
        }
    }
}