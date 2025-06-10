package com.example.safelife.ui.feed

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
    viewModel: FeedViewModel,
    navigateBack: () -> Unit
) {
    var textoPostagem by remember { mutableStateOf(TextFieldValue("")) }
    val isSending by viewModel.isSending
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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

                Button(
                    onClick = {
                        if (textoPostagem.text.isNotBlank()) {
                            coroutineScope.launch {
                                val currentUser = FirebaseAuth.getInstance().currentUser
                                val userId = currentUser?.uid

                                if (userId != null) {
                                    FirebaseFirestore.getInstance().collection("usuarios").document(userId)
                                        .get()
                                        .addOnSuccessListener { document ->
                                            val nome = document.getString("name") ?: "Usuário"

                                            viewModel.enviarPost(
                                                nomeAutor = nome,
                                                conteudo = textoPostagem.text,
                                                onSuccess = {
                                                    Toast.makeText(context, "Postagem enviada com sucesso!", Toast.LENGTH_SHORT).show()
                                                    navigateBack()
                                                },
                                                onFailure = { erro ->
                                                    Toast.makeText(context, erro, Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, "Erro ao buscar nome: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        } else {
                            Toast.makeText(context, "Digite algo para postar!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = textoPostagem.text.isNotBlank() && !isSending,
                    modifier = Modifier.fillMaxWidth()
                ) {
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
