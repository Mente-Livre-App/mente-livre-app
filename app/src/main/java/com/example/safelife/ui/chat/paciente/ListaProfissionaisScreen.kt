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
 * Tela que lista os profissionais disponíveis para o paciente.
 * Ao selecionar um profissional, a tela dispara a navegação para a conversa (chat).
 *
 * @param currentUserId ID do paciente logado.
 * @param navigateToChat Função de navegação que recebe (idPaciente, idProfissional).
 */
@Composable
fun ListaProfissionaisScreen(
    currentUserId: String,
    navigateToChat: (String, String) -> Unit,
    viewModel: ListaProfissionaisViewModel = viewModel()
) {
    // Encapsula a tela principal em uma função testável
    ListaProfissionaisScreenTestable(viewModel, currentUserId, navigateToChat)
}

@Composable
fun ListaProfissionaisScreenTestable(
    viewModel: ListaProfissionaisViewModel,
    currentUserId: String,
    navigateToChat: (String, String) -> Unit
) {
    // Observa os estados reativos da ViewModel
    val profissionais by viewModel.profissionais.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Ao abrir a tela, carrega os profissionais com tipo "profissional"
    LaunchedEffect(Unit) {
        viewModel.carregarProfissionaisPorTipo("profissional")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            // Mostra indicador de carregamento enquanto busca os dados
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("loading-indicator")
                )
            }

            // Exibe mensagem caso nenhum profissional seja encontrado
            profissionais.isEmpty() -> {
                Text(
                    text = "Nenhum profissional encontrado.",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("empty-message"),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Exibe a lista de profissionais em formato de cartão clicável
            else -> {
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
                                    // Ao clicar, abre o chat entre o paciente e o profissional
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
