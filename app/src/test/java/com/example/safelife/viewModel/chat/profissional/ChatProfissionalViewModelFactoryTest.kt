package com.example.safelife.viewModel.chat.profissional

import androidx.lifecycle.ViewModel
import org.junit.Assert.*
import org.junit.Test

class ChatProfissionalViewModelFactoryTest {



    @Test(expected = IllegalArgumentException::class)
    fun `create deve lançar excecao para classe desconhecida`() {
        val factory = ChatProfissionalViewModelFactory("prof123", "pac456")

        // Classe fictícia só para forçar erro
        class OutraViewModel : ViewModel()

        factory.create(OutraViewModel::class.java)
    }
}
