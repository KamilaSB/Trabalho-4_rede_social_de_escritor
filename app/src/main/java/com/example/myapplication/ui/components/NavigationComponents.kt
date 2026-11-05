package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.FolioOrange

@Composable
fun SharedBottomNavigation(selectedItem: String, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = Color.White, modifier = Modifier.height(80.dp)) {
        val items = listOf(
            Triple("Feed", Icons.Filled.Description, Icons.Outlined.Description),
            Triple("Chat", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline),
            Triple("Create", Icons.Filled.Add, Icons.Outlined.Add),
            Triple("Search", Icons.Default.Search, Icons.Outlined.Search),
            Triple("Profile", Icons.Filled.Person, Icons.Outlined.Person)
        )
        items.forEach { (name, filledIcon, outlinedIcon) ->
            NavigationBarItem(
                selected = selectedItem == name,
                onClick = { onNavigate(name) },
                icon = { Icon(if (selectedItem == name) filledIcon else outlinedIcon, contentDescription = name) },
                label = { Text(name, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FolioOrange,
                    selectedTextColor = FolioOrange,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
