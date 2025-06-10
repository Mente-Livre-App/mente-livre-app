import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.safelife.MainActivity
import com.example.safelife.model.Post
import com.example.safelife.ui.feed.FeedScreen
import com.example.safelife.viewModel.FeedViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FeedScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<FeedViewModel>(relaxed = true)

    @Test
    fun exibeMensagemQuandoNaoHaPostagens() {
        every { mockViewModel.posts } returns MutableStateFlow(emptyList())
        every { mockViewModel.userType } returns MutableStateFlow("profissional")

        composeTestRule.setContent {
            FeedScreen(viewModel = mockViewModel, navigateToNovaPostagem = {})
        }

        composeTestRule.onNodeWithText("Nenhuma publicação ainda.").assertIsDisplayed()
    }

    @Test
    fun exibeBotaoNovaPostagemParaProfissional() {
        every { mockViewModel.posts } returns MutableStateFlow(emptyList())
        every { mockViewModel.userType } returns MutableStateFlow("profissional")

        composeTestRule.setContent {
            FeedScreen(viewModel = mockViewModel, navigateToNovaPostagem = {})
        }

        composeTestRule.onNodeWithContentDescription("Nova Postagem").assertIsDisplayed()
    }

    @Test
    fun naoExibeBotaoNovaPostagemParaPaciente() {
        every { mockViewModel.posts } returns MutableStateFlow(emptyList())
        every { mockViewModel.userType } returns MutableStateFlow("paciente")

        composeTestRule.setContent {
            FeedScreen(viewModel = mockViewModel, navigateToNovaPostagem = {})
        }

        composeTestRule.onNodeWithContentDescription("Nova Postagem").assertDoesNotExist()
    }
}
