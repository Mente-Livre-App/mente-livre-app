package com.example.safelife.ui.agendamento.profissional.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.safelife.viewModel.agendamento.profissional.AgendaProfissionalViewModel

@Composable
fun HorarioSelector(
    diaSelecionado: String,
    horarios: List<String>,
    horariosSelecionados: Set<String>,
    onChange: (Set<String>) -> Unit,
    viewModel: AgendaProfissionalViewModel,
    edicoesPendentes: MutableState<Map<String, Set<String>>>
) {
    Column {
        horarios.chunked(4).forEach { linha ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                linha.forEach { hora ->
                    val selecionado = hora in horariosSelecionados
                    OutlinedButton(
                        onClick = {
                            val novoSet = if (selecionado)
                                horariosSelecionados - hora
                            else
                                horariosSelecionados + hora

                            onChange(novoSet)

                            val atual = edicoesPendentes.value.toMutableMap()
                            atual[diaSelecionado] = novoSet
                            edicoesPendentes.value = atual
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selecionado) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(1f)
                    ) {
                        Text(hora)
                    }
                }
            }
        }
    }
}