package com.example.safelife.viewModel.chat.paciente

import androidx.lifecycle.ViewModel
import org.junit.Assert.*
import org.junit.Test

class ChatPacienteViewModelFactoryTest {

    private val currentUserId = "paciente123"
    private val otherUserId = "profissional456"



    @Test(expected = IllegalArgumentException::class)
    fun `create deve lançar excecao para classe desconhecida`() {
        val factory = ChatPacienteViewModelFactory(currentUserId, otherUserId)
        factory.create(OutroViewModel::class.java)
    }


    // Classe fake para teste da exceção
    class OutroViewModel : ViewModel()
}
