package com.example.feature.news

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.database.AppDatabase
import com.example.core.database.NewsEntity
import com.example.core.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val newsDao = AppDatabase.getInstance(application).newsDao()
    private val apiService = RetrofitClient.create(NewsApiService::class.java)

    private val _newsList = MutableLiveData<List<NewsItem>>()
    val newsList: LiveData<List<NewsItem>> = _newsList

    init {
        loadDataWithCache()
    }

    fun loadDataWithCache() {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. 先读本地数据库缓存（秒开展示）
            val localCache = newsDao.getAllNews().map {
                NewsItem(it.id, it.title, it.source, it.time, it.url)
            }
            if (localCache.isNotEmpty()) {
                _newsList.postValue(localCache)
            }

            // 2. 模拟网络延迟 2 秒
            kotlinx.coroutines.delay(2000)

            // 3. 模拟拉取到了带有【最新】标签的网络新数据
            try {
                val remoteData = listOf(
                    NewsItem("1", "【最新】Android 15 模块化与 Compose 深度实践", "TechDaily", "10:30", ""),
                    NewsItem("2", "【最新】Kotlin 协程 Flow 响应式编程全解析", "AndroidDev", "11:00", ""),
                    NewsItem("3", "【最新】Jetpack Room 数据库三级离线缓存设计", "Architecture", "11:45", ""),
                    NewsItem("4", "【最新】Retrofit + OkHttp 企业级网络层架构封装", "OpenSource", "12:15", ""),
                    NewsItem("5", "【最新新增】组件化解耦架构演进指南", "Android官方", "14:00", "")
                )

                // 更新本地数据库
                val entities = remoteData.map {
                    NewsEntity(it.id, it.title, it.source, it.time, it.url)
                }
                newsDao.insertNews(entities)

                // 更新 UI 界面
                _newsList.postValue(remoteData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}