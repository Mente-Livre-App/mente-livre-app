package com.example.safelife.ui.agendamento.profissional.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.safelife.viewModel.agendamento.profissional.AgendaProfissionalViewModel
/**
 * Componente responsável por exibir e gerenciar a seleção de horários para um dia específico.
 *
 * Permite ao profissional escolher horários disponíveis que serão salvos posteriormente.
 * Cada botão representa um horário. Ao clicar, ele é marcado ou desmarcado.
 *
 * @param diaSelecionado Dia da semana selecionado (ex: "Seg", "Ter").
 * @param horarios Lista de horários possíveis para exibir (ex: "08:00", "09:00", etc).
 * @param horariosSelecionados Conjunto de horários já marcados para esse dia.
 * @param onChange Callback chamado sempre que a seleção for alterada.
 * @param viewModel ViewModel usado para acessar ou salvar disponibilidade.
 * @param edicoesPendentes Estado compartilhado que armazena horários editados antes de persistir.
 */
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
        // Divide os horários em grupos de 4 para exibir em linhas organizadas
        horarios.chunked(4).forEach { linha ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                linha.forEach { hora ->
                    // Verifica se o horário está selecionado
                    val selecionado = hora in horariosSelecionados

                    OutlinedButton(
                        onClick = {
                            // Atualiza o conjunto de horários com base no clique
                            val novoSet = if (selecionado)
                                horariosSelecionados - hora
                            else
                                horariosSelecionados + hora

                            // Dispara a atualização visual do conjunto selecionado
                            onChange(novoSet)

                            // Armazena temporariamente a edição no mapa de alterações
                            val atual = edicoesPendentes.value.toMutableMap()
                            atual[diaSelecionado] = novoSet
                            edicoesPendentes.value = atual
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selecionado)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            else
                                MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(1f) // Ocupa espaço proporcional dentro da linha
                    ) {
                        Text(hora) // Exibe o texto do horário no botão
                    }
                }
            }
        }
    }
}
