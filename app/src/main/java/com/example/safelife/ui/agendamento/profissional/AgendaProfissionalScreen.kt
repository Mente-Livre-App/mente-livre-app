package com.example.safelife.ui.agendamento.profissional

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safelife.viewModel.agendamento.profissional.AgendaProfissionalViewModel
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Definir Disponibilidade",
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        SnackbarHost(hostState = snackbarHostState)
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
        ) {
            .padding(bottom = 16.dp)
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
                }
            }
        }