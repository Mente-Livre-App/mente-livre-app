package com.example.safelife.ui.agendamento.profissional

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.safelife.viewModel.agendamento.profissional.AgendaProfissionalViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaProfissionalScreen(
    navController: NavController,
    profissionalId: String,
    viewModel: AgendaProfissionalViewModel = viewModel()
) {
    val red = Color(0xFFF44336)
    val yellow = Color(0xFFFFB400)
    val darkGray = Color(0xFF4B4B4B)
    val lightBackground = Color(0xFFF8F8F8)

    LaunchedEffect(Unit) {
        viewModel.salvarDadosProfissionalNoFirestore()
    }

    val coroutineScope = rememberCoroutineScope()
    val edicoesPendentes = remember { mutableStateOf<Map<String, Set<String>>>(emptyMap()) }
    val snackbarHostState = remember { SnackbarHostState() }

    val diasSemana = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
    val horarios = listOf("08:00", "09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00")

    var diaSelecionado by remember { mutableStateOf<String?>(null) }
    var horariosSelecionados by remember { mutableStateOf(emptySet<String>()) }

    Scaffold(
        containerColor = lightBackground,
        topBar = {
            TopAppBar(
                title = { Text("Disponibilidade", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Black),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Selecione os dias disponíveis:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        diasSemana.forEach { dia ->
                            val selecionado = dia == diaSelecionado
                            AssistChip(
                                onClick = {
                                    diaSelecionado = dia
                                    horariosSelecionados = edicoesPendentes.value[dia]
                                        ?: viewModel.obterHorariosParaDia(dia)
                                },
                                label = { Text(dia) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (selecionado) yellow else Color.LightGray,
                                    labelColor = if (selecionado) Color.White else Color.Black
                                )
                            )
                        }
                    }

                    if (diaSelecionado != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Horários para $diaSelecionado:",
                            style = MaterialTheme.typography.titleMedium
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            horarios.forEach { horario ->
                                val selecionado = horariosSelecionados.contains(horario)
                                AssistChip(
                                    onClick = {
                                        horariosSelecionados = if (selecionado) {
                                            horariosSelecionados - horario
                                        } else {
                                            horariosSelecionados + horario
                                        }
                                        edicoesPendentes.value = edicoesPendentes.value.toMutableMap().apply {
                                            set(diaSelecionado!!, horariosSelecionados)
                                        }
                                    },
                                    label = { Text(horario) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (selecionado) yellow else darkGray,
                                        labelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                edicoesPendentes.value.forEach { (dia, horarios) ->
                                    viewModel.salvarHorarios(dia, horarios)
                                }
                                viewModel.atualizarHorariosNoFirestore(viewModel.disponibilidade.value)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Disponibilidade salva com sucesso!")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = red)
                        ) {
                            Text("Salvar Disponibilidade", color = Color.White)
                        }

                        if (horariosSelecionados.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Horários salvos para $diaSelecionado:",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            horariosSelecionados.forEach { hora ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(hora)
                                    TextButton(
                                        onClick = {
                                            if (viewModel.podeRemover(diaSelecionado!!, hora)) {
                                                horariosSelecionados -= hora
                                                viewModel.salvarHorarios(diaSelecionado!!, horariosSelecionados)
                                            } else {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        "Horário já agendado. Contate o paciente."
                                                    )
                                                }
                                            }
                                        }
                                    ) {
                                        Text("Remover", color = red)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("agendamentosConfirmados") },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = red),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Meus Agendamentos", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
