package com.example.safelife.ui.agendamento.profissional

import androidx.compose.foundation.background
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
import com.example.safelife.viewModel.agendamento.profissional.AgendamentosConfirmadosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendamentosConfirmadosScreen(
    navController: NavController,
    onAbrirChat: (pacienteId: String, agendamentoId: String, userType: String) -> Unit,
    viewModel: AgendamentosConfirmadosViewModel = viewModel()
) {
    val agendamentos by viewModel.agendamentos.collectAsState()
    val dadosPacientes by viewModel.dadosPacientes.collectAsState()

    val blue = Color(0xFF1A3F78)
    val background = Color(0xFFF8F8F8)
    val cardBackground = Color.White

    Scaffold(
        containerColor = background,
        topBar = {
            TopAppBar(
                title = { Text("Agendamentos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            items(agendamentos) { agendamento ->
                val paciente = dadosPacientes[agendamento.pacienteId]

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📅 ${agendamento.data} às ${agendamento.horario}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("👤 ${paciente?.nome ?: "Carregando..."}")
                        Text("📧 ${paciente?.email ?: ""}")
                        Text("📱 ${paciente?.telefone ?: ""}")

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onAbrirChat(
                                    agendamento.pacienteId,
                                    agendamento.id,
                                    "profissional"
                                )
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = blue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp)
                        ) {
                            Text("Abrir Chat", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
