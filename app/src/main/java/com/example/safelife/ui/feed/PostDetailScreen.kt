package com.example.safelife.ui.feed

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safelife.model.Comment
import com.example.safelife.viewModel.PostDetailViewModel
import com.example.safelife.viewModel.PostDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    viewModel: PostDetailViewModel = viewModel(factory = PostDetailViewModelFactory(postId))
) {
    val post by viewModel.post.collectAsState()
    val comments by viewModel.comments.collectAsState()
    var novoComentario by remember { mutableStateOf(TextFieldValue("")) }
    val isSending by viewModel.isSending.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comentários") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            post?.let { loadedPost ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = loadedPost.authorName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = loadedPost.content,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Comentários (${comments.size})",
                style = MaterialTheme.typography.titleSmall
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(comments) { comment ->
                    CommentItem(comment)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = novoComentario,
                    onValueChange = { novoComentario = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Digite um comentário...") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val user = "Usuário" // Em breve podemos pegar do Firebase Auth
                        if (novoComentario.text.isNotBlank()) {
                            viewModel.enviarComentario(user, novoComentario.text) {
                                Toast.makeText(context, "Comentário enviado!", Toast.LENGTH_SHORT).show()
                                novoComentario = TextFieldValue("")
                            }
                        } else {
                            Toast.makeText(context, "Escreva algo para comentar!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = novoComentario.text.isNotBlank() && !isSending
                ) {
                    Text("Enviar")
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = comment.authorName,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = comment.formattedDate,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
