package com.example.safelife.ui.chat.paciente

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.safelife.model.Message
import com.example.safelife.ui.chat.paciente.ChatScreen
import com.example.safelife.viewModel.chat.paciente.ChatPacienteViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class ChatScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<ChatPacienteViewModel>(relaxed = true)

    @Test
    fun exibeCampoDeTextoEDesabilitaBotaoComTextoVazio() {
        every { mockViewModel.messages } returns emptyList()

        composeTestRule.setContent {
            ChatScreen(
                currentUserId = "user1",
                otherUserId = "user2",
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("Digite uma mensagem...").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Enviar").assertIsNotEnabled()
    }

    @Test
    fun mensagensSaoRenderizadasCorretamente() {
        val mensagens = listOf(
            Message(senderId = "user1", receiverId = "user2", text = "Oi", timestamp = 1L),
            Message(senderId = "user2", receiverId = "user1", text = "Olá", timestamp = 2L)
        )

        every { mockViewModel.messages } returns mensagens

        composeTestRule.setContent {
            ChatScreen(
                currentUserId = "user1",
                otherUserId = "user2",
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("Oi").assertIsDisplayed()
        composeTestRule.onNodeWithText("Olá").assertIsDisplayed()
    }
}
