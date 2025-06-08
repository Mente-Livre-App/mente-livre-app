package com.example.safelife.viewModel.agendamento.profissional
data class Agendamento(
    val agendamentoId: String = "", // Adicionado para vincular o ID do documento
    val pacienteId: String = "",
    val nomePaciente: String = "",
    val emailPaciente: String = "",
    val telefonePaciente: String = "",
    val data: String = "",
    val horario: String = "",
    val status: String
)