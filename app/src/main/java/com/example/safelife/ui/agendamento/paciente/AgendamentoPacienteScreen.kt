package com.example.safelife.ui.agendamento.paciente

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safelife.model.Profissional
import com.example.safelife.viewModel.agendamento.paciente.AgendamentoPacienteViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendamentoPacienteScreen(
    navController: NavController,
    viewModel: AgendamentoPacienteViewModel = viewModel()
) {
    val context = LocalContext.current
    val profissionais by viewModel.profissionais.collectAsState()
    val horariosDisponiveis by viewModel.horariosDisponiveis.collectAsState()
    val diaSelecionado by viewModel.diaSelecionado.collectAsState()
    val horarioSelecionado by viewModel.horarioSelecionado.collectAsState()

    var areaSelecionada by remember { mutableStateOf("Psicologia") }
    var profissionalSelecionado by remember { mutableStateOf<Profissional?>(null) }
    var expandedProfissional by remember { mutableStateOf(false) }
    var expandedArea by remember { mutableStateOf(false) }

    fun avancarDia(proximo: Boolean) {
        var novoDia = diaSelecionado
        repeat(7) {
            novoDia = if (proximo) novoDia.plusDays(1) else novoDia.minusDays(1)
            val chave = when (novoDia.dayOfWeek) {
                DayOfWeek.MONDAY -> "Seg"
                DayOfWeek.TUESDAY -> "Ter"
                DayOfWeek.WEDNESDAY -> "Qua"
                DayOfWeek.THURSDAY -> "Qui"
                DayOfWeek.FRIDAY -> "Sex"
                DayOfWeek.SATURDAY -> "Sáb"
                DayOfWeek.SUNDAY -> "Dom"
            }
            if (horariosDisponiveis.containsKey(chave)) {
                viewModel.atualizarDiaSelecionado(novoDia)
                return
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.carregarProfissionaisDisponiveis()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendamento") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown de área
            ExposedDropdownMenuBox(
                expanded = expandedArea,
                onExpandedChange = { expandedArea = !expandedArea }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = areaSelecionada,
                    onValueChange = {},
                    label = { Text("Escolha uma opção *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedArea) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedArea,
                    onDismissRequest = { expandedArea = false }
                ) {
                    listOf("Psicologia", "Psiquiatria").forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            areaSelecionada = it
                            expandedArea = false
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dropdown de profissional
            ExposedDropdownMenuBox(
                expanded = expandedProfissional,
                onExpandedChange = { expandedProfissional = !expandedProfissional }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = profissionalSelecionado?.name ?: "",
                    onValueChange = {},
                    label = { Text("Profissional *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedProfissional) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedProfissional,
                    onDismissRequest = { expandedProfissional = false }
                ) {
                    profissionais.forEach {
                        DropdownMenuItem(
                            text = { Text(it.name) },
                            onClick = {
                                profissionalSelecionado = it
                                viewModel.carregarHorariosParaProfissional(it.uid)
                                viewModel.atualizarDiaSelecionado(LocalDate.now())
                                expandedProfissional = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 280.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CONSULTA ${areaSelecionada.uppercase()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF333333)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Visualize os dias com horários disponíveis",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Calendário",
                        tint = Color(0xFFF44336)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { avancarDia(false) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Anterior", tint = Color(0xFF4B4B4B))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = diaSelecionado.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("pt", "BR")),
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.Black
                            )
                            Text(
                                text = diaSelecionado.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(onClick = { avancarDia(true) }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Próximo", tint = Color(0xFF4B4B4B))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val diaChave = when (diaSelecionado.dayOfWeek) {
                        DayOfWeek.MONDAY -> "Seg"
                        DayOfWeek.TUESDAY -> "Ter"
                        DayOfWeek.WEDNESDAY -> "Qua"
                        DayOfWeek.THURSDAY -> "Qui"
                        DayOfWeek.FRIDAY -> "Sex"
                        DayOfWeek.SATURDAY -> "Sáb"
                        DayOfWeek.SUNDAY -> "Dom"
                    }

                    val horarios = horariosDisponiveis[diaChave] ?: emptyList()

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        horarios.forEach { horario ->
                            AssistChip(
                                onClick = { viewModel.atualizarHorarioSelecionado(horario) },
                                label = {
                                    Text(
                                        horario,
                                        color = if (horario == horarioSelecionado) Color.White else Color.White
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (horario == horarioSelecionado) Color(0xFFFFB400) else Color(0xFF4B4B4B)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (profissionalSelecionado != null && horarioSelecionado != null) {
                                viewModel.agendarConsulta(
                                    profissionalSelecionado!!.uid,
                                    diaChave,
                                    horarioSelecionado!!,
                                    "Paciente",
                                    "email@paciente.com",
                                    "11999999999",
                                    onSuccess = {
                                        Toast.makeText(
                                            context,
                                            "Agendamento realizado com sucesso!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    },
                                    onFailure = { erro ->
                                        Toast.makeText(context, "Erro: $erro", Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = profissionalSelecionado != null && horarioSelecionado != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFCCCCCC),
                            disabledContentColor = Color.DarkGray
                        )
                    ) {
                        Text("AGENDAR")
                    }
                }
            }
        }
    }
}
