package com.example.safelife.viewModel.chat.profissional

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory para criar uma instância de ChatProfissionalViewModel com os parâmetros necessários.
 */
class ChatProfissionalViewModelFactory(
    private val profissionalId: String,
    private val pacienteId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatProfissionalViewModel::class.java)) {
            return ChatProfissionalViewModel(profissionalId, pacienteId) as T
        }
        throw IllegalArgumentException("Classe desconhecida para ViewModel: ${modelClass.name}")
    }
}
