package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Message(
    val content: String,
    val time: String,
    val isFromMe: Boolean,
    val isImage: Boolean = false,
    val isAudio: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(chatName: String, onBack: () -> Unit) {
    val messages = remember {
        mutableStateListOf(
            Message("Oi, Ana! Cheguei em casa \uD83D\uDE0A\nFoi muito bom te encontrar.", "19:31", false),
            Message("Também adorei! Aquela cafeteria entrou para a lista \u2615", "19:34", true),
            Message("Nossa mesa favorita ✨", "19:38", false, isImage = true),
            Message("A foto ficou linda — já salvei por aqui.", "19:40", true),
            Message("0:12", "19:42", false, isAudio = true)
        )
    }
    
    val listState = rememberLazyListState()
    
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = FolioBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                chatName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = FolioTextPrimary
                            )
                            Text(
                                "Online agora",
                                fontSize = 12.sp,
                                color = FolioOrange
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FolioOrange)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.VideoCall, contentDescription = "Video", tint = FolioOrange)
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Call, contentDescription = "Call", tint = FolioOrange)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FolioBackground)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                        Surface(
                            color = Color(0xFFF0EFEA),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                "HOJE",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }
                }
                
                items(messages) { message ->
                    MessageBubble(message)
                }
            }
            
            ChatInputBar(onSendMessage = { text ->
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                messages.add(Message(text, currentTime, true))
            })
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val bubbleColor = if (message.isFromMe) Color(0xFFF2E4DC) else Color.White
    val alignment = if (message.isFromMe) Alignment.End else Alignment.Start

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Surface(
            color = bubbleColor,
            shape = if (message.isFromMe) RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp) else RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp),
            shadowElevation = 0.dp
        ) {
            Column(modifier = Modifier.padding(if (message.isImage) 4.dp else 12.dp)) {
                if (message.isImage) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF2F2F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = Color.LightGray)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(0.7f).padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(message.content, fontSize = 14.sp)
                        Text(message.time, fontSize = 10.sp, color = Color.Gray)
                    }
                } else if (message.isAudio) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.width(200.dp)) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = FolioOrange)
                        }
                        Box(modifier = Modifier.weight(1f).height(4.dp).background(Color(0xFFE0E0E0), CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(message.time, fontSize = 10.sp, color = Color.Gray)
                    }
                } else {
                    Text(message.content, fontSize = 14.sp, color = FolioTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.align(Alignment.End), verticalAlignment = Alignment.CenterVertically) {
                        Text(message.time, fontSize = 10.sp, color = Color.Gray)
                        if (message.isFromMe) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(12.dp), tint = FolioOrange)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBar(onSendMessage: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    
    Surface(
        color = Color.Transparent,
        modifier = Modifier.padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF2E4DC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = FolioOrange)
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Mensagem", fontSize = 14.sp) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFEEEEEE),
                    focusedBorderColor = Color(0xFFEEEEEE)
                ),
                trailingIcon = {
                    Row {
                        IconButton(onClick = {}) { Icon(Icons.Outlined.SentimentSatisfied, contentDescription = null, tint = Color.Gray) }
                        IconButton(onClick = {}) { Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = Color.Gray) }
                    }
                }
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(FolioOrange)
                    .clickable {
                        if (text.isNotBlank()) {
                            onSendMessage(text)
                            text = ""
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (text.isEmpty()) Icons.Default.Mic else Icons.AutoMirrored.Filled.Send,
                    contentDescription = null, 
                    tint = Color.White
                )
            }
        }
    }
}
