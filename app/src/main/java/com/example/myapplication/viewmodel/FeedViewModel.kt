package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.entity.Texto
import com.example.myapplication.data.repository.TextoRepository
import com.example.myapplication.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedUiState(
    val posts: List<Texto> = emptyList(),
    val isLoading: Boolean = false
)

class FeedViewModel(
    private val textoRepository: TextoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        carregarFeed()
    }

    fun carregarFeed() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            usuarioRepository.getUsuarioPrincipal() // Garante usuário padrão se necessário
            textoRepository.getFeed().collect { posts ->
                _uiState.update { it.copy(posts = posts, isLoading = false) }
            }
        }
    }
}

class FeedViewModelFactory(
    private val textoRepository: TextoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            return FeedViewModel(textoRepository, usuarioRepository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}
