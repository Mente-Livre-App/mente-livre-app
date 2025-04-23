package com.example.safelife.viewModel.chat.profissional

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelife.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListaPacientesViewModel(
    private val profissionalId: String
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _pacientes = MutableStateFlow<List<Usuario>>(emptyList())
    val pacientes: StateFlow<List<Usuario>> = _pacientes

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun buscarPacientesComConversa() {
        _isLoading.value = true

        viewModelScope.launch {
            firestore.collection("chats")
                .whereArrayContains("participants", profissionalId)
                .get()
                .addOnSuccessListener { result ->
                    val listaTemp = mutableListOf<Usuario>()

                    result.documents.forEach { doc ->
                        val participantes = doc.get("participants") as? List<*>
                        val pacienteId = participantes?.firstOrNull { it != profissionalId }?.toString()

                        pacienteId?.let {
                            firestore.collection("usuarios").document(it).get()
                                .addOnSuccessListener { userDoc ->
                                    val paciente = userDoc.toObject(Usuario::class.java)
                                    paciente?.let { listaTemp.add(it) }
                                    _pacientes.value = listaTemp
                                }
                        }
                    }
                    _isLoading.value = false
                }
                .addOnFailureListener { e ->
                    Log.e("ListaPacientesVM", "Erro ao buscar pacientes: $e")
                    _isLoading.value = false
                }
        }
    }
}
