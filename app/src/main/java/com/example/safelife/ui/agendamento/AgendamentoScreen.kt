package com.example.safelife.ui.agendamento

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safelife.viewModel.AgendamentoViewModel
import com.example.safelife.viewModel.ListaProfissionaisViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendamentoScreen(
    pacienteId: String,
    viewModel: AgendamentoViewModel = viewModel(),
    profissionaisViewModel: ListaProfissionaisViewModel = viewModel(),
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val profissionais by profissionaisViewModel.profissionais.collectAsState()
    val isLoading by profissionaisViewModel.isLoading.collectAsState()

    val tiposProfissionais = listOf("Psicólogo", "Psiquiatra", "Terapeuta")
    var tipoSelecionado by remember { mutableStateOf("") }

    var expandedTipo by remember { mutableStateOf(false) }
    var expandedProfissional by remember { mutableStateOf(false) }

    var nomeProfissionalSelecionado by remember { mutableStateOf("") }
    var profissionalSelecionado by remember { mutableStateOf("") }

    // Estado da data e horário
    var dataSelecionada by remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date())) }
    var horarioSelecionado by remember { mutableStateOf("") }
    val horariosDisponiveis = listOf("09:50", "11:30", "14:00")

    Scaffold(
        topBar = { TopAppBar(title = { Text("Agendar Consulta") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 🔽 Dropdown de tipo de profissional
            ExposedDropdownMenuBox(
                expanded = expandedTipo,
                onExpandedChange = { expandedTipo = !expandedTipo }
            ) {
                OutlinedTextField(
                    value = tipoSelecionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de Profissional") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedTipo,
                    onDismissRequest = { expandedTipo = false }
                ) {
                    tiposProfissionais.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                tipoSelecionado = it
                                nomeProfissionalSelecionado = ""
                                profissionalSelecionado = ""
                                expandedTipo = false

                                // 🔁 Busca do Firebase
                                profissionaisViewModel.carregarProfissionaisPorTipo(it)
                            }
                        )
                    }
                }
            }

            // 🔽 Dropdown de profissionais filtrados
            ExposedDropdownMenuBox(
                expanded = expandedProfissional,
                onExpandedChange = { expandedProfissional = !expandedProfissional }
            ) {
                OutlinedTextField(
                    value = nomeProfissionalSelecionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Profissional") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedProfissional,
                    onDismissRequest = { expandedProfissional = false }
                ) {
                    profissionais.forEach {
                        DropdownMenuItem(
                            text = { Text(it.name) },
                            onClick = {
                                nomeProfissionalSelecionado = it.name
                                profissionalSelecionado = it.uid
                                expandedProfissional = false
                            }
                        )
                    }
                }
            }

            // 📅 Agenda visual com horários
            AgendaCard(
                dataSelecionada = dataSelecionada,
                onChangeData = { novaData -> dataSelecionada = novaData },
                horariosDisponiveis = horariosDisponiveis,
                horarioSelecionado = horarioSelecionado,
                onSelecionarHorario = { horarioSelecionado = it },
                onAgendar = {
                    when {
                        tipoSelecionado.isBlank() -> Toast.makeText(context, "Selecione o tipo de profissional", Toast.LENGTH_SHORT).show()
                        profissionalSelecionado.isBlank() -> Toast.makeText(context, "Selecione o profissional", Toast.LENGTH_SHORT).show()
                        horarioSelecionado.isBlank() -> Toast.makeText(context, "Selecione um horário", Toast.LENGTH_SHORT).show()
                        else -> {
                            viewModel.agendarConsulta(
                                pacienteId = pacienteId,
                                profissionalId = profissionalSelecionado,
                                data = dataSelecionada,
                                horario = horarioSelecionado,
                                onSuccess = {
                                    Toast.makeText(context, "Consulta agendada com sucesso", Toast.LENGTH_SHORT).show()
                                    navigateBack()
                                },
                                onFailure = {
                                    Toast.makeText(context, "Erro ao agendar: $it", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            )

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
