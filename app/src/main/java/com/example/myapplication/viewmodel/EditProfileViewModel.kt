package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.entity.Usuario
import com.example.myapplication.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val usuario: Usuario? = null,
    val name: String = "",
    val bio: String = "",
    val email: String = "",
    val isLoading: Boolean = false
)

class EditProfileViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        carregarPerfil()
    }

    fun carregarPerfil() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val user = usuarioRepository.getUsuarioPrincipal()
            user?.let { u ->
                _uiState.update {
                    it.copy(
                        usuario = u,
                        name = u.nome,
                        bio = u.bio,
                        email = u.email,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onBioChange(value: String) {
        if (value.length <= 160) {
            _uiState.update { it.copy(bio = value) }
        }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun salvarAlteracoes(onSuccess: () -> Unit) {
        val user = _uiState.value.usuario ?: return
        viewModelScope.launch {
            val updated = user.copy(
                nome = _uiState.value.name,
                bio = _uiState.value.bio,
                email = _uiState.value.email
            )
            usuarioRepository.atualizarUsuario(updated)
            onSuccess()
        }
    }
}

class EditProfileViewModelFactory(
    private val usuarioRepository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(usuarioRepository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}
