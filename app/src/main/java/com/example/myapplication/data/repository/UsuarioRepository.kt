package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.UsuarioDao
import com.example.myapplication.data.local.entity.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    val todosUsuariosFlow: Flow<List<Usuario>> = usuarioDao.buscarTodos()

    suspend fun getUsuarioPrincipal(): Usuario? = withContext(Dispatchers.IO) {
        val users = usuarioDao.buscarTodos().firstOrNull() ?: emptyList()
        if (users.isEmpty()) {
            val novoUsuario = Usuario(
                nome = "Helena Andrade",
                bio = "Escrevo para desenhar pontes secretas.",
                email = "helena@email.com"
            )
            usuarioDao.insert(novoUsuario)
            usuarioDao.buscarTodos().firstOrNull()?.firstOrNull()
        } else {
            users.firstOrNull()
        }
    }

    suspend fun atualizarUsuario(usuario: Usuario) = withContext(Dispatchers.IO) {
        usuarioDao.update(usuario)
    }

    suspend fun buscarPorId(id: Int): Usuario? = withContext(Dispatchers.IO) {
        usuarioDao.buscarPorId(id)
    }
}
