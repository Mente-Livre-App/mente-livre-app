package com.example.safelife.ui.agendamento.profissional

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safelife.viewModel.agendamento.profissional.AgendaProfissionalViewModel
import java.util.*
import kotlinx.coroutines.launch
import com.example.safelife.ui.agendamento.profissional.componentes.HorarioSelector


@Composable
fun AgendaProfissionalScreen(
    navController: NavController,
    profissionalId: String,
    viewModel: AgendaProfissionalViewModel = viewModel()
) {
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

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)) {
        Text(
            text = "Definir Disponibilidade",
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        SnackbarHost(hostState = snackbarHostState)

        // Botões dos dias da semana
        Row(modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(bottom = 16.dp)) {
            diasSemana.forEach { dia ->
                val selecionado = dia == diaSelecionado
                Button(
                    onClick = {
                        diaSelecionado = dia
                        horariosSelecionados = edicoesPendentes.value[dia]
                            ?: viewModel.obterHorariosParaDia(dia)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selecionado) Color(0xFF2374AB) else Color.LightGray
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(dia, color = Color.White)
                }
            }
        }

        if (diaSelecionado != null) {
            Text(
                text = "Horários para $diaSelecionado:",
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Grade de horários
            HorarioSelector(
                diaSelecionado = diaSelecionado!!,
                horarios = horarios,
                horariosSelecionados = horariosSelecionados,
                onChange = { horariosSelecionados = it },
                viewModel = viewModel,
                edicoesPendentes = edicoesPendentes
            )


            Spacer(modifier = Modifier.height(16.dp))

            // Botão para salvar (mock)
            Button(
                onClick = {
                    edicoesPendentes.value.forEach { (dia, horarios) ->
                        viewModel.salvarHorarios(dia, horarios)
                    }

                    // Salva apenas os horários
                    viewModel.atualizarHorariosNoFirestore(viewModel.disponibilidade.value)

                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Disponibilidade salva com sucesso no Firebase")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Disponibilidade")
            }

        }

        if (horariosSelecionados.isNotEmpty()) {
            Text(
                text = "Horários salvos para $diaSelecionado:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )

            horariosSelecionados.forEach { hora ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(hora)
                    Button(
                        onClick = {
                            if (viewModel.podeRemover(diaSelecionado!!, hora)) {
                                horariosSelecionados = horariosSelecionados - hora
                                viewModel.salvarHorarios(diaSelecionado!!, horariosSelecionados)
                            } else {
                                // mostra mensagem
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Horário já agendado, entre em contato com o paciente antes de cancelar"
                                    )
                                }
                            }
                        }
                    ) {
                        Text("Remover")
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        // Botão Meus Agendamentos
        Button(
            onClick = {
                navController.navigate("agendamentosConfirmados")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Meus Agendamentos")
        }
    }
}