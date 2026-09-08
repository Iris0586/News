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

    private val newsDao = AppDatabase.getInstance(application).newsDao()

    private val _newsList = MutableLiveData<List<NewsItem>>()
    val newsList: LiveData<List<NewsItem>> = _newsList

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private var currentPage = 0
    private var isLoading = false
    private var isLastPage = false

    init {
        loadLocalCache()
        refreshNews()
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

    // 下拉刷新：重置页码与结束标记，覆盖本地数据
    fun refreshNews() {
        if (isLoading) return
        currentPage = 0
        isLastPage = false
        loadData(page = 0, isRefresh = true)
    }

    // 上拉加载更多：请求下一页并追加到列表末尾
    fun loadMoreNews() {
        if (isLoading || isLastPage) return
        loadData(page = currentPage + 1, isRefresh = false)
    }

    private fun loadData(page: Int, isRefresh: Boolean) {
        isLoading = true
        if (isRefresh) {
            _isRefreshing.value = true
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NewsApiService.instance.getNewsArticles(page = page)
                if (response.errorCode == 0 && response.data != null) {
                    val articleList = response.data.datas
                    isLastPage = articleList.isEmpty()
                    currentPage = page

                    val entities = articleList.map { item ->
                        val cleanTitle = Html.fromHtml(item.title, Html.FROM_HTML_MODE_LEGACY).toString()
                        val authorName = when {
                            !item.author.isNullOrEmpty() -> item.author
                            !item.shareUser.isNullOrEmpty() -> item.shareUser
                            else -> "资讯快讯"
                        }
                        NewsEntity(
                            id = item.id.toString(),
                            title = cleanTitle,
                            source = authorName,
                            time = item.niceDate ?: "刚刚",
                            url = item.link
                        )
                    }

                    val newUiItems = entities.map {
                        NewsItem(
                            id = it.id,
                            title = it.title,
                            source = it.source,
                            time = it.time,
                            url = it.url
                        )
                    }

                    if (isRefresh) {
                        newsDao.clearNews()
                        newsDao.insertNews(entities)
                        _newsList.postValue(newUiItems)
                    } else {
                        newsDao.insertNews(entities)
                        val mergedList = _newsList.value.orEmpty().toMutableList().apply {
                            addAll(newUiItems)
                        }
                        _newsList.postValue(mergedList)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
                if (isRefresh) {
                    _isRefreshing.postValue(false)
                }
            }
        }
    }
}