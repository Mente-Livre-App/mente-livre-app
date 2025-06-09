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
    private val _disponibilidade = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    val disponibilidade: StateFlow<Map<String, Set<String>>> = _disponibilidade
    fun salvarHorarios(dia: String, horarios: Set<String>) {
        val atual = _disponibilidade.value.toMutableMap()
        atual[dia] = horarios
        _disponibilidade.value = atual
    }
    fun obterHorariosParaDia(dia: String): Set<String> {
        return _disponibilidade.value[dia] ?: emptySet()
    }
    private val agendamentosFicticios = listOf(
        Pair("Seg", "10:00"),
        Pair("Ter", "13:00")
    )
    fun podeRemover(dia: String, horario: String): Boolean {
        return !agendamentosFicticios.contains(Pair(dia, horario))
    }
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
                Log.d("Firestore", "Horarios atualizados no Firestore")
            }
    }
    .addOnFailureListener {
        Log.e("Firestore", "Erro ao atualizar horarios", it)
    }
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
                Log.e("Firestore", "Erro ao buscar nome do usuario", it)
            }
    }
}