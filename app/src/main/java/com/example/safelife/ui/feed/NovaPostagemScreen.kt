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

// Composable principal da tela de nova postagem
@Composable
fun NovaPostagemScreen(
    viewModel: FeedViewModel,
    navigateBack: () -> Unit
) {
    // Campo de texto controlado com estado
    var textoPostagem by remember { mutableStateOf(TextFieldValue("")) }
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
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
        }
    }
}
Column(
horizontalAlignment = Alignment.CenterHorizontally,
verticalArrangement = Arrangement.spacedBy(16.dp)
) {
}
// Área de texto para digitar a postagem
TextField(
value = textoPostagem,
onValueChange = { textoPostagem = it },
label = { Text("Escreva sua mensagem de apoio...") },
placeholder = { Text("Compartilhe algo positivo!") },
modifier = Modifier.fillMaxWidth().height(200.dp),
maxLines = 8,
enabled = true
)
// Botão virá no próximo commit