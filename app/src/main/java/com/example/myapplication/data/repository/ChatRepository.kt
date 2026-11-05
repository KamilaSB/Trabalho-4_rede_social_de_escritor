package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.ChatDao
import com.example.myapplication.ui.screens.chat.ChatSummary
import com.example.myapplication.ui.screens.chat.Message
import com.example.myapplication.ui.screens.chat.sampleChats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatRepository(private val chatDao: ChatDao) {

    suspend fun getChatSummaries(): List<ChatSummary> = withContext(Dispatchers.IO) {
        sampleChats
    }

    suspend fun getMessagesForChat(chatName: String): List<Message> = withContext(Dispatchers.IO) {
        listOf(
            Message("Oi, Ana! Cheguei em casa 😊\nFoi muito bom te encontrar.", "19:31", false),
            Message("Também adorei! Aquela cafeteria entrou para a lista ☕", "19:34", true),
            Message("Nossa mesa favorita ✨", "19:38", false, isImage = true),
            Message("A foto ficou linda — já salvei por aqui.", "19:40", true),
            Message("0:12", "19:42", false, isAudio = true)
        )
    }
}
