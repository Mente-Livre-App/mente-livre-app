package com.example.safelife.ui.agendamento.paciente

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            }
        }
    }
}
