package com.example.safelife.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
//    private val db: FirebaseFirestore = FirebaseFirestore.getInstance() // Adicionado corretamente
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance() // Banco de Dados Firestore

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> = _isUserLoggedIn

    private val _isCheckingAuth = MutableLiveData<Boolean>(true) // Estado de carregamento
    val isCheckingAuth: LiveData<Boolean> = _isCheckingAuth

    init {
        _isCheckingAuth.value = true // Inicia o estado como "verificando"
        // Adiciona um listener para detectar automaticamente se o usuário está logado
        auth.addAuthStateListener { firebaseAuth ->
            _isUserLoggedIn.value = firebaseAuth.currentUser != null
            _isCheckingAuth.value = false // Agora já verificou, pode carregar a tela correta
            Log.d("AuthViewModel", "Estado do login atualizado: ${_isUserLoggedIn.value}")
        }
    }

    /**
     * Faz login com e-mail e senha no Firebase.
     */
    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        Log.d("AuthViewModel", "Tentativa de login com email: $email")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "Login bem-sucedido para: $email")
                    onSuccess()
                } else {
                    val errorMsg = task.exception?.message ?: "Erro desconhecido"
                    Log.e("AuthViewModel", "Erro ao fazer login: $errorMsg")
                    onError(errorMsg)
                }
            }
    }

    /**
     * Cadastra um novo usuário no Firebase Authentication e salva os dados no Realtime Database.
     */
    fun signup(
        name: String,
        email: String,
        phone: String,
        userType: String,
        crp: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Verifica se os campos obrigatórios estão preenchidos
        if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            onError("Todos os campos devem ser preenchidos.")
            return
        }

        // Verifica se as senhas coincidem
        if (password != confirmPassword) {
            onError("As senhas não coincidem.")
            return
        }

        // Se for profissional, verifica se o CRP foi preenchido
        if (userType == "profissional" && crp.isBlank()) {
            onError("O campo CRP é obrigatório para profissionais.")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: return@addOnCompleteListener

                    val user = hashMapOf(
                        "uid" to userId,
                        "name" to name,
                        "email" to email,
                        "phone" to phone,
                        "userType" to userType.lowercase(),
                        "crp" to if (userType == "profissional") crp else ""
                    )

                    db.collection("usuarios").document(userId).set(user)
                        .addOnSuccessListener {
                            Log.d("AuthViewModel", "Usuário salvo no Realtime Database com sucesso!")
                            _isUserLoggedIn.postValue(true)
                            _isCheckingAuth.postValue(false)
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            Log.e("AuthViewModel", "Erro ao salvar usuário no Realtime Database: ${e.message}")
                            onError(e.message ?: "Erro ao salvar no banco")
                        }
                } else {
                    onError(task.exception?.message ?: "Erro desconhecido ao cadastrar usuário")
                }
            }
    }


    fun resetPassword(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "E-mail de redefinição de senha enviado para $email")
                    callback(true, "E-mail de redefinição enviado para $email. Verifique sua caixa de entrada.")
                } else {
                    val errorMsg = task.exception?.message ?: "Erro ao enviar e-mail de redefinição"
                    Log.e("AuthViewModel", "Erro ao redefinir senha: $errorMsg")
                    callback(false, errorMsg)
                }
            }
    }


    /**
     * Faz logout do usuário e atualiza o estado do login.
     */
    fun logout(onLogout: () -> Unit) {
        auth.signOut()
        _isUserLoggedIn.value = false
        _isCheckingAuth.value = false // Reinicia para permitir navegação correta
        Log.d("AuthViewModel", "Usuário deslogado")
        onLogout()
    }

    fun getUserType(userId: String, callback: (String?) -> Unit) {
        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { document ->
                val tipoConta = document.getString("userType")
                callback(tipoConta)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}

