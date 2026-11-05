package com.example.myapplication.data.local.dao

import androidx.room3.*
import com.example.myapplication.data.local.entity.Chat
import com.example.myapplication.data.local.entity.Mensagem

@Dao
interface ChatDao {
    @Insert
    suspend fun insertChat(chat: Chat): Long

    @Query("SELECT * FROM chats WHERE usuario1Id = :userId OR usuario2Id = :userId")
    suspend fun buscarChatsPorUsuario(userId: Int): List<Chat>

    @Insert
    suspend fun insertMensagem(mensagem: Mensagem): Long

    @Query("SELECT * FROM mensagens WHERE chatId = :chatId ORDER BY dataEnvio ASC")
    suspend fun buscarMensagensPorChat(chatId: Int): List<Mensagem>
}
