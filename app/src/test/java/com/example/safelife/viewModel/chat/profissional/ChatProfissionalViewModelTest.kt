package com.example.safelife.viewModel.chat.profissional

import com.example.safelife.model.Message
import com.example.safelife.repository.ChatRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatProfissionalViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var chatRepository: ChatRepository
    private lateinit var viewModel: ChatProfissionalViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        chatRepository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `iniciarOuCarregarChat deve popular mensagens corretamente`() = runTest {
        val mensagensMock = listOf(
            Message("1", "2", "Oi", null, false),
            Message("2", "1", "Olá", null, false)
        )

        coEvery { chatRepository.getOrCreateChatId("prof123", "pac456") } returns "chat789"
        every { chatRepository.observeMessages("chat789") } returns flowOf(mensagensMock)

        viewModel = ChatProfissionalViewModel("prof123", "pac456", chatRepository)

        advanceUntilIdle()

        val mensagens = viewModel.mensagens.value
        assertEquals(2, mensagens.size)
        assertEquals("Oi", mensagens[0].text)
    }

    @Test
    fun `enviarMensagem deve chamar sendMessage corretamente`() = runTest {
        coEvery { chatRepository.getOrCreateChatId(any(), any()) } returns "chat123"
        every { chatRepository.observeMessages("chat123") } returns flowOf(emptyList())

        viewModel = ChatProfissionalViewModel("prof1", "pac1", chatRepository)

        viewModel.enviarMensagem("Olá paciente")

        coVerify {
            chatRepository.sendMessage("chat123", withArg {
                assertEquals("prof1", it.senderId)
                assertEquals("pac1", it.receiverId)
                assertEquals("Olá paciente", it.text)
            })
        }
    }

    @Test
    fun `enviarMensagem nao deve chamar sendMessage se texto for vazio`() = runTest {
        coEvery { chatRepository.getOrCreateChatId(any(), any()) } returns "chat123"
        every { chatRepository.observeMessages("chat123") } returns flowOf(emptyList())

        viewModel = ChatProfissionalViewModel("prof1", "pac1", chatRepository)

        viewModel.enviarMensagem("   ")

        coVerify(exactly = 0) { chatRepository.sendMessage(any(), any()) }
    }
    @Test
    fun `enviarMensagem nao deve chamar sendMessage se chatId estiver vazio`() = runTest {
        // Chat ID ainda não atribuído
        coEvery { chatRepository.getOrCreateChatId(any(), any()) } returns ""
        every { chatRepository.observeMessages(any()) } returns flowOf(emptyList())

        viewModel = ChatProfissionalViewModel("profX", "pacY", chatRepository)

        viewModel.enviarMensagem("Mensagem válida")

        coVerify(exactly = 0) { chatRepository.sendMessage(any(), any()) }
    }
    @Test
    fun `iniciarOuCarregarChat deve iniciar com lista vazia se sem mensagens`() = runTest {
        coEvery { chatRepository.getOrCreateChatId(any(), any()) } returns "chat999"
        every { chatRepository.observeMessages("chat999") } returns flowOf(emptyList())

        viewModel = ChatProfissionalViewModel("profA", "pacB", chatRepository)

        advanceUntilIdle()

        assertTrue(viewModel.mensagens.value.isEmpty())
    }

}
