package com.example.safelife.model

/**
 * Modelo de dados para representar um profissional na listagem e nos chats.
 * Esses dados são recuperados da coleção 'usuarios' no Firestore.
 */
data class Profissional(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val crp: String = "",
    val userType: String = ""
)