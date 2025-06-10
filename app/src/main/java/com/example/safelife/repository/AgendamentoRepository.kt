package com.example.safelife.repository

import com.example.safelife.model.Profissional
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AgendamentoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getProfissionaisComAgenda(): List<Profissional> {
        val snapshot = db.collection("disponibilidade").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val name = doc.getString("nome") ?: return@mapNotNull null
            val email = doc.getString("email") ?: ""
            val crp = "" // não está armazenado aqui
            val userType = doc.getString("userType") ?: ""
            val uid = doc.id
            Profissional(
                uid = uid,
                name = name,
                email = email,
                crp = crp,
                userType = userType
            )
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
        telefone: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val pacienteId = auth.currentUser?.uid ?: return onFailure("Usuário não autenticado")

        val dbRef = db.collection("agendamentos")
        dbRef
            .whereEqualTo("profissionalId", profissionalId)
            .whereEqualTo("pacienteId", pacienteId)
            .whereEqualTo("data", dia)
            .whereEqualTo("horario", horario)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    onFailure("Você já possui um agendamento nesse horário.")
                    return@addOnSuccessListener
                }

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

                dbRef.add(agendamento)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onFailure(e.message ?: "Erro desconhecido") }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Erro ao verificar disponibilidade")
            }
    }


}
