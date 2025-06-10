package com.example.safelife.viewModel.chat.profissional

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore

class ListaPacientesViewModelFactory(
    private val profissionalId: String,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListaPacientesViewModel::class.java)) {
            return ListaPacientesViewModel(profissionalId, firestore) as T
        }
        throw IllegalArgumentException("Classe desconhecida para ViewModel: ${modelClass.name}")
    }
}

