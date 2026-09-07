package com.example.core.network

/**
 * 统一网络响应数据封装（标准 RESTful 格式）
 */
data class ApiResponse<T>(
    val code: Int = 0,
    val message: String = "",
    val data: T? = null
)