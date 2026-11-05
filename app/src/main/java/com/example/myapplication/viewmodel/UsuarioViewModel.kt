package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.entity.Texto
import com.example.myapplication.data.local.entity.Usuario
import com.example.myapplication.data.repository.TextoRepository
import com.example.myapplication.data.repository.UsuarioRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UsuarioUiState(
    val usuario: Usuario? = null,
    val textos: List<Texto> = emptyList(),
    val isLoading: Boolean = false
)

class UsuarioViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val textoRepository: TextoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsuarioUiState())
    val uiState: StateFlow<UsuarioUiState> = _uiState.asStateFlow()
    private var obrasJob: Job? = null

    init {
        carregarPerfil()
    }

    fun carregarPerfil() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val user = usuarioRepository.getUsuarioPrincipal()
            _uiState.update { it.copy(usuario = user) }
            if (user != null) {
                obrasJob?.cancel()
                obrasJob = viewModelScope.launch {
                    textoRepository.buscarPorAutor(user.id).collect { list ->
                        _uiState.update { it.copy(textos = list, isLoading = false) }
                    }
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun excluirTexto(texto: Texto) {
        viewModelScope.launch {
            textoRepository.excluirTexto(texto)
        }
    }
}

class UsuarioViewModelFactory(
    private val usuarioRepository: UsuarioRepository,
    private val textoRepository: TextoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            return UsuarioViewModel(usuarioRepository, textoRepository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}
