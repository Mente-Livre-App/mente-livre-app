package com.example.safelife.ui.chat.paciente

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safelife.viewModel.ListaProfissionaisViewModel

/**
 * Tela que lista os profissionais disponíveis.
 * Ao clicar em um, chama navigateToChat passando o UID dele e do usuário atual.
 */
@Composable
fun ListaProfissionaisScreen(
    currentUserId: String,
    navigateToChat: (String, String) -> Unit,
    viewModel: ListaProfissionaisViewModel = viewModel() // ✅ único viewModel agora
) {
    ListaProfissionaisScreenTestable(viewModel, currentUserId, navigateToChat)
}
@Composable
fun ListaProfissionaisScreenTestable(
    viewModel: ListaProfissionaisViewModel,
    currentUserId: String,
    navigateToChat: (String, String) -> Unit
) {
    val profissionais by viewModel.profissionais.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // ✅ Chamada automática ao abrir a tela
    LaunchedEffect(Unit) {
        viewModel.carregarProfissionaisPorTipo("profissional")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .testTag("loading-indicator")
            )
        } else {
            if (profissionais.isEmpty()) {
                Text(
                    text = "Nenhum profissional encontrado.",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("empty-message"),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(profissionais) { profissional ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    navigateToChat(currentUserId, profissional.uid)
                                },
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = profissional.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF333333)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = profissional.email,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF666666)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
