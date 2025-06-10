package com.example.safelife.viewModel.chat.paciente

import com.example.safelife.model.Message
import com.example.safelife.repository.ChatRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
@OptIn(ExperimentalCoroutinesApi::class)
class ChatPacienteViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var chatRepository: ChatRepository

    private val currentUserId = "paciente123"
    private val otherUserId = "profissional456"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        chatRepository = mockk(relaxed = true)

        // Mock para evitar exceção no collect
        coEvery { chatRepository.getOrCreateChatId(currentUserId, otherUserId) } returns "chatABC"
        coEvery { chatRepository.observeMessages("chatABC") } returns flow {
            emit(emptyList()) // evita erro no init
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `estado inicial deve conter lista vazia de mensagens`() = runTest {
        val viewModel = ChatPacienteViewModel(currentUserId, otherUserId, chatRepository)
        advanceUntilIdle()
        assertTrue(viewModel.messages.isEmpty())
    }

    @Test
    fun `setupChat deve popular mensagens corretamente`() = runTest {
        val mensagensMock = listOf(
            Message("paciente123", "profissional456", "Oi", null, false)
        )

        // Simula fluxo com mensagens
        coEvery { chatRepository.observeMessages("chatABC") } returns flow {
            emit(mensagensMock)
        }

        val viewModel = ChatPacienteViewModel(currentUserId, otherUserId, chatRepository)
        advanceUntilIdle()

        assertEquals(1, viewModel.messages.size)
        assertEquals("Oi", viewModel.messages.first().text)
    }
}
