package com.example.safelife.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelife.model.Comment
import com.example.safelife.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FeedViewModel(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _userType = MutableStateFlow("")
    val userType: StateFlow<String> = _userType

    private val commentsMap = mutableMapOf<String, MutableStateFlow<List<Comment>>>()

    var isSending = mutableStateOf(false)
        private set

    fun carregarPosts() {
        viewModelScope.launch {
            db.collection("posts")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener

                    val lista = snapshot?.documents?.mapNotNull { doc ->
                        val post = doc.toObject(Post::class.java)?.copy(id = doc.id)
                        // 🔧 Recupera manualmente o likedBy, caso o mapeamento automático falhe
                        val likedBy = doc.get("likedBy") as? List<String> ?: emptyList()
                        post?.copy(likedBy = likedBy)
                    } ?: emptyList()

                    _posts.value = lista
                }
        }
    }


    fun enviarPost(
        nomeAutor: String,
        conteudo: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                isSending.value = true
                val novaPostagem = hashMapOf(
                    "authorName" to nomeAutor,
                    "content" to conteudo,
                    "timestamp" to System.currentTimeMillis(),
                    "likeCount" to 0
                )
                db.collection("posts")
                    .add(novaPostagem)
                    .addOnSuccessListener {
                        isSending.value = false
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        isSending.value = false
                        onFailure(e.message ?: "Erro ao publicar")
                    }
            } catch (e: Exception) {
                isSending.value = false
                onFailure(e.message ?: "Erro inesperado ao postar")
            }
        }
    }

    fun toggleLike(postId: String, userId: String, isCurrentlyLiked: Boolean) {
        val postRef = db.collection("posts").document(postId)

        val updates = if (isCurrentlyLiked) {
            mapOf(
                "likeCount" to FieldValue.increment(-1),
                "likedBy" to FieldValue.arrayRemove(userId)
            )
        } else {
            mapOf(
                "likeCount" to FieldValue.increment(1),
                "likedBy" to FieldValue.arrayUnion(userId)
            )
        }

        postRef.update(updates)
            .addOnSuccessListener {
                Log.d("FeedViewModel", "Like atualizado com sucesso: $userId -> $postId")
            }
            .addOnFailureListener { e ->
                Log.e("FeedViewModel", "Erro ao atualizar like: ${e.message}", e)
            }
    }


    fun enviarComentario(
        postId: String,
        texto: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = auth.currentUser
        val uid = user?.uid ?: return
        val dbUser = FirebaseFirestore.getInstance().collection("usuarios").document(uid)

        dbUser.get().addOnSuccessListener { document ->
            val nomeUsuario = document.getString("name") ?: "Usuário"

            viewModelScope.launch {
                try {
                    val novoComentario = hashMapOf(
                        "authorName" to nomeUsuario,
                        "text" to texto,
                        "timestamp" to System.currentTimeMillis()
                    )

                    db.collection("posts").document(postId)
                        .collection("comments")
                        .add(novoComentario)
                        .await()

                    carregarComentarios(postId)
                    onSuccess()
                } catch (e: Exception) {
                    onFailure(e.message ?: "Erro ao comentar")
                }
            }
        }.addOnFailureListener {
            onFailure("Erro ao buscar nome do usuário.")
        }
    }

    fun carregarComentarios(postId: String) {
        viewModelScope.launch {
            db.collection("posts").document(postId)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener

                    val listaComentarios = snapshot?.documents?.mapNotNull {
                        it.toObject(Comment::class.java)
                    } ?: emptyList()

                    commentsMap.getOrPut(postId) { MutableStateFlow(emptyList()) }.value = listaComentarios
                }
        }
    }

    fun getCommentsForPost(postId: String): StateFlow<List<Comment>> {
        return commentsMap.getOrPut(postId) { MutableStateFlow(emptyList()) }
    }

    fun buscarTipoUsuario(userId: String) {
        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { document ->
                val tipoConta = document.getString("userType") ?: ""
                _userType.value = tipoConta
            }
            .addOnFailureListener {
                _userType.value = ""
            }
    }
}
