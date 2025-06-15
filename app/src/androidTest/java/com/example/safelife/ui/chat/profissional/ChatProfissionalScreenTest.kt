import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.safelife.model.Message
import com.example.safelife.ui.chat.profissional.ChatProfissionalScreen
import com.example.safelife.viewModel.chat.profissional.ChatProfissionalViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class ChatProfissionalScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<ChatProfissionalViewModel>(relaxed = true)

    @Test
    fun exibeTituloConversaComPaciente() {
        every { mockViewModel.mensagens } returns MutableStateFlow(emptyList())

        composeTestRule.setContent {
            ChatProfissionalScreen(
                profissionalId = "prof1",
                pacienteId = "pac1",
                navController = TODO(),
                agendamentoId = TODO(),
                userType = TODO()
            )
        }

        composeTestRule.onNodeWithText("Conversa com paciente").assertIsDisplayed()
    }

    @Test
    fun campoTextoInicialmenteVazio() {
        every { mockViewModel.mensagens } returns MutableStateFlow(emptyList())

        composeTestRule.setContent {
            ChatProfissionalScreen(
                profissionalId = "prof1",
                pacienteId = "pac1",
                navController = TODO(),
                agendamentoId = TODO(),
                userType = TODO()
            )
        }

        composeTestRule.onNode(hasText("Digite uma mensagem...")).assertExists()
    }

    @Test
    fun botaoEnviarEhExibido() {
        every { mockViewModel.mensagens } returns MutableStateFlow(emptyList())

        composeTestRule.setContent {
            ChatProfissionalScreen(
                profissionalId = "prof1",
                pacienteId = "pac1",
                navController = TODO(),
                agendamentoId = TODO(),
                userType = TODO()
            )
        }

        composeTestRule.onNodeWithText("Enviar").assertIsDisplayed()
    }

}
