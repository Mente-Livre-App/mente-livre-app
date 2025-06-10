import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.safelife.ui.auth.SignupScreenTestable
import com.example.safelife.viewModel.AuthViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class SignupScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<AuthViewModel>(relaxed = true)

    @Test
    fun exibeCamposDeTextoObrigatorios() {
        composeTestRule.setContent {
            SignupScreenTestable(
                navigateToHome = {},
                viewModel = mockk(relaxed = true) // Correto: nome do parâmetro compatível
            )

        }

        composeTestRule.onNodeWithTag("campoNome").assertIsDisplayed()
        composeTestRule.onNodeWithTag("campoEmail").assertIsDisplayed()
        composeTestRule.onNodeWithTag("campoTelefone").assertIsDisplayed()
        composeTestRule.onNodeWithTag("campoSenha").assertIsDisplayed()
        composeTestRule.onNodeWithTag("campoConfirmarSenha").assertIsDisplayed()
    }

    @Test
    fun botaoConfirmarEhExibido() {
        composeTestRule.setContent {
            SignupScreenTestable(
                navigateToHome = {},
                viewModel  = mockViewModel
            )
        }

        composeTestRule.onNodeWithTag("botaoConfirmar").assertIsDisplayed()
    }

    @Test
    fun alternaEntrePacienteEProfissional() {
        composeTestRule.setContent {
            SignupScreenTestable(
                navigateToHome = {},
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithTag("radioProfissional").performClick()
        composeTestRule.onNodeWithTag("campoCRP").assertIsDisplayed()

        composeTestRule.onNodeWithTag("radioPaciente").performClick()
        composeTestRule.onNodeWithTag("campoCRP").assertDoesNotExist()
    }
}
