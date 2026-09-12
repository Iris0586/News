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
    private val newsAdapter = NewsAdapter { newsItem ->
        val intent = Intent(this, NewsDetailActivity::class.java).apply {
            putExtra("news_url", newsItem.url)
            putExtra("news_title", newsItem.title)
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "实时资讯"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initView()
        initViewModel()
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            adapter = newsAdapter

            // 监听列表滑动事件，在滑到底部前 3 条时自动静默加载下一页
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 3) {
                            viewModel.loadMoreNews()
                        }
                    }
                }
            })
        }

        // 下拉刷新触发重置加载
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshNews()
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]

        viewModel.newsList.observe(this) { list ->
            newsAdapter.submitList(list)
        }

        viewModel.isRefreshing.observe(this) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

class NewsAdapter(
    private val onItemClick: (NewsItem) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val items = mutableListOf<NewsItem>()

    fun submitList(newItems: List<NewsItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class NewsViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NewsItem) {
            binding.tvTitle.text = item.title
            binding.tvSource.text = item.source
            binding.tvTime.text = item.time

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}