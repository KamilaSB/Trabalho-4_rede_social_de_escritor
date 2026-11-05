package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*

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

@Composable
fun MainScreen() {
    // Estado para controlar qual tela está ativa
    var currentScreen by remember { mutableStateOf("Feed") }
    var selectedChatName by remember { mutableStateOf("") }

    when (currentScreen) {
        "Feed" -> FolioApp(onNavigate = { currentScreen = it })
        "Search" -> ExplorarScreen(onNavigate = { currentScreen = it })
        "Profile" -> UsuarioScreen(onNavigate = { currentScreen = it })
        "Create" -> CreateStoryScreen(onNavigate = { currentScreen = it })
        "Chat" -> ChatListScreen(
            onNavigate = { currentScreen = it },
            onChatClick = { chat ->
                selectedChatName = chat.name
                currentScreen = "ChatDetail"
            }
        )
        "ChatDetail" -> ChatDetailScreen(
            chatName = selectedChatName,
            onBack = { currentScreen = "Chat" }
        )
        else -> {
            // Placeholder para outras telas
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

data class Post(
    val authorName: String,
    val authorHandle: String,
    val title: String,
    val content: String,
    val likes: String,
    val comments: String
)

val samplePosts = listOf(
    Post(
        "Virginia Woolf",
        "@virginia_w",
        "To the Lighthouse",
        "\"The lighthouse stood out stark and clear; a magnificent sentinel against the gray and rolling tide of afternoon memories...\"",
        "1.2k",
        "340"
    ),
    Post(
        "Fernando Pessoa",
        "@desassossego",
        "Livro do Desassossego",
        "\"Ah, as estradas de Portugal ao entardecer, quando a névoa beija os telhados amigos e a alma se dissolve in prosa silenciada...\"",
        "945",
        "188"
    ),
    Post(
        "Clarice Lispector",
        "@clarice_l",
        "A Hora da Estrela",
        "\"Tudo no mundo começou com um sim. Uma molécula disse sim a outra molécula e nasceu a vida...\"",
        "2.5k",
        "560"
    ),
    Post(
        "Machado de Assis",
        "@machado_assis",
        "Dom Casmurro",
        "\"Olhos de ressaca? Sim, olhos de ressaca. Eram assim, com aquela força de atrair e de prender...\"",
        "3.1k",
        "890"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolioApp(onNavigate: (String) -> Unit) {
    Scaffold(
        containerColor = FolioBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Folio",
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
            SharedBottomNavigation(selectedItem = "Feed", onNavigate = onNavigate)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(samplePosts) { post ->
                PostCard(post)
            }
        }
    }
}

@Composable
fun PostCard(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        post.authorName,
                        fontWeight = FontWeight.Bold,
                        color = FolioTextPrimary,
                        fontSize = 15.sp
                    )
                    Text(
                        post.authorHandle,
                        color = FolioTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                post.title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = FolioTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Image Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF2F2F2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                post.content,
                fontStyle = FontStyle.Italic,
                color = FolioTextSecondary,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        modifier = Modifier.size(20.dp),
                        tint = FolioTextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(post.likes, fontSize = 13.sp, color = FolioTextSecondary)
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(
                        Icons.Outlined.Email,
                        contentDescription = "Comment",
                        modifier = Modifier.size(20.dp),
                        tint = FolioTextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(post.comments, fontSize = 13.sp, color = FolioTextSecondary)
                }
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Bookmark",
                    modifier = Modifier.size(20.dp),
                    tint = FolioTextSecondary
                )
            }
        }
    }
}

@Composable
fun SharedBottomNavigation(selectedItem: String, onNavigate: (String) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            selected = selectedItem == "Feed",
            onClick = { onNavigate("Feed") },
            icon = { Icon(if (selectedItem == "Feed") Icons.Filled.Description else Icons.Outlined.Description, contentDescription = null) },
            label = { Text("Feed", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FolioOrange,
                selectedTextColor = FolioOrange,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selectedItem == "Chat" || selectedItem == "ChatDetail",
            onClick = { onNavigate("Chat") },
            icon = { Icon(if (selectedItem == "Chat" || selectedItem == "ChatDetail") Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline, contentDescription = null) },
            label = { Text("Chat", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FolioOrange,
                selectedTextColor = FolioOrange,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selectedItem == "Create",
            onClick = { onNavigate("Create") },
            icon = { Icon(Icons.Outlined.Add, contentDescription = "Create") },
            label = { Text("Create", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FolioOrange,
                selectedTextColor = FolioOrange,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selectedItem == "Search",
            onClick = { onNavigate("Search") },
            icon = { Icon(if (selectedItem == "Search") Icons.Default.Search else Icons.Outlined.Search, contentDescription = "Search") },
            label = { Text("Search", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FolioOrange,
                selectedTextColor = FolioOrange,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selectedItem == "Profile",
            onClick = { onNavigate("Profile") },
            icon = { Icon(if (selectedItem == "Profile") Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
            label = { Text("Profile", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FolioOrange,
                selectedTextColor = FolioOrange,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
    }
}
