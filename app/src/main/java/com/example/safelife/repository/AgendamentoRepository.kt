package com.example.safelife.repository

import com.example.safelife.model.Profissional
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repositório responsável por lidar com a lógica de agendamento de consultas no Firestore.
 * Permite buscar profissionais com agenda, consultar horários disponíveis e agendar novas consultas.
 */
class AgendamentoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Recupera a lista de profissionais que possuem horários cadastrados na coleção "disponibilidade".
     *
     * @return Lista de objetos [Profissional] com informações básicas (nome, email, tipo de usuário).
     */
    suspend fun getProfissionaisComAgenda(): List<Profissional> {
        val snapshot = db.collection("disponibilidade").get().await()

        // Mapeia os documentos em objetos Profissional
        return snapshot.documents.mapNotNull { doc ->
            val name = doc.getString("nome") ?: return@mapNotNull null
            val email = doc.getString("email") ?: ""
            val crp = "" // CRP não está disponível nesta coleção
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

    /**
     * Retorna os horários disponíveis para um profissional específico.
     *
     * @param profissionalId ID do profissional.
     * @return Mapa contendo dias da semana como chave e lista de horários como valor.
     */
    suspend fun getHorariosDisponiveis(profissionalId: String): Map<String, List<String>> {
        val doc = db.collection("disponibilidade").document(profissionalId).get().await()

        // Converte o campo "horarios" para um mapa de listas
        @Suppress("UNCHECKED_CAST")
        return doc.get("horarios") as? Map<String, List<String>> ?: emptyMap()
    }

    /**
     * Agenda uma nova consulta no Firestore, validando se já existe agendamento no mesmo horário.
     *
     * @param profissionalId ID do profissional escolhido.
     * @param dia Dia do agendamento (ex: "2025-06-10").
     * @param horario Horário do agendamento (ex: "10:00").
     * @param nome Nome do paciente.
     * @param email Email do paciente.
     * @param telefone Telefone do paciente.
     * @param onSuccess Callback chamado em caso de sucesso.
     * @param onFailure Callback chamado em caso de erro com mensagem.
     */
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
        val pacienteId = auth.currentUser?.uid
            ?: return onFailure("Usuário não autenticado")

        val dbRef = db.collection("agendamentos")

        // Verifica se já existe um agendamento com os mesmos dados
        dbRef
            .whereEqualTo("profissionalId", profissionalId)
            .whereEqualTo("pacienteId", pacienteId)
            .whereEqualTo("data", dia)
            .whereEqualTo("horario", horario)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    // Já existe agendamento nesse horário
                    onFailure("Você já possui um agendamento nesse horário.")
                    return@addOnSuccessListener
                }

                // Cria o novo agendamento
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

                // Salva o agendamento no Firestore
                dbRef.add(agendamento)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e ->
                        onFailure(e.message ?: "Erro desconhecido")
                    }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Erro ao verificar disponibilidade")
            }
    }
}
