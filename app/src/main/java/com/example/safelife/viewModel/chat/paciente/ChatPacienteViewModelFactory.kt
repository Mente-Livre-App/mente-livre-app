package com.example.safelife.viewModel.chat.paciente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Fábrica para instanciar o ChatViewModel com parâmetros personalizados.
 * Necessária porque o ViewModel padrão não aceita parâmetros diretamente.
 */
class ChatPacienteViewModelFactory(
    private val currentUserId: String,
    private val otherUserId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == ChatPacienteViewModel::class.java) {
            return ChatPacienteViewModel(currentUserId, otherUserId) as T
        }
        throw IllegalArgumentException("Classe desconhecida para ViewModel")
    }
}
