package com.example.safelife.ui.agendamento.paciente

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import java.time.format.TextStyle
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safelife.repository.Profissional
import com.example.safelife.viewModel.agendamento.paciente.AgendamentoPacienteViewModel
@Composable
fun AgendamentoPacienteScreen(
    navController: NavController,
    viewModel: AgendamentoPacienteViewModel = viewModel()
) {
    val profissionais by viewModel.profissionais.collectAsState()
    var profissionalSelecionado by remember { mutableStateOf<Profissional?>(null) }
    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.carregarProfissionaisDisponiveis()
    }
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Agende uma Consulta",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = profissionalSelecionado?.nome ?: "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                label = { Text("Profissional") },
                readOnly = true
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                profissionais.forEach { profissional ->
                    DropdownMenuItem(
                        text = { Text(profissional.nome) },
                        onClick = {
                            viewModel.carregarHorariosParaProfissional(profissional.uid)
                            profissionalSelecionado = profissional
                            expanded = false
                        }
                    )
                }
                val horariosDisponiveis by viewModel.horariosDisponiveis.collectAsState()
                val diaSelecionado by viewModel.diaSelecionado.collectAsState()
                val horarioSelecionado by viewModel.horarioSelecionado.collectAsState()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Dia da Semana: ${diaSelecionado.dayOfWeek.getDisplayName(TextStyle.FULL,
                        Locale("pt", "BR"))} - ${diaSelecionado.dayOfMonth}/${diaSelecionado.monthValue}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Horários Disponíveis:", style = MaterialTheme.typography.bodyMedium)
                val listaHorarios = horariosDisponiveis[diaSelecionado.dayOfWeek.name] ?: emptyList()
                LazyColumn {
                    items(listaHorarios) { horario ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.atualizarHorarioSelecionado(horario) }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = horarioSelecionado == horario,
                                onClick = { viewModel.atualizarHorarioSelecionado(horario) }
                            )
                            Text(text = horario)
                        }
                    }

                }
        }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (profissionalSelecionado != null && horarioSelecionado != null) {
                        viewModel.agendarConsulta(
                            profissionalId = profissionalSelecionado!!.uid,
                            dia = diaSelecionado.dayOfWeek.name,
                            horario = horarioSelecionado!!,
                            nome = "Nome do paciente",
                            email = "email@paciente.com",
                            telefone = "11999999999"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = profissionalSelecionado != null && horarioSelecionado != null
            ) {
            }
            Text("Agendar")
    }
}
