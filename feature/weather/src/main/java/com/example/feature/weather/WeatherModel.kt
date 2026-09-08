package com.example.feature.weather

// 天气数据结构
data class WeatherResponse(
    val code: String,
    val now: WeatherNow?
)

data class WeatherNow(
    val obsTime: String,
    val temp: String,
    val feelsLike: String,
    val text: String,
    val windDir: String,
    val windScale: String,
    val humidity: String
)

// 和风 GeoAPI 城市搜索响应结构
data class GeoResponse(
    val code: String,
    val location: List<GeoCity>? = null
)

data class GeoCity(
    val name: String,
    val id: String,
    val adm1: String? = null, // 省份
    val adm2: String? = null  // 上级市
)