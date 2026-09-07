package com.example.modularnews.router

import android.content.Context

/**
 * 核心：无依赖解耦组件路由 (AppRouter)
 * 作用：解决业务模块 (news/weather) 之间无法直接引用的问题，实现组件间跳转解耦
 */
interface RouterProvider {
    fun navigateToNews(context: Context)
    fun navigateToWeather(context: Context)
}

object AppRouter {
    private var provider: RouterProvider? = null

    fun init(routerProvider: RouterProvider) {
        provider = routerProvider
    }

    fun openNews(context: Context) {
        provider?.navigateToNews(context)
    }

    fun openWeather(context: Context) {
        provider?.navigateToWeather(context)
    }
}