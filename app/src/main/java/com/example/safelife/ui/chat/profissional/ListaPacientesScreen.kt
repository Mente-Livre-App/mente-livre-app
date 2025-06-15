package com.example.safelife.ui.chat.profissional

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safelife.viewModel.chat.profissional.ListaPacientesViewModel
import com.example.safelife.viewModel.chat.profissional.ListaPacientesViewModelFactory

/**
 * Tela que exibe a lista de pacientes que iniciaram conversa com o profissional.
 *
 * @param navController Controlador de navegação para redirecionar ao chat.
 * @param profissionalId ID do profissional logado.
 * @param viewModelOverride (opcional) permite injetar um ViewModel para testes ou preview.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaPacientesScreen(
    navController: NavController,
    profissionalId: String,
    viewModelOverride: ListaPacientesViewModel? = null
) {
    val viewModel = viewModelOverride ?: viewModel(
        factory = ListaPacientesViewModelFactory(profissionalId)
    )

    val pacientes by viewModel.pacientes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.buscarPacientesComConversa()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pacientes em conversa") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                pacientes.isEmpty() -> {
                    Text(
                        text = "Nenhum paciente iniciou conversa ainda.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(pacientes) { paciente ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        val agendamentoId = "sem_agendamento" // Ajuste conforme sua lógica futura
                                        val userType = "profissional"

                                        navController.navigate(
                                            "chat_profissional/$profissionalId/${paciente.uid}/$agendamentoId/$userType"
                                        )
                                    },
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = paciente.name ?: "Paciente desconhecido",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = paciente.email ?: "",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
