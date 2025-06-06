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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    navigateToNovaPostagem: () -> Unit
) {
    val posts by viewModel.posts.collectAsState()
    val userType by viewModel.userType.collectAsState()
    val context = LocalContext.current

    // Buscar tipo do usuário ao abrir a tela
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            viewModel.buscarTipoUsuario(uid)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feed de Apoio") }
            )
        },
        floatingActionButton = {
            if (userType.isNotBlank() && userType.equals("profissional", ignoreCase = true)) {
                FloatingActionButton(onClick = navigateToNovaPostagem) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Nova Postagem")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (posts.isEmpty()) {
                Text(
                    text = "Nenhuma publicação ainda.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(posts.reversed()) { post ->
                        PostItem(
                            post = post,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    viewModel: FeedViewModel
) {
    var liked by remember { mutableStateOf(false) }
    var novoComentario by remember { mutableStateOf(TextFieldValue("")) }
    val comentarios by viewModel.getCommentsForPost(post.id).collectAsState(initial = emptyList())
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val likeColor by animateColorAsState(
        targetValue = if (liked) Color(0xFF00C8FF) else MaterialTheme.colorScheme.onSurfaceVariant
    )

    val highlightColor = Color(0xFF00C8FF)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = post.authorName,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = {
                        liked = !liked
                        viewModel.atualizarLike(post.id, post.likeCount, liked)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Curtir",
                        tint = likeColor
                    )
                }
                Text(
                    text = "${post.likeCount}",
                    color = highlightColor,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = { /* Visualizar comentários */ }) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Comentários",
                        tint = highlightColor
                    )
                }
                Text(
                    text = "${comentarios.size} comentários",
                    color = highlightColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            comentarios.forEach { comment ->
                Text(
                    text = "${comment.authorName}: ${comment.text}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = novoComentario,
                onValueChange = { novoComentario = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Adicionar comentário...") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (novoComentario.text.isNotBlank()) {
                        coroutineScope.launch {
                            viewModel.enviarComentario(
                                postId = post.id,
                                texto = novoComentario.text,
                                onSuccess = {
                                    Toast.makeText(context, "Comentário enviado!", Toast.LENGTH_SHORT).show()
                                    novoComentario = TextFieldValue("")
                                },
                                onFailure = { erro ->
                                    Toast.makeText(context, erro, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Enviar")
            }
        }
    }
}