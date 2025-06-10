package com.example.safelife.ui.agendamento.profissional

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safelife.viewModel.agendamento.profissional.AgendamentosConfirmadosViewModel

@Composable
fun AgendamentosConfirmadosScreen(
    onAbrirChat: (pacienteId: String, agendamentoId: String, userType: String) -> Unit,
    viewModel: AgendamentosConfirmadosViewModel = viewModel()
) {
    val agendamentos by viewModel.agendamentos.collectAsState()
    val dadosPacientes by viewModel.dadosPacientes.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(agendamentos) { agendamento ->
            val paciente = dadosPacientes[agendamento.pacienteId]

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📅 ${agendamento.data} às ${agendamento.horario}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("👤 ${paciente?.nome ?: "Carregando..."}")
                    Text("📧 ${paciente?.email ?: ""}")
                    Text("📱 ${paciente?.telefone ?: ""}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onAbrirChat(
                                agendamento.pacienteId,
                                agendamento.id,
                                "profissional"
                            )
                        }
                    ) {
                        Text("Abrir Chat")
                    }
                }
            }
        }
    }
}
