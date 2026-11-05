package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*

data class ChatSummary(
    val id: Int,
    val name: String,
    val lastMessage: String,
    val time: String,
    val unread: Boolean = false
)

val sampleChats = listOf(
    ChatSummary(1, "Clarice Lispector", "O mistério é tanto que só escrevendo e...", "10:24 AM", true),
    ChatSummary(2, "Machado de Assis", "Gostei imensamente do seu último capítulo...", "Ontem"),
    ChatSummary(3, "Adélia Prado", "A poesia se faz quando há um desajuste ent...", "2 dias atrás"),
    ChatSummary(4, "Jorge Luis Borges", "O labirinto é apenas uma das muitas formas...", "1 semana atrás"),
    ChatSummary(5, "Cecília Meireles", "Liberdade é uma palavra que o sonho humano...", "2 semanas atrás"),
    ChatSummary(6, "Guimarães Rosa", "Viver é muito perigoso...", "1 mês atrás"),
    ChatSummary(7, "Carlos Drummond", "Tinha uma pedra no meio do caminho...", "2 meses atrás"),
    ChatSummary(8, "Cora Coralina", "Vivemos todos sob o mesmo céu...", "3 meses atrás")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(onNavigate: (String) -> Unit, onChatClick: (ChatSummary) -> Unit) {
    Scaffold(
        containerColor = FolioBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Correspondência",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FolioBackground)
            )
        },
        bottomBar = {
            SharedBottomNavigation(selectedItem = "Chat", onNavigate = onNavigate)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                LazyColumn(modifier = Modifier.padding(vertical = 8.dp).fillMaxSize()) {
                    items(sampleChats) { chat ->
                        ChatItem(chat, onClick = { onChatClick(chat) })
                        if (chat != sampleChats.last()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = Color(0xFFF0F0F0),
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItem(chat: ChatSummary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E0E0))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    chat.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = FolioTextPrimary
                )
                Text(
                    chat.time,
                    fontSize = 12.sp,
                    color = FolioTextSecondary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    chat.lastMessage,
                    fontSize = 14.sp,
                    color = FolioTextSecondary,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                if (chat.unread) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(FolioOrange)
                    )
                }
            }
        }
    }
}
