package com.example.safelife.viewModel.chat.paciente

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelife.model.Message
import com.example.safelife.repository.ChatRepository
import kotlinx.coroutines.launch

class ChatPacienteViewModel(
    private val currentUserId: String,   // ID do usuário atual
    private val otherUserId: String,    // ID do outro usuário
    private val chatRepository: ChatRepository = ChatRepository()
) : ViewModel() {

    // Lista observável de mensagens
    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> = _messages

    private var chatId: String = ""  // ID da conversa atual

    init {
        setupChat()  // Inicia a configuração do chat quando o ViewModel é criado
    }

    private fun setupChat() {
        viewModelScope.launch {
            try {
                chatId = chatRepository.getOrCreateChatId(
                    user1 = currentUserId,
                    user2 = otherUserId,
                    userType = "paciente"
                )

                chatRepository.observeMessages(chatId).collect { newMessages ->
                    _messages.clear()
                    _messages.addAll(newMessages)
                }

            } catch (e: Exception) {
                Log.e("ChatViewModel", "Erro ao configurar chat: ${e.message}")
            }
        }
    }


    // Envia uma nova mensagem
    fun sendMessage(text: String) {
        if (text.isBlank() || chatId.isBlank()) {
            Log.e("ChatViewModel", "Mensagem inválida ou chatId não configurado")
            return
        }  // Não envia mensagens vazias

        viewModelScope.launch {
            val message = Message(
                senderId = currentUserId,
                receiverId = otherUserId,
                text = text,
                timestamp = null, // ✅ será definido automaticamente pelo Firestore
                read = false  // Inicialmente não lida
            )

            Log.d("ChatViewModel", "Enviando mensagem: $message")
            chatRepository.sendMessage(chatId, message)  // Persiste no Firestore
        }
    }
}