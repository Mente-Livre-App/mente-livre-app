package com.example.safelife.ui.chat.profissional

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safelife.viewModel.chat.profissional.ListaPacientesViewModel
import com.example.safelife.viewModel.chat.profissional.ListaPacientesViewModelFactory

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
            TopAppBar(
                title = {
                    Text("Pacientes em conversa", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
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
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pacientes) { paciente ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val agendamentoId = "sem_agendamento"
                                        val userType = "profissional"
                                        navController.navigate(
                                            "chat_profissional/$profissionalId/${paciente.uid}/$agendamentoId/$userType"
                                        )
                                    },
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                ) {
                                    Text("👤 ${paciente.name ?: "Paciente desconhecido"}",
                                        style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("📧 ${paciente.email ?: "-"}",
                                        style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
