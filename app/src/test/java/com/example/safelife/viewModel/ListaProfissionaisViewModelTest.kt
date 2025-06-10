package com.example.safelife.viewModel

import com.example.safelife.model.Profissional
import com.example.safelife.repository.ProfissionalRepository
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListaProfissionaisViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ProfissionalRepository
    private lateinit var firestore: FirebaseFirestore
    private lateinit var viewModel: ListaProfissionaisViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        firestore = mockk(relaxed = true)
        viewModel = ListaProfissionaisViewModel(repository = repository, db = firestore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    @Test
    fun `profissionais deve iniciar como lista vazia`() {
        assertTrue(viewModel.profissionais.value.isEmpty())
    }

    @Test
    fun `isLoading deve iniciar como false`() {
        assertFalse(viewModel.isLoading.value)
    }


}
