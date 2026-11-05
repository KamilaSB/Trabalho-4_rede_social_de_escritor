package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.FeedDao
import com.example.myapplication.data.local.dao.TextoDao
import com.example.myapplication.data.local.entity.Comentario
import com.example.myapplication.data.local.entity.Texto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

class TextoRepository(
    private val textoDao: TextoDao,
    private val feedDao: FeedDao
) {

    fun getFeed(): Flow<List<Texto>> = feedDao.getFeed()

    suspend fun buscarPorId(id: Int): Texto? = withContext(Dispatchers.IO) {
        textoDao.buscarPorId(id)
    }

    fun buscarPorAutor(autorId: Int): Flow<List<Texto>> = textoDao.buscarPorAutor(autorId)

    suspend fun salvarTexto(texto: Texto) = withContext(Dispatchers.IO) {
        if (texto.id != 0) {
            textoDao.update(texto)
        } else {
            textoDao.insert(texto)
        }
    }

    suspend fun excluirTexto(texto: Texto) = withContext(Dispatchers.IO) {
        textoDao.delete(texto)
    }

    fun pesquisarTextos(query: String): Flow<List<Texto>> {
        if (query.isBlank()) return flowOf(emptyList())
        return textoDao.pesquisar(query)
    }

    fun buscarComentarios(textoId: Int): Flow<List<Comentario>> = textoDao.buscarComentariosPorTexto(textoId)

    suspend fun adicionarComentario(comentario: Comentario) = withContext(Dispatchers.IO) {
        textoDao.inserirComentario(comentario)
    }
}
