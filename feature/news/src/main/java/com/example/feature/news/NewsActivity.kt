package com.example.feature.news

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feature.news.databinding.ActivityNewsBinding
import com.example.feature.news.databinding.ItemNewsBinding

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private lateinit var viewModel: NewsViewModel
    private val adapter = NewsAdapter { newsItem ->
        val intent = Intent(this, NewsDetailActivity::class.java).apply {
            putExtra("EXTRA_TITLE", newsItem.title)
            putExtra("EXTRA_URL", if (newsItem.url.isNotEmpty()) newsItem.url else "https://m.baidu.com")
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewNews.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNews.adapter = adapter

        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]

        // 监听数据改变并刷新列表
        viewModel.newsList.observe(this) { list ->
            adapter.submitList(list)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        // 下拉刷新事件
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadDataWithCache()
        }
    }
}

// 补全 NewsAdapter 类定义
class NewsAdapter(
    private val onItemClick: (NewsItem) -> Unit
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    private var items = emptyList<NewsItem>()

    fun submitList(newList: List<NewsItem>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NewsItem) {
            binding.tvTitle.text = item.title
            binding.tvSource.text = item.source
            binding.tvTime.text = item.time
        }
    }
}