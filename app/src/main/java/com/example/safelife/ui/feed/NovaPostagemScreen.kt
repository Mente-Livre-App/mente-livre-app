package com.example.safelife.ui.feed

// Importações necessárias para compor a UI, lidar com estado, Firebase e corrotinas
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safelife.viewModel.FeedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaPostagemScreen(
    viewModel: FeedViewModel,            // ViewModel responsável por gerenciar a lógica da postagem
    navigateBack: () -> Unit             // Função que navega de volta após a postagem
) {
    // Estado que armazena o texto digitado na postagem
    var textoPostagem by remember { mutableStateOf(TextFieldValue("")) }

    // Observa se a postagem está sendo enviada
    val isSending by viewModel.isSending

    // Contexto da aplicação para mostrar Toasts
    val context = LocalContext.current

    // Escopo de corrotina para chamadas assíncronas
    val coroutineScope = rememberCoroutineScope()

    // Layout principal com barra superior (TopAppBar)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Postagem") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->

        // Container principal da tela
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo de texto para digitar a mensagem
                TextField(
                    value = textoPostagem,
                    onValueChange = { textoPostagem = it },
                    label = { Text("Escreva sua mensagem de apoio...") },
                    placeholder = { Text("Compartilhe algo positivo!") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 8,
                    enabled = !isSending
                )

                // Botão para publicar a mensagem
                Button(
                    onClick = {
                        // Verifica se o texto não está vazio
                        if (textoPostagem.text.isNotBlank()) {
                            coroutineScope.launch {
                                // Recupera o usuário autenticado
                                val currentUser = FirebaseAuth.getInstance().currentUser
                                val userId = currentUser?.uid

                                // Se o usuário estiver autenticado
                                if (userId != null) {
                                    // Busca o nome do usuário no Firestore
                                    FirebaseFirestore.getInstance().collection("usuarios").document(userId)
                                        .get()
                                        .addOnSuccessListener { document ->
                                            // Recupera o nome ou usa "Usuário" como padrão
                                            val nome = document.getString("name") ?: "Usuário"

                                            // Chama a função do ViewModel para enviar o post
                                            viewModel.enviarPost(
                                                nomeAutor = nome,
                                                conteudo = textoPostagem.text,
                                                onSuccess = {
                                                    Toast.makeText(context, "Postagem enviada com sucesso!", Toast.LENGTH_SHORT).show()
                                                    navigateBack() // Volta para a tela anterior
                                                },
                                                onFailure = { erro ->
                                                    Toast.makeText(context, erro, Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            // Caso não consiga buscar o nome
                                            Toast.makeText(context, "Erro ao buscar nome: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        } else {
                            // Caso o campo esteja vazio
                            Toast.makeText(context, "Digite algo para postar!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    // Habilita o botão somente se há texto e não está enviando
                    enabled = textoPostagem.text.isNotBlank() && !isSending,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Mostra indicador de carregamento ou texto do botão
                    if (isSending) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Publicar")
                    }
                }
            }
        }
    }
}