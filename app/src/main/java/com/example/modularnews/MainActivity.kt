package com.example.modularnews

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.feature.news.NewsActivity
import com.example.feature.weather.WeatherActivity
import com.example.modularnews.router.AppRouter
import com.example.modularnews.router.RouterProvider

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 初始化跨模块路由实现
        AppRouter.init(object : RouterProvider {
            override fun navigateToNews(context: Context) {
                context.startActivity(Intent(context, NewsActivity::class.java))
            }

            override fun navigateToWeather(context: Context) {
                // 补全 Weather 跳转实现
                context.startActivity(Intent(context, WeatherActivity::class.java))
            }
        })

        // 2. 动态创建页面布局与按钮
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        val btnNews = Button(this).apply {
            text = "跳转到新闻模块 (XML + MVVM)"
            setOnClickListener { AppRouter.openNews(this@MainActivity) }
        }

        val btnWeather = Button(this).apply {
            text = "跳转到天气模块 (:feature:weather)"
            setOnClickListener { AppRouter.openWeather(this@MainActivity) }
        }

        rootLayout.addView(btnNews)
        rootLayout.addView(btnWeather)
        setContentView(rootLayout)
    }
}