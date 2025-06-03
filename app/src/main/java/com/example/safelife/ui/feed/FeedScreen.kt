package com.example.safelife.ui.feed

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.example.safelife.model.Comment
import androidx.compose.material.icons.filled.ChatBubble

import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.safelife.model.Post
import com.example.safelife.viewModel.FeedViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
// Composable principal que representa a tela de feed com uma lista de postagens
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    navigateToNovaPostagem: () -> Unit
) {
    // Observa a lista de postagens do ViewModel
    val posts by viewModel.posts.collectAsState()
    // Estrutura base da tela usando Scaffold
    Scaffold(
        topBar = { TopAppBar(title = { Text("Feed de Apoio") }) },
        floatingActionButton = { /* será adicionado no commit 2 */ }
    ) { paddingValues ->
        // Área principal da tela com padding interno
        Box(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            if (posts.isEmpty()) {
                // Mensagem exibida quando não há postagens
                Text(
                    text = "Nenhuma publicação ainda.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Lista de postagens usando LazyColumn
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(posts.reversed()) { post ->
                        // Exibição temporária de nome do autor (será substituído por
                        PostItem)
                        Text("Postagem de ${post.authorName}")
                    }
                }
            }
        }
    }
}