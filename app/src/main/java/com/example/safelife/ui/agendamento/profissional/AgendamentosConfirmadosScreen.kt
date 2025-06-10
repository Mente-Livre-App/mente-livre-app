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
    // Coleta os estados expostos pelo ViewModel (agendamentos confirmados e dados dos pacientes)
    val agendamentos by viewModel.agendamentos.collectAsState()
    val dadosPacientes by viewModel.dadosPacientes.collectAsState()

    // Lista em coluna com preenchimento
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Para cada agendamento confirmado, exibe um card com os detalhes
        items(agendamentos) { agendamento ->
            val paciente = dadosPacientes[agendamento.pacienteId] // Busca os dados do paciente associado

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Exibe data e horário da consulta
                    Text(
                        text = "📅 ${agendamento.data} às ${agendamento.horario}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Exibe informações básicas do paciente (nome, e-mail, telefone)
                    Text("👤 ${paciente?.nome ?: "Carregando..."}")
                    Text("📧 ${paciente?.email ?: ""}")
                    Text("📱 ${paciente?.telefone ?: ""}")

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botão que aciona o chat entre profissional e paciente
                    Button(
                        onClick = {
                            onAbrirChat(
                                agendamento.pacienteId,
                                agendamento.id,
                                "profissional" // Define o tipo de usuário que iniciou
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
