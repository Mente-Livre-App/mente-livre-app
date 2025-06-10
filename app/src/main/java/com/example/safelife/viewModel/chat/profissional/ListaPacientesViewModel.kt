package com.example.safelife.viewModel.chat.profissional

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelife.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel responsável por buscar os pacientes com quem o profissional possui conversas.
 *
 * @param profissionalId ID do profissional autenticado.
 * @param firestore Instância do FirebaseFirestore (injetável para facilitar testes).
 */
class ListaPacientesViewModel(
    private val profissionalId: String,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _pacientes = MutableStateFlow<List<Usuario>>(emptyList())
    val pacientes: StateFlow<List<Usuario>> = _pacientes

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Busca todos os pacientes com quem o profissional teve conversas registradas em "chats".
     */
    fun buscarPacientesComConversa() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val chatsSnapshot = firestore.collection("chats")
                    .whereArrayContains("participants", profissionalId)
                    .get()
                    .await()

                val listaTemp = mutableListOf<Usuario>()

                chatsSnapshot.documents.forEach { doc ->
                    val participantes = doc.get("participants") as? List<*>
                    val pacienteId = participantes?.firstOrNull { it != profissionalId }?.toString()

                    if (pacienteId != null) {
                        val userDoc =
                            firestore.collection("usuarios").document(pacienteId).get().await()
                        val paciente = userDoc.toObject(Usuario::class.java)
                        if (paciente != null) listaTemp.add(paciente)
                    }
                }

                _pacientes.value = listaTemp
            } catch (e: Exception) {
                Log.e("ListaPacientesVM", "Erro ao buscar pacientes: ${e.message}")
                _pacientes.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
