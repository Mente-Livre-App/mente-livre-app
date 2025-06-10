import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.safelife.ui.feed.NovaPostagemScreen
import com.example.safelife.viewModel.FeedViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class NovaPostagemScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<FeedViewModel>(relaxed = true)

    @Test
    fun exibeCampoDeTextoEBotao() {
        // Mocka o estado de envio como falso
        every { mockViewModel.isSending } returns mutableStateOf(false)

        composeTestRule.setContent {
            NovaPostagemScreen(viewModel = mockViewModel, navigateBack = {})
        }

        composeTestRule.onNodeWithText("Escreva sua mensagem de apoio...").assertIsDisplayed()
        composeTestRule.onNodeWithText("Publicar").assertIsDisplayed()
    }

    @Test
    fun botaoDesabilitadoQuandoTextoVazio() {
        every { mockViewModel.isSending } returns mutableStateOf(false)

        composeTestRule.setContent {
            NovaPostagemScreen(viewModel = mockViewModel, navigateBack = {})
        }

        composeTestRule.onNodeWithText("Publicar").assertIsNotEnabled()
    }
}
