package com.example.safelife.viewModel.agendamento.profissional

import androidx.lifecycle.ViewModel
import com.example.safelife.model.Agendamento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AgendamentosConfirmadosViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _agendamentos = MutableStateFlow<List<Agendamento>>(emptyList())
    val agendamentos: StateFlow<List<Agendamento>> = _agendamentos

    private val _dadosPacientes = MutableStateFlow<Map<String, PacienteInfo>>(emptyMap())
    val dadosPacientes: StateFlow<Map<String, PacienteInfo>> = _dadosPacientes

    private var listenerRegistration: ListenerRegistration? = null

    init {
        carregarAgendamentosConfirmados()
    }

    private fun carregarAgendamentosConfirmados() {
        val profissionalId = auth.currentUser?.uid ?: return

        listenerRegistration?.remove()

        listenerRegistration = db.collection("agendamentos")
            .whereEqualTo("profissionalId", profissionalId)
            .whereEqualTo("status", "confirmado")
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) return@addSnapshotListener

                val lista = snapshots.documents
                    .distinctBy { it.id }
                    .mapNotNull { doc ->
                        val id = doc.id
                        val pacienteId = doc.getString("pacienteId") ?: return@mapNotNull null
                        val profissionalIdDoc = doc.getString("profissionalId") ?: ""
                        val data = doc.getString("data") ?: ""
                        val horario = doc.getString("horario") ?: ""
                        val status = doc.getString("status") ?: ""

                        // Lança carregamento do paciente
                        carregarDadosPaciente(pacienteId)

                        Agendamento(
                            id = id,
                            pacienteId = pacienteId,
                            profissionalId = profissionalIdDoc,
                            data = data,
                            horario = horario,
                            status = status
                        )
                    }

                _agendamentos.value = lista
            }
    }

    private fun carregarDadosPaciente(pacienteId: String) {
        // Verifica se já está no cache para evitar requisições duplicadas
        if (_dadosPacientes.value.containsKey(pacienteId)) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot = db.collection("usuarios").document(pacienteId).get().await()
                val nome = snapshot.getString("name") ?: ""
                val email = snapshot.getString("email") ?: ""
                val telefone = snapshot.getString("telefone") ?: ""

                val pacienteInfo = PacienteInfo(nome, email, telefone)

                _dadosPacientes.value = _dadosPacientes.value.toMutableMap().apply {
                    put(pacienteId, pacienteInfo)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    data class PacienteInfo(
        val nome: String,
        val email: String,
        val telefone: String
    )
}
