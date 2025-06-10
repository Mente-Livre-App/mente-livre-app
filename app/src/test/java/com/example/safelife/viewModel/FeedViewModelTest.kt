package com.example.safelife.viewModel

import com.example.safelife.model.Comment
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: FeedViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        db = mockk(relaxed = true)
        auth = mockk(relaxed = true)
        viewModel = FeedViewModel(db = db, auth = auth)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `enviarPost deve chamar onFailure quando add falhar`() = runTest {
        val postsCollection = mockk<CollectionReference>(relaxed = true)
        val task = mockk<Task<DocumentReference>>(relaxed = true)

        every { db.collection("posts") } returns postsCollection
        every { postsCollection.add(any()) } returns task

        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(any()) } answers {
            val listener = arg<com.google.android.gms.tasks.OnFailureListener>(0)
            listener.onFailure(RuntimeException("Falha simulada"))
            task
        }

        var sucesso = false
        var erro: String? = null

        viewModel.enviarPost(
            nomeAutor = "Gabriel",
            conteudo = "Post com erro",
            onSuccess = { sucesso = true },
            onFailure = { erro = it }
        )

        advanceUntilIdle()

        assertFalse("onSuccess não deve ser chamado", sucesso)
        assertEquals("Falha simulada", erro)
    }

    @Test
    fun `buscarTipoUsuario deve atualizar userType quando sucesso`() = runTest {
        val documentRef = mockk<DocumentReference>()
        val task = mockk<Task<com.google.firebase.firestore.DocumentSnapshot>>(relaxed = true)

        every { db.collection("usuarios") } returns mockk {
            every { document("uid123") } returns documentRef
        }

        every { documentRef.get() } returns task

        every { task.addOnSuccessListener(any()) } answers {
            val listener =
                arg<com.google.android.gms.tasks.OnSuccessListener<com.google.firebase.firestore.DocumentSnapshot>>(
                    0
                )
            val snapshot = mockk<com.google.firebase.firestore.DocumentSnapshot>()
            every { snapshot.getString("userType") } returns "paciente"
            listener.onSuccess(snapshot)
            task
        }

        every { task.addOnFailureListener(any()) } returns task

        viewModel.buscarTipoUsuario("uid123")

        advanceUntilIdle()
        assertEquals("paciente", viewModel.userType.value)
    }

    @Test
    fun `buscarTipoUsuario deve definir userType como vazio em caso de erro`() = runTest {
        val documentRef = mockk<DocumentReference>()
        val task = mockk<Task<com.google.firebase.firestore.DocumentSnapshot>>(relaxed = true)

        every { db.collection("usuarios") } returns mockk {
            every { document("uid123") } returns documentRef
        }

        every { documentRef.get() } returns task

        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(any()) } answers {
            val listener = arg<com.google.android.gms.tasks.OnFailureListener>(0)
            listener.onFailure(Exception("Simulated error"))
            task
        }

        viewModel.buscarTipoUsuario("uid123")

        advanceUntilIdle()
        assertEquals("", viewModel.userType.value)
    }

    @Test
    fun `atualizarLike deve incrementar o likeCount corretamente quando isLiked for true`() {
        val postId = "post123"
        val currentLikes = 5
        val documentRef = mockk<DocumentReference>(relaxed = true)

        every { db.collection("posts") } returns mockk {
            every { document(postId) } returns documentRef
        }

        viewModel.atualizarLike(postId, currentLikes, isLiked = true)

        verify {
            documentRef.update("likeCount", 6)
        }
    }

    @Test
    fun `atualizarLike deve decrementar o likeCount corretamente quando isLiked for false`() {
        val postId = "post123"
        val currentLikes = 5
        val documentRef = mockk<DocumentReference>(relaxed = true)

        every { db.collection("posts") } returns mockk {
            every { document(postId) } returns documentRef
        }

        viewModel.atualizarLike(postId, currentLikes, isLiked = false)

        verify {
            documentRef.update("likeCount", 4)
        }
    }

    @Test
    fun `atualizarLike deve manter likeCount em zero se currentLikes for zero e isLiked for false`() {
        val postId = "post123"
        val currentLikes = 0
        val documentRef = mockk<DocumentReference>(relaxed = true)

        every { db.collection("posts") } returns mockk {
            every { document(postId) } returns documentRef
        }

        viewModel.atualizarLike(postId, currentLikes, isLiked = false)

        verify {
            documentRef.update("likeCount", 0)
        }
    }

    @Test
    fun `getCommentsForPost deve retornar StateFlow vazio inicialmente`() = runTest {
        val postId = "post123"

        val commentsFlow = viewModel.getCommentsForPost(postId)

        assertNotNull("Deve retornar um StateFlow", commentsFlow)
        assertTrue("A lista de comentários deve estar vazia", commentsFlow.value.isEmpty())
    }

    @Test
    fun `isSending deve ser true durante o envio e false apos falha`() = runTest {
        val postsCollection = mockk<CollectionReference>(relaxed = true)
        val task = mockk<Task<DocumentReference>>(relaxed = true)

        every { db.collection("posts") } returns postsCollection
        every { postsCollection.add(any()) } returns task

        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(any()) } answers {
            val listener = arg<com.google.android.gms.tasks.OnFailureListener>(0)
            listener.onFailure(RuntimeException("Erro simulado"))
            task
        }

        var erro: String? = null

        viewModel.enviarPost(
            nomeAutor = "Gabriel",
            conteudo = "Teste",
            onSuccess = {},
            onFailure = { erro = it }
        )

        // Aguarda a inicialização da corrotina e execução da linha isSending = true
        advanceUntilIdle()

        // Primeiro assert: já deve estar false porque addOnFailureListener é executado logo após a chamada
        assertFalse("isSending deveria ser false após falha", viewModel.isSending.value)
        assertEquals("Erro simulado", erro)
    }

    @Test
    fun `enviarPost deve chamar onSuccess quando add for bem-sucedido`() = runTest {
        val postsCollection = mockk<CollectionReference>(relaxed = true)
        val task = mockk<Task<DocumentReference>>(relaxed = true)

        every { db.collection("posts") } returns postsCollection
        every { postsCollection.add(any()) } returns task

        // Simula sucesso no Firebase
        every { task.addOnSuccessListener(any()) } answers {
            val listener = arg<OnSuccessListener<DocumentReference>>(0)
            listener.onSuccess(mockk())
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        var sucesso = false
        var erro: String? = null

        viewModel.enviarPost(
            nomeAutor = "Gabriel",
            conteudo = "Post de sucesso",
            onSuccess = { sucesso = true },
            onFailure = { erro = it }
        )

        advanceUntilIdle()

        assertTrue("onSuccess deveria ter sido chamado", sucesso)
        assertNull("onFailure não deve ser chamado", erro)
    }

    @Test
    fun `enviarComentario deve chamar onFailure quando ocorrer excecao`() = runTest {
        val postId = "post123"
        val commentsCollection = mockk<CollectionReference>(relaxed = true)
        val documentRef = mockk<DocumentReference>(relaxed = true)

        every { auth.currentUser } returns mockk {
            every { displayName } returns "Gabriel"
        }

        every { db.collection("posts") } returns mockk {
            every { document(postId) } returns documentRef
        }
        every { documentRef.collection("comments") } returns commentsCollection
        coEvery { commentsCollection.add(any()) } throws RuntimeException("Erro ao adicionar comentário")

        var erro: String? = null

        viewModel.enviarComentario(
            postId = postId,
            texto = "Comentário teste",
            onSuccess = {},
            onFailure = { erro = it }
        )

        advanceUntilIdle()

        assertEquals("Erro ao adicionar comentário", erro)
    }
}