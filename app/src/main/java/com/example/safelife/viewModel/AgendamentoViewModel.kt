package com.example.safelife.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AgendamentoViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun agendarConsulta(
        pacienteId: String,
        profissionalId: String,
        data: String,
        horario: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val agendamento = hashMapOf(
            "pacienteId" to pacienteId,
            "profissionalId" to profissionalId,
            "data" to data,
            "horario" to horario,
            "status" to "pendente"
        )

        _isLoading.value = true
        viewModelScope.launch {
            db.collection("agendamentos")
                .add(agendamento)
                .addOnSuccessListener {
                    _isLoading.value = false
                    onSuccess()
                }
                .addOnFailureListener {
                    _isLoading.value = false
                    onFailure(it.message ?: "Erro ao agendar")
                }
        }
    }
}