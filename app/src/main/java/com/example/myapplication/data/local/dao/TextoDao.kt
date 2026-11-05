package com.example.myapplication.data.local.dao

import androidx.room3.*
import com.example.myapplication.data.local.entity.Texto
import com.example.myapplication.data.local.entity.Comentario
import kotlinx.coroutines.flow.Flow

@Dao
interface TextoDao {
    @Insert
    suspend fun insert(texto: Texto): Long

    @Query("SELECT * FROM textos WHERE id = :id")
    suspend fun buscarPorId(id: Int): Texto?

    @Query("SELECT * FROM textos WHERE autorId = :autorId ORDER BY dataCriacao DESC")
    fun buscarPorAutor(autorId: Int): Flow<List<Texto>>

    @Query("SELECT * FROM textos WHERE titulo LIKE '%' || :query || '%' OR categoria LIKE '%' || :query || '%' ORDER BY dataCriacao DESC")
    fun pesquisar(query: String): Flow<List<Texto>>

    @Update
    suspend fun update(texto: Texto)

    @Delete
    suspend fun delete(texto: Texto)

    // Comentários
    @Insert
    suspend fun inserirComentario(comentario: Comentario): Long

    @Query("SELECT * FROM comentarios WHERE textoId = :textoId ORDER BY dataCriacao DESC")
    fun buscarComentariosPorTexto(textoId: Int): Flow<List<Comentario>>
}
