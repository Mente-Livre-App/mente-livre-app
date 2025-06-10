package com.example.safelife.repository

import android.util.Log
import com.example.safelife.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FieldValue

open class ChatRepository {
    private val db: FirebaseFirestore = Firebase.firestore

    /**
     * Obtém ou cria um ID de chat para dois usuários.
     */
    suspend fun getOrCreateChatId(user1: String, user2: String): String {
        val participantsSorted = listOf(user1, user2).sorted()

        val snapshot = db.collection("chats")
            .whereEqualTo("participants", participantsSorted)
            .get()
            .await()

        if (!snapshot.isEmpty) {
            return snapshot.documents[0].id
        }

        val newChat = hashMapOf(
            "participants" to participantsSorted,
            "createdAt" to System.currentTimeMillis()
        )

        val docRef = db.collection("chats").add(newChat).await()
        return docRef.id
    }

    /**
     * Envia uma mensagem para um chat existente.
     */
    suspend fun sendMessage(chatId: String, message: Message) {
        try {
            Log.d("ChatRepository", "Enviando mensagem: ${message.text} para chat $chatId")
            val firebaseMessage = hashMapOf(
                "senderId" to message.senderId,
                "receiverId" to message.receiverId,
                "text" to message.text,
                "timestamp" to FieldValue.serverTimestamp(),
                "read" to message.read
            )

            db.collection("chats/$chatId/messages")
                .add(firebaseMessage)
                .await()

            Log.d("ChatRepository", "Mensagem enviada com sucesso.")
        } catch (e: Exception) {
            Log.e("ChatRepository", "Erro ao enviar mensagem: ${e.message}", e)
        }
    }

    /**
     * Observa mensagens em tempo real de um chat específico.
     */
    fun observeMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val listener = db.collection("chats/$chatId/messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val senderId = data["senderId"] as? String ?: return@mapNotNull null
                    val receiverId = data["receiverId"] as? String ?: return@mapNotNull null
                    val text = data["text"] as? String ?: ""
                    val timestamp = (data["timestamp"] as? com.google.firebase.Timestamp)?.toDate()?.time
                    val read = data["read"] as? Boolean ?: false

                    Message(
                        senderId = senderId,
                        receiverId = receiverId,
                        text = text,
                        timestamp = timestamp,
                        read = read
                    )
                } ?: emptyList()

                Log.d("ChatRepository", "Mensagens convertidas: ${messages.size}")
                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Obtém ou cria um chat vinculado a um agendamento.
     */
    suspend fun getOrCreateChatId(
        user1: String,
        user2: String,
        userType: String,
        agendamentoId: String
    ): String {
        val participantsSorted = listOf(user1, user2).sorted()

        val snapshot = db.collection("chats")
            .whereEqualTo("participants", participantsSorted)
            .whereEqualTo("agendamentoId", agendamentoId)
            .get()
            .await()

        if (!snapshot.isEmpty) {
            return snapshot.documents[0].id
        }

        val newChat = hashMapOf(
            "participants" to participantsSorted,
            "userType" to userType,
            "agendamentoId" to agendamentoId,
            "createdAt" to System.currentTimeMillis()
        )

        val docRef = db.collection("chats").add(newChat).await()
        return docRef.id
    }
}
