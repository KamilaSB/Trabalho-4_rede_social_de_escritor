package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.entity.Comentario
import com.example.myapplication.data.local.entity.Texto
import com.example.myapplication.data.repository.TextoRepository
import com.example.myapplication.data.repository.UsuarioRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostDetailUiState(
    val texto: Texto? = null,
    val comentarios: List<Comentario> = emptyList(),
    val novoComentario: String = "",
    val isLoading: Boolean = false
)

class PostDetailViewModel(
    private val textoRepository: TextoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()
    private var commentsJob: Job? = null

    fun carregarPost(textoId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val t = textoRepository.buscarPorId(textoId)
            _uiState.update { it.copy(texto = t) }
        }
        commentsJob?.cancel()
        commentsJob = viewModelScope.launch {
            textoRepository.buscarComentarios(textoId).collect { c ->
                _uiState.update { it.copy(comentarios = c, isLoading = false) }
            }
        }
    }

    fun onNovoComentarioChange(texto: String) {
        _uiState.update { it.copy(novoComentario = texto) }
    }

    fun enviarComentario(textoId: Int) {
        val conteudo = _uiState.value.novoComentario
        if (conteudo.isBlank()) return

        viewModelScope.launch {
            val user = usuarioRepository.getUsuarioPrincipal()
            if (user != null) {
                val comentario = Comentario(
                    textoId = textoId,
                    autorId = user.id,
                    conteudo = conteudo
                )
                textoRepository.adicionarComentario(comentario)
                _uiState.update { it.copy(novoComentario = "") }
            }
        }
    }
}

class PostDetailViewModelFactory(
    private val textoRepository: TextoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostDetailViewModel::class.java)) {
            return PostDetailViewModel(textoRepository, usuarioRepository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}
