package com.example.safelife.ui.feed

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    navigateToNovaPostagem: () -> Unit,
    navigateBack: () -> Unit
) {
    val posts by viewModel.posts.collectAsState()
    val userType by viewModel.userType.collectAsState()
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            viewModel.buscarTipoUsuario(uid)
            viewModel.carregarPosts()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fórum",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            if (userType.equals("profissional", ignoreCase = true)) {
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
                        PostItem(post = post, viewModel = viewModel)
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
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserId = currentUser?.uid.orEmpty()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isLiked = post.likedBy.contains(currentUserId)
    val likeColor by animateColorAsState(
        targetValue = if (isLiked) Color(0xFF00C8FF) else Color.Gray
    )
    val highlightColor = Color(0xFF00C8FF)

    var novoComentario by remember { mutableStateOf(TextFieldValue("")) }
    val comentarios by viewModel.getCommentsForPost(post.id).collectAsState(initial = emptyList())

    LaunchedEffect(post.id) {
        viewModel.carregarComentarios(post.id)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = post.authorName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        viewModel.toggleLike(
                            postId = post.id,
                            userId = currentUserId,
                            isCurrentlyLiked = isLiked
                        )
                    }
                }) {
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

                IconButton(onClick = { /* ação de comentários */ }) {
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
                Column(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(
                        text = comment.authorName,
                        style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF4A148C))
                    )
                    Text(
                        text = comment.text,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = novoComentario,
                onValueChange = { novoComentario = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Adicionar comentário") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
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
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C8FF))
            ) {
                Text("Enviar", color = Color.White)
            }
        }
    }
}
