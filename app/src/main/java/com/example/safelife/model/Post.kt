package com.example.safelife.model

data class Post(
    val id: String = "",
    val authorName: String = "",
    val content: String = "",
    val timestamp: Long = 0L,
    val commentCount: Int = 0,
    val likeCount: Int = 0,
    val likedBy: List<String> = emptyList() // ✅ Adicionado para persistência de curtidas por usuário
) {
    val formattedDate: String
        get() = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            .format(java.util.Date(timestamp))
}
