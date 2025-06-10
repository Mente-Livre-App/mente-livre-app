import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.safelife.model.Usuario
import com.example.safelife.ui.chat.profissional.ListaPacientesScreen
import com.example.safelife.viewModel.chat.profissional.ListaPacientesViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class ListaPacientesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<ListaPacientesViewModel>(relaxed = true)

    @Test
    fun mostraTextoNenhumPacienteQuandoListaVazia() {
        every { mockViewModel.isLoading } returns MutableStateFlow(false)
        every { mockViewModel.pacientes } returns MutableStateFlow(emptyList())

        composeTestRule.setContent {
            val navController = rememberNavController()
            ListaPacientesScreen(
                navController = navController,
                profissionalId = "prof123",
                viewModelOverride = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("Nenhum paciente iniciou conversa ainda.").assertIsDisplayed()
    }

    @Test
    fun mostraListaDePacientesQuandoDisponivel() {
        val pacientes = listOf(
            Usuario(uid = "p1", name = "Paciente 1", email = "p1@email.com"),
            Usuario(uid = "p2", name = "Paciente 2", email = "p2@email.com")
        )

        every { mockViewModel.isLoading } returns MutableStateFlow(false)
        every { mockViewModel.pacientes } returns MutableStateFlow(pacientes)

        composeTestRule.setContent {
            val navController = rememberNavController()
            ListaPacientesScreen(
                navController = navController,
                profissionalId = "prof123",
                viewModelOverride = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("Paciente 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Paciente 2").assertIsDisplayed()
    }
}
