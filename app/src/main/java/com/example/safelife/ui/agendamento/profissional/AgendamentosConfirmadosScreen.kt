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
import com.example.safelife.viewModel.agendamento.profissional.Agendamento
@Composable
fun AgendamentosConfirmadosScreen(
    onAbrirChat: (pacienteId: String, agendamentoId: String, userType: String) -> Unit,
    viewModel: AgendamentosConfirmadosViewModel = viewModel()
) {
    val agendamentos by viewModel.agendamentos.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(agendamentos) { agendamento ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        " ${agendamento.data} s ${agendamento.horario}", style =
                            MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(" ${agendamento.nomePaciente}")
                    Text(" ${agendamento.emailPaciente}")
                    Text(" ${agendamento.telefonePaciente}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onAbrirChat(
                                agendamento.pacienteId,
                                agendamento.agendamentoId,
                                "profissional"
                            )
                        }
                    ) {
                    }
                }
                Text("Abrir Chat")
            }
        }
    }
}