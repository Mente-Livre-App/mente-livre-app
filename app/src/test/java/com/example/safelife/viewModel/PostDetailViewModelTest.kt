package com.example.safelife.viewModel

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: PostDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        db = mockk(relaxed = true)
        viewModel = PostDetailViewModel(postId = "post123", db = db)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isSending deve iniciar como false`() {
        assertFalse(viewModel.isSending.value)
    }

    @Test
    fun `post deve iniciar como null`() {
        assertNull(viewModel.post.value)
    }

    @Test
    fun `comments deve iniciar como lista vazia`() {
        assertTrue(viewModel.comments.value.isEmpty())
    }

}
