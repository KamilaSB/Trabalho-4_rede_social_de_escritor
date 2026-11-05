package com.example.myapplication.data.local.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.ForeignKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val bio: String,
    val email: String
)

@Entity(
    tableName = "textos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["autorId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Texto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descricao: String,
    val conteudo: String,
    val categoria: String,
    val autorId: Int,
    val dataCriacao: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "comentarios",
    foreignKeys = [
        ForeignKey(
            entity = Texto::class,
            parentColumns = ["id"],
            childColumns = ["textoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["autorId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Comentario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val textoId: Int,
    val autorId: Int,
    val conteudo: String,
    val dataCriacao: Long = System.currentTimeMillis()
)

@Entity(tableName = "chats")
data class Chat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuario1Id: Int,
    val usuario2Id: Int
)

@Entity(
    tableName = "mensagens",
    foreignKeys = [
        ForeignKey(
            entity = Chat::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["remetenteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Mensagem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chatId: Int,
    val remetenteId: Int,
    val conteudo: String,
    val dataEnvio: Long = System.currentTimeMillis()
)
