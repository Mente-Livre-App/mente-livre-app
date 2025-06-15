package com.example.safelife.ui.feed

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
                title = {
                    Text(
                        "Nova Postagem",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card com borda preta
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                border = BorderStroke(1.dp, Color.Black),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
            ) {
                TextField(
                    value = textoPostagem,
                    onValueChange = { textoPostagem = it },
                    placeholder = { Text("Escreva sua mensagem de apoio...") },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão de publicar
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Publicar", color = Color.White)
                }
            }
        }
    }
}
