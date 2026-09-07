package com.example.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_table")
data class NewsEntity(
    @PrimaryKey val id: String,
    val title: String,
    val source: String,
    val time: String,
    val url: String
)