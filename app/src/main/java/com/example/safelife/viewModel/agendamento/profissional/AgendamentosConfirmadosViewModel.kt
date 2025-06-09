package com.example.safelife.viewModel.agendamento.profissional
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.auth.FirebaseAuth
class AgendamentosConfirmadosViewModel : ViewModel() {
    private val _agendamentos = MutableStateFlow<List<Agendamento>>(emptyList())
    val agendamentos: StateFlow<List<Agendamento>> = _agendamentos
    init {
        carregarAgendamentosConfirmados()
    }
    private fun carregarAgendamentosConfirmados() {
        val db = FirebaseFirestore.getInstance()
        val profissionalId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("agendamentos")
            .whereEqualTo("profissionalId", profissionalId)
            .whereEqualTo("status", "confirmado")
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) return@addSnapshotListener
                val lista = snapshots.documents.mapNotNull { doc: DocumentSnapshot ->
                    val data = doc.data ?: return@mapNotNull null
                    Agendamento(
                        agendamentoId = doc.id,
                        nomePaciente = data["nomePaciente"] as? String ?: "",
                        emailPaciente = data["emailPaciente"] as? String ?: "",
                        telefonePaciente = data["telefonePaciente"] as? String ?: "",
                        pacienteId = data["pacienteId"] as? String ?: "",
                        data = data["data"] as? String ?: "",
                        horario = data["horario"] as? String ?: "",
                        status = data["status"] as? String ?: ""
                    )
                }
                _agendamentos.value = lista
            }
    }
}