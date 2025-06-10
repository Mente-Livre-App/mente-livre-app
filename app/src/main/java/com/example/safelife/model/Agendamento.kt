package com.example.safelife.model

data class Agendamento(
    val id: String = "",
    val pacienteId: String = "",
    val profissionalId: String = "",
    val nomePaciente: String = "",
    val emailPaciente: String = "",
    val telefonePaciente: String = "",
    val data: String = "",
    val horario: String = "",
    val status: String = "pendente"
)
