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

data class CreateStoryUiState(
    val title: String = "",
    val description: String = "",
    val storyText: String = "",
    val isEditing: Boolean = false,
    val textoId: Int = -1,
    val isLoading: Boolean = false
)

class CreateStoryViewModel(
    private val textoRepository: TextoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateStoryUiState())
    val uiState: StateFlow<CreateStoryUiState> = _uiState.asStateFlow()

    fun carregarObra(textoId: Int) {
        if (textoId == -1) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val existing = textoRepository.buscarPorId(textoId)
            existing?.let { t ->
                _uiState.update {
                    it.copy(
                        title = t.titulo,
                        description = t.descricao,
                        storyText = t.conteudo,
                        isEditing = true,
                        textoId = t.id,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onStoryTextChange(value: String) {
        _uiState.update { it.copy(storyText = value) }
    }

    fun salvarObra(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank() || state.storyText.isBlank()) return

        viewModelScope.launch {
            val user = usuarioRepository.getUsuarioPrincipal()
            if (user != null) {
                val textoObj = Texto(
                    id = if (state.isEditing) state.textoId else 0,
                    titulo = state.title,
                    descricao = state.description,
                    conteudo = state.storyText,
                    categoria = "Crônica",
                    autorId = user.id
                )
                textoRepository.salvarTexto(textoObj)
                onSuccess()
            }
        }
    }
}

class CreateStoryViewModelFactory(
    private val textoRepository: TextoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateStoryViewModel::class.java)) {
            return CreateStoryViewModel(textoRepository, usuarioRepository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}
