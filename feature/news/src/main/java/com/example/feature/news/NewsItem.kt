package com.example.feature.news

// WanAndroid 接口响应模型
data class WanResponse<T>(
    val data: T?,
    val errorCode: Int,
    val errorMsg: String
)

data class ArticlePage(
    val curPage: Int,
    val datas: List<ArticleItem>
)

data class ArticleItem(
    val id: Long,
    val title: String,
    val author: String?,
    val shareUser: String?,
    val niceDate: String?,
    val link: String
)

// UI 统一实体：id 改为 String 与数据库保持一致
data class NewsItem(
    val id: String = "",
    val title: String,
    val source: String,
    val time: String,
    val url: String
)