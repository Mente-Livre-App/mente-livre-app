package com.example.safelife.viewModel.agendamento.paciente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelife.repository.AgendamentoRepository
import com.example.safelife.model.Profissional
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class AgendamentoPacienteViewModel : ViewModel() {

    private val repository = AgendamentoRepository()

    private val _profissionais = MutableStateFlow<List<Profissional>>(emptyList())
    val profissionais: StateFlow<List<Profissional>> = _profissionais

    private val _horariosDisponiveis = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val horariosDisponiveis: StateFlow<Map<String, List<String>>> = _horariosDisponiveis

    private val _diaSelecionado = MutableStateFlow<LocalDate>(LocalDate.now())
    val diaSelecionado: StateFlow<LocalDate> = _diaSelecionado

    private val _horarioSelecionado = MutableStateFlow<String?>(null)
    val horarioSelecionado: StateFlow<String?> = _horarioSelecionado

    fun carregarProfissionaisDisponiveis() {
        viewModelScope.launch {
            val lista = repository.getProfissionaisComAgenda()
            println(">>> Profissionais carregados: ${lista.size}")
            lista.forEach { println(">>> ${it.name} - ${it.uid}") }
            _profissionais.value = lista
        }
    }


    fun carregarHorariosParaProfissional(profissionalId: String) {
        viewModelScope.launch {
            val horarios = repository.getHorariosDisponiveis(profissionalId)
            println(">>> HORÁRIOS DISPONÍVEIS RECEBIDOS:")
            horarios.forEach { (dia, lista) ->
                println(">>> $dia -> $lista")
            }
            _horariosDisponiveis.value = horarios
        }
    }


    fun agendarConsulta(
        profissionalId: String,
        dia: String,
        horario: String,
        nome: String,
        email: String,
        telefone: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.agendarConsulta(
            profissionalId,
            dia,
            horario,
            nome,
            email,
            telefone,
            onSuccess,
            onFailure
        )
    }


    fun atualizarDiaSelecionado(novoDia: LocalDate) {
        _diaSelecionado.value = novoDia
    }

    fun atualizarHorarioSelecionado(horario: String) {
        _horarioSelecionado.value = horario
    }
}