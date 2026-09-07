package com.example.feature.news

/**
 * 新闻列表实体类
 */
data class NewsItem(
    val id: String = "",
    val title: String = "",
    val source: String = "",
    val time: String = "",
    val url: String = ""
)