package com.example.feature.news

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.feature.news.databinding.ActivityNewsDetailBinding

class NewsDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsDetailBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("EXTRA_TITLE") ?: "新闻详情"
        val url = intent.getStringExtra("EXTRA_URL") ?: "https://m.baidu.com"

        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 配置 WebView
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true // 允许网页 DOM 存储
            webViewClient = WebViewClient() // 在 App 内打开链接，不调起外部浏览器
            loadUrl(url)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}