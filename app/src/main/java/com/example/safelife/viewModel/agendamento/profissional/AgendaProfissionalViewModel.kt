package com.example.safelife.viewModel.agendamento.profissional

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AgendaProfissionalViewModel : ViewModel() {

    // Armazena disponibilidade: dia ("Seg") -> horários (ex: "08:00", "09:00")
    private val _disponibilidade = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    val disponibilidade: StateFlow<Map<String, Set<String>>> = _disponibilidade

    // Simula o salvamento de horários
    fun salvarHorarios(dia: String, horarios: Set<String>) {
        val atual = _disponibilidade.value.toMutableMap()
        atual[dia] = horarios
        _disponibilidade.value = atual
    }

    fun obterHorariosParaDia(dia: String): Set<String> {
        return _disponibilidade.value[dia] ?: emptySet()
    }

    // Simula agendamentos já existentes
    private val agendamentosFicticios = listOf(
        Pair("Seg", "10:00"),
        Pair("Ter", "13:00")
    )

    // Verifica se um horário pode ser removido
    fun podeRemover(dia: String, horario: String): Boolean {
        return !agendamentosFicticios.contains(Pair(dia, horario))
    }

    // Atualiza apenas o campo de horários do profissional
    fun atualizarHorariosNoFirestore(horariosPorDia: Map<String, Set<String>>) {
        val user = Firebase.auth.currentUser ?: return
        val profissionalId = user.uid

        val dadosHorarios = mapOf(
            "horarios" to horariosPorDia.mapValues { it.value.toList() }
        )

        Firebase.firestore.collection("disponibilidade")
            .document(profissionalId)
            .update(dadosHorarios)
            .addOnSuccessListener {
                Log.d("Firestore", "Horários atualizados no Firestore")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Erro ao atualizar horários", it)
            }
    }


    // Salva apenas uma vez os dados fixos do profissional (nome, email, tipo)
    fun salvarDadosProfissionalNoFirestore() {
        val user = Firebase.auth.currentUser ?: return
        val profissionalId = user.uid

        Firebase.firestore.collection("usuarios").document(profissionalId).get()
            .addOnSuccessListener { doc ->
                val nome = doc.getString("name") ?: "Profissional"
                val email = doc.getString("email") ?: ""
                val userType = doc.getString("userType") ?: "profissional"

                val dadosFixos = mapOf(
                    "nome" to nome,
                    "email" to email,
                    "userType" to userType
                )

                Firebase.firestore.collection("disponibilidade")
                    .document(profissionalId)
                    .set(dadosFixos, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("Firestore", "Dados fixos do profissional salvos com sucesso")
                    }
                    .addOnFailureListener {
                        Log.e("Firestore", "Erro ao salvar dados fixos", it)
                    }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Erro ao buscar nome do usuário", it)
            }
    }

}