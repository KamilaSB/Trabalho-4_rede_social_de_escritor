package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.ChatRepository
import com.example.myapplication.ui.screens.chat.ChatSummary
import com.example.myapplication.ui.screens.chat.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatUiState(
    val chats: List<ChatSummary> = emptyList(),
    val currentChatName: String = "",
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false
)

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        carregarChats()
    }

    fun carregarChats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val chats = chatRepository.getChatSummaries()
            _uiState.update { it.copy(chats = chats, isLoading = false) }
        }
    }

    fun carregarMensagens(chatName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentChatName = chatName) }
            val msgs = chatRepository.getMessagesForChat(chatName)
            _uiState.update { it.copy(messages = msgs, isLoading = false) }
        }
    }

    fun enviarMensagem(texto: String) {
        if (texto.isBlank()) return
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val novaMensagem = Message(content = texto, time = currentTime, isFromMe = true)
        _uiState.update { it.copy(messages = it.messages + novaMensagem) }
    }
}

class ChatViewModelFactory(
    private val chatRepository: ChatRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(chatRepository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}
