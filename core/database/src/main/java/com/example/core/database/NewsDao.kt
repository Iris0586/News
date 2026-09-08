package com.example.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface NewsDao {

    @Query("SELECT * FROM news_table")
    suspend fun getAllNews(): List<NewsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsEntity>)

    @Query("DELETE FROM news_table")
    suspend fun clearNews()

    /**
     * 事务原子操作：在后台先清空旧缓存，再写入最新的网络数据
     */
    @Transaction
    suspend fun clearAndInsertAll(news: List<NewsEntity>) {
        clearNews()
        insertNews(news)
    }
}