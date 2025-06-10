package com.example.safelife.ui.agendamento

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Componente visual de seleção de data e horário para agendamento.
 *
 * @param dataSelecionada Data atualmente exibida no formato dd/MM/yyyy.
 * @param onChangeData Callback para mudar a data (navegação com setas).
 * @param horariosDisponiveis Lista de horários disponíveis para a data.
 * @param horarioSelecionado Horário atualmente selecionado.
 * @param onSelecionarHorario Callback ao selecionar um horário.
 * @param onAgendar Ação executada ao clicar no botão AGENDAR.
 */
@Composable
fun AgendaCard(
    dataSelecionada: String,
    onChangeData: (String) -> Unit,
    horariosDisponiveis: List<String>,
    horarioSelecionado: String,
    onSelecionarHorario: (String) -> Unit,
    onAgendar: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    val diaSemana = try {
        val date = dateFormatter.parse(dataSelecionada)
        date?.let {
            SimpleDateFormat("EEEE", Locale("pt", "BR")).format(it)
                .replaceFirstChar { c -> c.uppercase() }
        } ?: ""
    } catch (e: Exception) {
        ""
    }

    val calendario = Calendar.getInstance()
    val dataAtual = dateFormatter.parse(dataSelecionada) ?: calendario.time
    calendario.time = dataAtual

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CONSULTA PSICOLOGIA",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Visualize os dias com horários disponíveis",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Seletor de data com setas
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    calendario.add(Calendar.DAY_OF_MONTH, -1)
                    onChangeData(dateFormatter.format(calendario.time))
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Anterior")
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = diaSemana, style = MaterialTheme.typography.labelLarge)
                    Text(text = dataSelecionada, style = MaterialTheme.typography.bodyMedium)
                }

                IconButton(onClick = {
                    calendario.add(Calendar.DAY_OF_MONTH, 1)
                    onChangeData(dateFormatter.format(calendario.time))
                }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Próximo")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lista de horários
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                horariosDisponiveis.forEach { horario ->
                    val isSelected = horario == horarioSelecionado
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color(0xFFFFC43D) else Color(0xFF616161),
                        modifier = Modifier
                            .clickable { onSelecionarHorario(horario) }
                    ) {
                        Text(
                            text = horario,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão AGENDAR
            Button(
                onClick = onAgendar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFED474A)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("AGENDAR", color = Color.White)
            }
        }
    }
}
