package com.example.safelife.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> = _isUserLoggedIn

    private val _isCheckingAuth = MutableLiveData(true)
    val isCheckingAuth: LiveData<Boolean> = _isCheckingAuth

    init {
        _isCheckingAuth.value = true
        auth.addAuthStateListener { firebaseAuth ->
            _isUserLoggedIn.value = firebaseAuth.currentUser != null
            _isCheckingAuth.value = false
            Log.d("AuthViewModel", "Estado do login atualizado: ${_isUserLoggedIn.value}")
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        Log.d("AuthViewModel", "Tentativa de login com email: $email")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "Login bem-sucedido para: $email")
                    onSuccess()
                } else {
                    val errorMsg = task.exception?.message ?: "Erro desconhecido"
                    Log.e("AuthViewModel", "Erro ao fazer login: $errorMsg", task.exception)
                    onError(errorMsg)
                }
            }
    }

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
        Log.d("AuthViewModel", "Iniciando cadastro: $email como $userType")

        if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            Log.w("AuthViewModel", "Campos obrigatórios ausentes")
            onError("Todos os campos devem ser preenchidos.")
            return
        }

        if (password != confirmPassword) {
            Log.w("AuthViewModel", "Senhas não coincidem")
            onError("As senhas não coincidem.")
            return
        }

        if (userType.lowercase() == "profissional" && crp.isBlank()) {
            Log.w("AuthViewModel", "CRP obrigatório para profissional")
            onError("O campo CRP é obrigatório para profissionais.")
            return
        }

        Log.d("AuthViewModel", "Criando usuário no Firebase Authentication...")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid
                    if (userId == null) {
                        Log.e("AuthViewModel", "UID retornado é nulo após criação do usuário!")
                        onError("Erro ao obter ID do usuário.")
                        return@addOnCompleteListener
                    }

                    Log.d("AuthViewModel", "UID gerado: $userId")

                    val user = hashMapOf(
                        "uid" to userId,
                        "name" to name,
                        "email" to email,
                        "phone" to phone,
                        "userType" to userType.lowercase(),
                        "crp" to if (userType.lowercase() == "profissional") crp else ""
                    )

                    Log.d("AuthViewModel", "Tentando salvar no Firestore com userType=${userType.lowercase()}")

                    db.collection("usuarios").document(userId).set(user)
                        .addOnSuccessListener {
                            Log.d("AuthViewModel", "Usuário salvo no Firestore com sucesso!")
                            _isUserLoggedIn.postValue(true)
                            _isCheckingAuth.postValue(false)
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            Log.e("AuthViewModel", "Erro ao salvar usuário no Firestore: ${e.message}", e)
                            onError(e.message ?: "Erro ao salvar no banco de dados.")
                        }
                } else {
                    val errorMsg = task.exception?.message ?: "Erro desconhecido ao cadastrar usuário."
                    Log.e("AuthViewModel", "Erro ao criar usuário no FirebaseAuth: $errorMsg", task.exception)
                    onError(errorMsg)
                }
            }
    }

    fun resetPassword(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "E-mail de redefinição enviado.")
                    callback(true, "E-mail enviado. Verifique sua caixa de entrada.")
                } else {
                    val errorMsg = task.exception?.message ?: "Erro ao enviar e-mail de redefinição."
                    Log.e("AuthViewModel", "Erro ao redefinir senha: $errorMsg", task.exception)
                    callback(false, errorMsg)
                }
            }
    }

    fun logout(onLogout: () -> Unit) {
        auth.signOut()
        _isUserLoggedIn.value = false
        _isCheckingAuth.value = false
        Log.d("AuthViewModel", "Usuário deslogado.")
        onLogout()
    }

    fun getUserType(userId: String, callback: (String?) -> Unit) {
        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { document ->
                val tipoConta = document.getString("userType")
                Log.d("AuthViewModel", "Tipo de usuário obtido: $tipoConta")
                callback(tipoConta)
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Erro ao buscar tipo de usuário: ${e.message}", e)
                callback(null)
            }
    }

    fun excluirConta(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid

            // Exclui dados no Firestore
            firestore.collection("usuarios").document(uid).delete()
                .addOnSuccessListener {
                    // Exclui a conta de autenticação
                    user.delete()
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onError("Erro ao excluir conta: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    onError("Erro ao excluir dados: ${e.message}")
                }
        } else {
            onError("Usuário não autenticado.")
        }
    }

}
