package com.example.safelife.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelife.model.Comment
import com.example.safelife.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel responsável por gerenciar os dados da tela de feed (postagens e comentários).
 * Ele se conecta com o Firebase Firestore para carregar, publicar e atualizar posts/comentários.
 */
class FeedViewModel(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(), // Instância do banco Firestore
    private val auth: FirebaseAuth = FirebaseAuth.getInstance() // Instância da autenticação Firebase
) : ViewModel() {

    // Lista reativa que contém os posts carregados do banco
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts // Exposta como somente leitura

    // Armazena o tipo de usuário atual (ex: "paciente", "profissional")
    private val _userType = MutableStateFlow("")
    val userType: StateFlow<String> = _userType

    // Mapa reativo que relaciona postagens com seus respectivos comentários
    private val commentsMap = mutableMapOf<String, MutableStateFlow<List<Comment>>>()

    // Indica se uma postagem está sendo enviada no momento (para controle de loading)
    var isSending = mutableStateOf(false)
        private set

    /**
     * Carrega os posts do Firestore em tempo real, ordenados por timestamp.
     * Qualquer alteração na coleção é refletida automaticamente na UI.
     */
    fun carregarPosts() {
        viewModelScope.launch {
            db.collection("posts")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener // Ignora se houver erro

                    val lista = snapshot?.documents?.mapNotNull {
                        it.toObject(Post::class.java)?.copy(id = it.id) // Garante que o ID do documento esteja presente
                    } ?: emptyList()

                    _posts.value = lista // Atualiza o fluxo de posts
                }
        }
    }

    /**
     * Envia uma nova postagem para o Firestore.
     * @param nomeAutor Nome de quem está postando.
     * @param conteudo Conteúdo da postagem.
     * @param onSuccess Função chamada em caso de sucesso.
     * @param onFailure Função chamada em caso de falha, com a mensagem de erro.
     */
    fun enviarPost(
        nomeAutor: String,
        conteudo: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                isSending.value = true // Ativa loading
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

    /**
     * Atualiza a contagem de curtidas de um post específico.
     * @param postId ID do post a ser atualizado.
     * @param currentLikes Número atual de curtidas.
     * @param isLiked Indica se o usuário curtiu ou descurtiu.
     */
    fun atualizarLike(postId: String, currentLikes: Int, isLiked: Boolean) {
        val novoLikeCount = if (isLiked) currentLikes + 1 else (currentLikes - 1).coerceAtLeast(0)
        db.collection("posts").document(postId)
            .update("likeCount", novoLikeCount)
    }

    /**
     * Envia um novo comentário para um post específico.
     * @param postId ID do post a ser comentado.
     * @param texto Texto do comentário.
     * @param onSuccess Callback de sucesso.
     * @param onFailure Callback de falha.
     */
    fun enviarComentario(
        postId: String,
        texto: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = auth.currentUser
        val nomeUsuario = user?.displayName ?: "Usuário"

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

                carregarComentarios(postId) // Recarrega comentários após adicionar
                onSuccess()
            } catch (e: Exception) {
                onFailure(e.message ?: "Erro ao comentar")
            }
        }
    }

    /**
     * Carrega os comentários de um post específico em tempo real.
     * Os comentários são armazenados em um mapa local separado por postId.
     */
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

    /**
     * Retorna o fluxo reativo de comentários de um determinado post.
     * Se não existir, inicializa com uma lista vazia.
     */
    fun getCommentsForPost(postId: String): StateFlow<List<Comment>> {
        return commentsMap.getOrPut(postId) { MutableStateFlow(emptyList()) }
    }

    /**
     * Busca o tipo de usuário (paciente, profissional, etc) no Firestore com base no ID.
     * @param userId ID do usuário autenticado.
     */
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
