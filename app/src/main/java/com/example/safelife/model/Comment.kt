package com.example.safelife.model

data class Comment(
    val id: String = "",
    val authorName: String = "",
    val text: String = "",
    val timestamp: Long = 0L
) {
    val formattedDate: String
        get() = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date(timestamp))
}
