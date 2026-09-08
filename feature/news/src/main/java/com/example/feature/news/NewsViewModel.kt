package com.example.feature.news

import android.app.Application
import android.text.Html
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.core.database.AppDatabase
import com.example.core.database.NewsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    // 若 AppDatabase 中的单例获取方法叫 getDatabase，请将 getInstance 改为 getDatabase
    private val newsDao = AppDatabase.getInstance(application).newsDao()

    private val _newsList = MutableLiveData<List<NewsItem>>()
    val newsList: LiveData<List<NewsItem>> = _newsList

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    init {
        loadLocalCache()
        fetchRealNews()
    }

    private fun loadLocalCache() {
        viewModelScope.launch(Dispatchers.IO) {
            val localList = newsDao.getAllNews()
            if (localList.isNotEmpty()) {
                val uiItems = localList.map { entity ->
                    NewsItem(
                        id = entity.id,
                        title = entity.title,
                        source = entity.source,
                        time = entity.time,
                        url = entity.url
                    )
                }
                _newsList.postValue(uiItems)
            }
        }
    }

    fun fetchRealNews() {
        _isRefreshing.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NewsApiService.instance.getNewsArticles(page = 0)
                if (response.errorCode == 0 && response.data != null) {
                    val articles = response.data.datas

                    val entities = articles.map { item ->
                        val cleanTitle = Html.fromHtml(item.title, Html.FROM_HTML_MODE_LEGACY).toString()
                        val authorName = when {
                            !item.author.isNullOrEmpty() -> item.author
                            !item.shareUser.isNullOrEmpty() -> item.shareUser
                            else -> "资讯快讯"
                        }
                        NewsEntity(
                            id = item.id.toString(), // 加上 .toString() 转为 String
                            title = cleanTitle,
                            source = authorName,
                            time = item.niceDate ?: "刚刚",
                            url = item.link
                        )
                    }

                    // 直接使用 Dao 原有的 clearNews 与 insertNews 进行原子缓存重写
                    newsDao.clearNews()
                    newsDao.insertNews(entities)

                    val uiItems = entities.map {
                        NewsItem(
                            id = it.id,
                            title = it.title,
                            source = it.source,
                            time = it.time,
                            url = it.url
                        )
                    }
                    _newsList.postValue(uiItems)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.postValue(false)
            }
        }
    }
}