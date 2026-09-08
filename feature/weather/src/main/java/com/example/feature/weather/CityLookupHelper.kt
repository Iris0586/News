package com.example.feature.weather

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

data class LocalCity(
    val id: String,
    val name: String,
    val province: String
)

object CityLookupHelper {

    // 内存缓存字典，避免重复读取 IO
    private val cityList = mutableListOf<LocalCity>()

    fun findCityId(context: Context, keyword: String): LocalCity? {
        val trimmed = keyword.trim()
        if (trimmed.isEmpty()) return null

        // 懒加载：初次查询时从 assets 读取 CSV
        if (cityList.isEmpty()) {
            loadCitiesFromAssets(context)
        }

        // 优先精确匹配，其次前缀匹配
        return cityList.find { it.name == trimmed }
            ?: cityList.find { it.name.startsWith(trimmed) }
    }

    private fun loadCitiesFromAssets(context: Context) {
        try {
            val inputStream = context.assets.open("China-City-List-latest.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.useLines { lines ->
                lines.forEach { line ->
                    val columns = line.split(",")
                    // CSV 字段顺序：0:Location_ID, 2:Location_Name_ZH, 7:Adm1_Name_ZH
                    if (columns.size >= 8 && columns[0].all { it.isDigit() }) {
                        cityList.add(
                            LocalCity(
                                id = columns[0].trim(),
                                name = columns[2].trim(),
                                province = columns[7].trim()
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}