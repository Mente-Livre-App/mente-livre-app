package com.example.safelife.repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
class AgendamentoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    suspend fun getProfissionaisComAgenda(): List<Profissional> {
        val snapshot = db.collection("disponibilidade").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val nome = doc.getString("nome") ?: return@mapNotNull null
            val uid = doc.id
            Profissional(uid, nome)
        }
    }
    suspend fun getHorariosDisponiveis(profissionalId: String): Map<String, List<String>> {
        val doc = db.collection("disponibilidade").document(profissionalId).get().await()
        @Suppress("UNCHECKED_CAST")
        return doc.get("horarios") as? Map<String, List<String>> ?: emptyMap()
    }
    fun agendarConsulta(
        profissionalId: String,
        dia: String,
        horario: String,
        nome: String,
        email: String,
        telefone: String
    ) {
        val pacienteId = auth.currentUser?.uid ?: return
        val agendamento = hashMapOf(
            "profissionalId" to profissionalId,
            "pacienteId" to pacienteId,
            "nomePaciente" to nome,
            "emailPaciente" to email,
            "telefonePaciente" to telefone,
            "data" to dia,
            "horario" to horario,
            "status" to "confirmado"
        )
        db.collection("agendamentos").add(agendamento)
    }
}
data class Profissional(
    val uid: String,
    val nome: String
)