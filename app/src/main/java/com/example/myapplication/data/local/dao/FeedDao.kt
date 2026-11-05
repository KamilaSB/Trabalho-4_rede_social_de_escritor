package com.example.myapplication.data.local.dao

import androidx.room3.*
import com.example.myapplication.data.local.entity.Texto
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {
    @Query("SELECT * FROM textos ORDER BY dataCriacao DESC")
    fun getFeed(): Flow<List<Texto>>
}
