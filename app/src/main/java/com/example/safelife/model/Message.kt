package com.example.safelife.model

/**
 * Modelo de dados para representar uma mensagem trocada entre dois usuários no chat.
 * Essa classe será utilizada para armazenar e recuperar dados no Firebase Firestore.
 */
data class Message(
    val senderId: String = "",      // ID de quem enviou a mensagem
    val receiverId: String = "",    // ID de quem recebeu a mensagem
    val text: String = "",          // Conteúdo textual da mensagem
    val timestamp: Long? = null,       // Momento em que a mensagem foi enviada (em milissegundos)
    val read: Boolean = false       // Indica se a mensagem foi lida ou não
)