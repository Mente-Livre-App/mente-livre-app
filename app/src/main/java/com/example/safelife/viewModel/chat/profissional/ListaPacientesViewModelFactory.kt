package com.example.safelife.viewModel.chat.profissional

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ListaPacientesViewModelFactory(
    private val profissionalId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListaPacientesViewModel::class.java)) {
            return ListaPacientesViewModel(profissionalId) as T
        }
        throw IllegalArgumentException("Classe desconhecida para ViewModel: ${modelClass.name}")
    }
}
