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

/**
 * Repositório responsável pela lógica de mensagens do chat no Firestore.
 * Oferece funcionalidades para criação de chats, envio e escuta de mensagens em tempo real.
 */
open class ChatRepository {

    // Instância do Firestore
    private val db: FirebaseFirestore = Firebase.firestore

    /**
     * Obtém o ID de um chat entre dois usuários. Caso não exista, cria um novo chat.
     *
     * @param user1 ID do primeiro usuário (por exemplo, paciente).
     * @param user2 ID do segundo usuário (por exemplo, profissional).
     * @return O ID do chat existente ou recém-criado.
     */
    open suspend fun getOrCreateChatId(user1: String, user2: String): String {
        // Ordena os participantes para garantir consistência
        val participantsSorted = listOf(user1, user2).sorted()

        // Verifica se já existe chat com os dois participantes
        val snapshot = db.collection("chats")
            .whereEqualTo("participants", participantsSorted)
            .get()
            .await()

        if (!snapshot.isEmpty) {
            return snapshot.documents[0].id
        }

        // Se não existir, cria novo chat
        val newChat = hashMapOf(
            "participants" to participantsSorted,
            "createdAt" to System.currentTimeMillis()
        )

        val docRef = db.collection("chats").add(newChat).await()
        return docRef.id
    }

    /**
     * Envia uma mensagem para um chat existente.
     *
     * @param chatId ID do chat onde a mensagem será enviada.
     * @param message Objeto contendo os dados da mensagem (texto, remetente, etc).
     */
    suspend fun sendMessage(chatId: String, message: Message) {
        try {
            Log.d("ChatRepository", "Enviando mensagem: ${message.text} para chat $chatId")

            // Estrutura da mensagem para o Firestore
            val firebaseMessage = hashMapOf(
                "senderId" to message.senderId,
                "receiverId" to message.receiverId,
                "text" to message.text,
                "timestamp" to FieldValue.serverTimestamp(), // Timestamp do servidor
                "read" to message.read
            )

            // Salva a mensagem no subdocumento de mensagens do chat
            db.collection("chats/$chatId/messages")
                .add(firebaseMessage)
                .await()

            Log.d("ChatRepository", "Mensagem enviada com sucesso.")
        } catch (e: Exception) {
            Log.e("ChatRepository", "Erro ao enviar mensagem: ${e.message}", e)
        }
    }

    /**
     * Observa em tempo real as mensagens de um determinado chat.
     *
     * @param chatId ID do chat a ser observado.
     * @return Um Flow com a lista de mensagens sempre atualizada.
     */
    open fun observeMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        // Cria listener que escuta mudanças na coleção de mensagens
        val listener = db.collection("chats/$chatId/messages")
            .orderBy("timestamp") // Ordena por ordem cronológica
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                // Mapeia os documentos em objetos Message
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val senderId = data["senderId"] as? String ?: return@mapNotNull null
                    val receiverId = data["receiverId"] as? String ?: return@mapNotNull null
                    val text = data["text"] as? String ?: ""
                    val timestamp = (data["timestamp"] as? com.google.firebase.Timestamp)
                        ?.toDate()?.time
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

        // Cancela o listener quando o Flow for encerrado
        awaitClose { listener.remove() }
    }

    /**
     * Obtém ou cria um ID de chat para um agendamento específico entre dois usuários.
     *
     * @param user1 ID do primeiro participante.
     * @param user2 ID do segundo participante.
     * @param userType Tipo do usuário que iniciou a conversa (ex: "profissional").
     * @param agendamentoId ID do agendamento relacionado à conversa.
     * @return O ID do chat existente ou recém-criado.
     */
    suspend fun getOrCreateChatId(
        user1: String,
        user2: String,
        userType: String,
        agendamentoId: String
    ): String {
        val participantsSorted = listOf(user1, user2).sorted()

        // Verifica se já existe um chat com os mesmos participantes e agendamento
        val snapshot = db.collection("chats")
            .whereEqualTo("participants", participantsSorted)
            .whereEqualTo("agendamentoId", agendamentoId)
            .get()
            .await()

        if (!snapshot.isEmpty) {
            return snapshot.documents[0].id
        }

        // Se não existir, cria novo chat vinculado ao agendamento
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
