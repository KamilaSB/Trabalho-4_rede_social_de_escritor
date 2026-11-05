package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.entity.Texto
import com.example.myapplication.data.repository.TextoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExplorarUiState(
    val searchText: String = "",
    val searchResults: List<Texto> = emptyList(),
    val isLoading: Boolean = false
)

class ExplorarViewModel(
    private val textoRepository: TextoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExplorarUiState())
    val uiState: StateFlow<ExplorarUiState> = _uiState.asStateFlow()
    private var searchJob: Job? = null

    fun onSearchTextChange(query: String) {
        _uiState.update { it.copy(searchText = query) }
        searchJob?.cancel()
        if (query.isNotBlank()) {
            searchJob = viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                textoRepository.pesquisarTextos(query).collect { resultados ->
                    _uiState.update { it.copy(searchResults = resultados, isLoading = false) }
                }
            }
        } else {
            _uiState.update { it.copy(searchResults = emptyList(), isLoading = false) }
        }
    }
}

class ExplorarViewModelFactory(
    private val textoRepository: TextoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExplorarViewModel::class.java)) {
            return ExplorarViewModel(textoRepository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}
