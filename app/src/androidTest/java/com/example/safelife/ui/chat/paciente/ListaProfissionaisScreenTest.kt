import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.ComposeTestRule

import com.example.safelife.model.Profissional
import com.example.safelife.ui.chat.paciente.ListaProfissionaisScreen
import com.example.safelife.viewModel.ListaProfissionaisViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class ListaProfissionaisScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<ListaProfissionaisViewModel>(relaxed = true)

    @Test
    fun exibeMensagemDeCarregamentoQuandoLoading() {
        every { mockViewModel.isLoading } returns MutableStateFlow(true)
        every { mockViewModel.profissionais } returns MutableStateFlow(emptyList())

        composeTestRule.setContent {
            val navController = TestNavHostController(LocalContext.current)
            ListaProfissionaisScreen(
                currentUserId = "user123",
                navigateToChat = { _, _ -> },
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithTag("loading-indicator").assertExists()
    }

    @Test
    fun exibeMensagemDeListaVazia() {
        every { mockViewModel.isLoading } returns MutableStateFlow(false)
        every { mockViewModel.profissionais } returns MutableStateFlow(emptyList())

        composeTestRule.setContent {
            ListaProfissionaisScreen(
                currentUserId = "user123",
                navigateToChat = { _, _ -> },
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("Nenhum profissional encontrado.").assertIsDisplayed()
    }

    @Test
    fun exibeProfissionaisNaTela() {
        val profissionaisMock = listOf(
            Profissional(uid = "p1", name = "Dr. A", email = "a@email.com"),
            Profissional(uid = "p2", name = "Dra. B", email = "b@email.com")
        )


        every { mockViewModel.isLoading } returns MutableStateFlow(false)
        every { mockViewModel.profissionais } returns MutableStateFlow(profissionaisMock)

        composeTestRule.setContent {
            ListaProfissionaisScreen(
                currentUserId = "user123",
                navigateToChat = { _, _ -> },
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("Dr. A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dra. B").assertIsDisplayed()
    }
}
