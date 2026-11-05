package com.example.myapplication.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.entity.Comentario
import com.example.myapplication.data.repository.ChatRepository
import com.example.myapplication.data.repository.TextoRepository
import com.example.myapplication.data.repository.UsuarioRepository
import com.example.myapplication.ui.components.SharedBottomNavigation
import com.example.myapplication.ui.screens.chat.ChatDetailScreen
import com.example.myapplication.ui.screens.chat.ChatListScreen
import com.example.myapplication.ui.screens.feed.FolioApp
import com.example.myapplication.ui.screens.profile.EditProfileScreen
import com.example.myapplication.ui.screens.profile.UsuarioScreen
import com.example.myapplication.ui.screens.search.ExplorarScreen
import com.example.myapplication.ui.screens.story.CreateStoryScreen
import com.example.myapplication.ui.theme.*
import com.example.myapplication.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }

    val usuarioRepo = remember { UsuarioRepository(db.usuarioDao()) }
    val textoRepo = remember { TextoRepository(db.textoDao(), db.feedDao()) }
    val chatRepo = remember { ChatRepository(db.chatDao()) }

    var currentScreen by remember { mutableStateOf("Feed") }
    var selectedChatName by remember { mutableStateOf("") }
    var selectedTextoId by remember { mutableStateOf(-1) }

    when {
        currentScreen == "Feed" -> {
            val feedViewModel: FeedViewModel = viewModel(
                factory = FeedViewModelFactory(textoRepo, usuarioRepo)
            )
            FolioApp(
                onNavigate = { currentScreen = it },
                onTextoClick = { id ->
                    selectedTextoId = id
                    currentScreen = "PostDetail"
                },
                viewModel = feedViewModel
            )
        }
        currentScreen == "Search" -> {
            val explorarViewModel: ExplorarViewModel = viewModel(
                factory = ExplorarViewModelFactory(textoRepo)
            )
            ExplorarScreen(
                onNavigate = { currentScreen = it },
                onTextoClick = { id ->
                    selectedTextoId = id
                    currentScreen = "PostDetail"
                },
                viewModel = explorarViewModel
            )
        }
        currentScreen == "Profile" -> {
            val usuarioViewModel: UsuarioViewModel = viewModel(
                factory = UsuarioViewModelFactory(usuarioRepo, textoRepo)
            )
            UsuarioScreen(
                onNavigate = { currentScreen = it },
                onEditTexto = { id ->
                    selectedTextoId = id
                    currentScreen = "EditStory"
                },
                viewModel = usuarioViewModel
            )
        }
        currentScreen == "EditProfile" -> {
            val editProfileViewModel: EditProfileViewModel = viewModel(
                factory = EditProfileViewModelFactory(usuarioRepo)
            )
            EditProfileScreen(
                onBack = { currentScreen = "Profile" },
                onSave = { currentScreen = "Profile" },
                onNavigate = { currentScreen = it },
                viewModel = editProfileViewModel
            )
        }
        currentScreen == "Create" -> {
            val createStoryViewModel: CreateStoryViewModel = viewModel(
                factory = CreateStoryViewModelFactory(textoRepo, usuarioRepo)
            )
            CreateStoryScreen(
                onNavigate = { currentScreen = it },
                viewModel = createStoryViewModel
            )
        }
        currentScreen == "EditStory" -> {
            val createStoryViewModel: CreateStoryViewModel = viewModel(
                factory = CreateStoryViewModelFactory(textoRepo, usuarioRepo)
            )
            CreateStoryScreen(
                onNavigate = { currentScreen = it },
                textoId = selectedTextoId,
                viewModel = createStoryViewModel
            )
        }
        currentScreen == "Chat" -> {
            val chatViewModel: ChatViewModel = viewModel(
                factory = ChatViewModelFactory(chatRepo)
            )
            ChatListScreen(
                onNavigate = { currentScreen = it },
                onChatClick = { chatSummary ->
                    selectedChatName = chatSummary.name
                    currentScreen = "ChatDetail"
                },
                viewModel = chatViewModel
            )
        }
        currentScreen == "ChatDetail" -> {
            val chatViewModel: ChatViewModel = viewModel(
                factory = ChatViewModelFactory(chatRepo)
            )
            ChatDetailScreen(
                chatName = selectedChatName,
                onBack = { currentScreen = "Chat" },
                viewModel = chatViewModel
            )
        }
        currentScreen == "PostDetail" -> {
            val postDetailViewModel: PostDetailViewModel = viewModel(
                factory = PostDetailViewModelFactory(textoRepo, usuarioRepo)
            )
            PostDetailScreen(
                textoId = selectedTextoId,
                onBack = { currentScreen = "Feed" },
                viewModel = postDetailViewModel
            )
        }
        else -> {
            Scaffold(
                bottomBar = { SharedBottomNavigation(selectedItem = currentScreen, onNavigate = { currentScreen = it }) }
            ) { padding ->
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tela $currentScreen em desenvolvimento")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    textoId: Int,
    onBack: () -> Unit,
    viewModel: PostDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(textoId) {
        viewModel.carregarPost(textoId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.texto?.titulo ?: "Carregando...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FolioOrange)
            }
        } else {
            LazyColumn(Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
                item {
                    uiState.texto?.let {
                        Text(it.titulo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Por Autor #${it.autorId}", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text(it.conteudo, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(32.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(16.dp))
                        Text("Comentários", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                    } ?: Text("Post não encontrado")
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = uiState.novoComentario,
                            onValueChange = { viewModel.onNovoComentarioChange(it) },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Escreva um comentário...") }
                        )
                        IconButton(onClick = {
                            viewModel.enviarComentario(textoId)
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                items(uiState.comentarios) { comentario ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Text("Autor #${comentario.autorId}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(comentario.conteudo, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
