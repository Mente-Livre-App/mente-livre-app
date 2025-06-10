package com.example.safelife.ui.agendamento.paciente

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safelife.model.Profissional
import com.example.safelife.viewModel.agendamento.paciente.AgendamentoPacienteViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendamentoPacienteScreen(
    navController: NavController,
    viewModel: AgendamentoPacienteViewModel = viewModel()
) {
    // Observa os estados da ViewModel
    val profissionais by viewModel.profissionais.collectAsState()
    val horariosDisponiveis by viewModel.horariosDisponiveis.collectAsState()
    val diaSelecionado by viewModel.diaSelecionado.collectAsState()
    val horarioSelecionado by viewModel.horarioSelecionado.collectAsState()

    // Estados locais
    var profissionalSelecionado by remember { mutableStateOf<Profissional?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val diasComHorarios = horariosDisponiveis.keys.toList()

    /**
     * Função utilitária que avança ou retrocede o dia atual até encontrar um com horários disponíveis.
     */
    fun avancarDiaAtual(diaAtual: LocalDate, proximo: Boolean): LocalDate {
        var dia = diaAtual
        repeat(7) {
            dia = if (proximo) dia.plusDays(1) else dia.minusDays(1)
            val chave = dia.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
            if (horariosDisponiveis.containsKey(chave)) return dia
        }
        return diaAtual
    }

    // Carrega os profissionais assim que a tela for aberta
    LaunchedEffect(Unit) {
        viewModel.carregarProfissionaisDisponiveis()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Agende uma Consulta",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Dropdown com a lista de profissionais disponíveis
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = profissionalSelecionado?.name ?: "",
                onValueChange = {},
                label = { Text("Profissional") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                profissionais.forEach { profissional ->
                    DropdownMenuItem(
                        text = { Text(profissional.name) },
                        onClick = {
                            viewModel.carregarHorariosParaProfissional(profissional.uid)
                            profissionalSelecionado = profissional
                            viewModel.atualizarDiaSelecionado(LocalDate.now())
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botões para navegar entre os dias com horários
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                val novoDia = avancarDiaAtual(diaSelecionado, proximo = false)
                viewModel.atualizarDiaSelecionado(novoDia)
            }) {
                Text("Dia anterior")
            }

            Button(onClick = {
                val novoDia = avancarDiaAtual(diaSelecionado, proximo = true)
                viewModel.atualizarDiaSelecionado(novoDia)
            }) {
                Text("Próximo dia")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Exibe o dia da semana e data atual selecionada
        Text(
            text = "Dia da Semana: ${diaSelecionado.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))} - ${diaSelecionado.dayOfMonth}/${diaSelecionado.monthValue}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Horários Disponíveis:", style = MaterialTheme.typography.bodyMedium)

        // Traduz o dia da semana para a chave usada no Firebase
        val diaChave = when (diaSelecionado.dayOfWeek) {
            DayOfWeek.MONDAY -> "Seg"
            DayOfWeek.TUESDAY -> "Ter"
            DayOfWeek.WEDNESDAY -> "Qua"
            DayOfWeek.THURSDAY -> "Qui"
            DayOfWeek.FRIDAY -> "Sex"
            DayOfWeek.SATURDAY -> "Sáb"
            DayOfWeek.SUNDAY -> "Dom"
        }
        val listaHorarios = horariosDisponiveis[diaChave] ?: emptyList()

        // Lista de horários disponíveis com seleção via RadioButton
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

        Spacer(modifier = Modifier.height(16.dp))

        // Botão de confirmação do agendamento
        Button(
            onClick = {
                if (profissionalSelecionado != null && horarioSelecionado != null) {
                    viewModel.agendarConsulta(
                        profissionalId = profissionalSelecionado!!.uid,
                        dia = diaChave,
                        horario = horarioSelecionado!!,
                        nome = "Nome do paciente", // 🔧 Pode ser dinâmico no futuro
                        email = "email@paciente.com",
                        telefone = "11999999999",
                        onSuccess = {
                            Toast.makeText(context, "Consulta agendada com sucesso!", Toast.LENGTH_LONG).show()
                        },
                        onFailure = { erro ->
                            Toast.makeText(context, erro, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = profissionalSelecionado != null && horarioSelecionado != null
        ) {
            Text("Agendar")
        }
    }
}
