// ViewModel principal para controlar o feed da aplicação
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

class FeedViewModel(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {
    // Lista de postagens observável
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts
    // Tipo de usuário (profissional ou paciente)
    private val _userType = MutableStateFlow("")
    val userType: StateFlow<String> = _userType
    // Estado para controlar envio de postagens
    var isSending = mutableStateOf(false)
        private set
    // Carrega postagens do Firestore em tempo real
    fun carregarPosts() {
        viewModelScope.launch {
            db.collection("posts")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    val lista = snapshot?.documents?.mapNotNull {
                        it.toObject(Post::class.java)?.copy(id = it.id)
                    } ?: emptyList()
                    _posts.value = lista
                }
        }
    }
    // Busca o tipo de conta do usuário atual no Firestore
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