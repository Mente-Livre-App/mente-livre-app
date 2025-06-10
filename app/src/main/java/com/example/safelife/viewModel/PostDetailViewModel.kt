package com.example.safelife.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelife.model.Comment
import com.example.safelife.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel responsável por lidar com a lógica da tela de detalhes de um post específico.
 *
 * @param postId ID do post a ser exibido.
 * @param db Instância do FirebaseFirestore, injetável para facilitar testes.
 * @param dispatcher Dispatcher de corrotina usado para testes ou produção.
 */
class PostDetailViewModel(
    private val postId: String,
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val dispatcher: CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Main
) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending

    init {
        carregarPost()
        carregarComentarios()
    }

    /**
     * Carrega o post específico pelo ID.
     */
    private fun carregarPost() {
        viewModelScope.launch(dispatcher) {
            try {
                val snapshot = db.collection("posts")
                    .document(postId)
                    .get()
                    .await()

                val loadedPost = snapshot.toObject(Post::class.java)?.copy(id = snapshot.id)
                _post.value = loadedPost
            } catch (e: Exception) {
                _post.value = null
            }
        }
    }

    /**
     * Carrega os comentários do post e escuta atualizações em tempo real.
     */
    private fun carregarComentarios() {
        viewModelScope.launch(dispatcher) {
            db.collection("posts")
                .document(postId)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener

                    val lista = snapshot?.documents?.mapNotNull {
                        it.toObject(Comment::class.java)?.copy(id = it.id)
                    } ?: emptyList()
                    _comments.value = lista
                }
        }
    }

    /**
     * Envia um novo comentário para o post.
     */
    fun enviarComentario(autor: String, texto: String, onSuccess: () -> Unit) {
        viewModelScope.launch(dispatcher) {
            try {
                _isSending.value = true

                val novoComentario = hashMapOf(
                    "authorName" to autor,
                    "text" to texto,
                    "timestamp" to System.currentTimeMillis()
                )

                db.collection("posts")
                    .document(postId)
                    .collection("comments")
                    .add(novoComentario)
                    .await()

                _isSending.value = false
                onSuccess()
            } catch (e: Exception) {
                _isSending.value = false
            }
        }
    }
}
