import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.safelife.model.Comment
import com.example.safelife.model.Post
import com.example.safelife.ui.feed.PostDetailScreen
import com.example.safelife.viewModel.PostDetailViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class PostDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<PostDetailViewModel>(relaxed = true)

    @Test
    fun exibePostagemCorretamente() {
        val post = Post(id = "1", authorName = "Autor", content = "Conteudo", likeCount = 0)

        every { mockViewModel.post } returns MutableStateFlow(post)
        every { mockViewModel.comments } returns MutableStateFlow(emptyList())
        every { mockViewModel.isSending } returns MutableStateFlow(false)

        composeTestRule.setContent {
            PostDetailScreen(postId = "1", viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Autor").assertIsDisplayed()
        composeTestRule.onNodeWithText("Conteudo").assertIsDisplayed()
    }

    @Test
    fun exibeComentariosCorretamente() {
        val comment = Comment(id = "c1", authorName = "Joao", text = "Muito bom!", timestamp = 0L)

        every { mockViewModel.post } returns MutableStateFlow(null)
        every { mockViewModel.comments } returns MutableStateFlow(listOf(comment))
        every { mockViewModel.isSending } returns MutableStateFlow(false)

        composeTestRule.setContent {
            PostDetailScreen(postId = "1", viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Joao").assertIsDisplayed()
        composeTestRule.onNodeWithText("Muito bom!").assertIsDisplayed()
    }

    @Test
    fun botaoEnviarDesabilitadoComComentarioVazio() {
        every { mockViewModel.post } returns MutableStateFlow(null)
        every { mockViewModel.comments } returns MutableStateFlow(emptyList())
        every { mockViewModel.isSending } returns MutableStateFlow(false)

        composeTestRule.setContent {
            PostDetailScreen(postId = "1", viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Enviar").assertIsNotEnabled()
    }
}
