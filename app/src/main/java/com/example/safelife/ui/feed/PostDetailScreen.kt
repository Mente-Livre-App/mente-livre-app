package com.example.safelife.ui.feed

// Importações necessárias para o Compose, ViewModel e outros componentes utilizados
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
    postId: String, // ID do post que será carregado
    viewModel: PostDetailViewModel = viewModel(factory = PostDetailViewModelFactory(postId)) // ViewModel com factory para carregar dados do post
) {
    // Observa o estado do post e dos comentários
    val post by viewModel.post.collectAsState()
    val comments by viewModel.comments.collectAsState()

    // Estado local para armazenar o texto do novo comentário
    var novoComentario by remember { mutableStateOf(TextFieldValue("")) }

    // Observa o estado de envio (usado para desabilitar botão durante envio)
    val isSending by viewModel.isSending.collectAsState()

    // Contexto usado para mostrar Toasts
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comentários") } // Título do topo da tela
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Exibe os dados do post, se carregado
            post?.let { loadedPost ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = loadedPost.authorName, // Nome do autor do post
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = loadedPost.content, // Conteúdo do post
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Exibe o número total de comentários
            Text(
                text = "Comentários (${comments.size})",
                style = MaterialTheme.typography.titleSmall
            )

            // Lista de comentários com rolagem
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Ocupa o espaço restante da tela
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(comments) { comment ->
                    CommentItem(comment) // Componente para exibir cada comentário
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de digitação e botão de envio de novo comentário
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Campo de texto para digitar o comentário
                OutlinedTextField(
                    value = novoComentario,
                    onValueChange = { novoComentario = it },
                    modifier = Modifier.weight(1f), // Ocupa o maior espaço possível
                    placeholder = { Text("Digite um comentário...") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Botão de envio do comentário
                Button(
                    onClick = {
                        val user = "Usuário" // Nome fixo por enquanto, pode ser substituído pelo nome do usuário autenticado futuramente
                        if (novoComentario.text.isNotBlank()) {
                            // Envia o comentário via ViewModel
                            viewModel.enviarComentario(user, novoComentario.text) {
                                Toast.makeText(context, "Comentário enviado!", Toast.LENGTH_SHORT).show()
                                novoComentario = TextFieldValue("") // Limpa o campo após envio
                            }
                        } else {
                            Toast.makeText(context, "Escreva algo para comentar!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = novoComentario.text.isNotBlank() && !isSending // Habilita o botão apenas se houver texto e não estiver enviando
                ) {
                    Text("Enviar")
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    // Componente reutilizável para exibir cada comentário
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = comment.authorName, // Nome do autor do comentário
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.text, // Texto do comentário
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = comment.formattedDate, // Data formatada do comentário
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
