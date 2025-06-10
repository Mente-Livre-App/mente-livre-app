package com.example.safelife.viewModel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId

    private val _userType = MutableStateFlow("")
    val userType: StateFlow<String> = _userType

    init {
        val user = auth.currentUser
        if (user != null) {
            _userId.value = user.uid
            loadUserType(user.uid)
        }
    }

    private fun loadUserType(userId: String) {
        firestore.collection("usuarios").document(userId).get()
            .addOnSuccessListener { doc ->
                _userType.value = doc.getString("userType")?.lowercase() ?: ""
            }
    }

    fun logout() {
        auth.signOut()
    }
}
