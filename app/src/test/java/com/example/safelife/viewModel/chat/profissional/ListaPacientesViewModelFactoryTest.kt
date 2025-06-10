package com.example.safelife.viewModel.chat.profissional

import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

class ListaPacientesViewModelFactoryTest {

    @Test
    fun `create deve retornar instancia de ListaPacientesViewModel`() {
        val firestore = mockk<FirebaseFirestore>(relaxed = true)
        val factory = ListaPacientesViewModelFactory("prof123", firestore)
        val viewModel = factory.create(ListaPacientesViewModel::class.java)

        assertTrue(viewModel is ListaPacientesViewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create deve lançar excecao para classe desconhecida`() {
        val firestore = mockk<FirebaseFirestore>(relaxed = true)
        val factory = ListaPacientesViewModelFactory("prof123", firestore)
        factory.create(UnknownViewModel::class.java)
    }

    private class UnknownViewModel : androidx.lifecycle.ViewModel()
}
