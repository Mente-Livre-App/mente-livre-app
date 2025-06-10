package com.example.safelife.viewModel.chat.profissional

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelife.model.Message
import com.example.safelife.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatProfissionalViewModel(
    private val profissionalId: String,
    private val pacienteId: String,
    private val agendamentoId: String
) : ViewModel() {

    private val chatRepository = ChatRepository()
    private val _mensagens = MutableStateFlow<List<Message>>(emptyList())
    val mensagens: StateFlow<List<Message>> = _mensagens

    private var chatId: String = ""

    init {
        iniciarOuCarregarChat()
    }

    fun iniciarOuCarregarChat() {
        viewModelScope.launch {
            try {
                chatId = chatRepository.getOrCreateChatId(
                    user1 = profissionalId,
                    user2 = pacienteId,
                    userType = "profissional",
                    agendamentoId = agendamentoId
                )


                chatRepository.observeMessages(chatId).collect { novasMensagens ->
                    _mensagens.value = novasMensagens
                }
            } catch (e: Exception) {
                Log.e("ChatProfissionalVM", "Erro ao configurar chat: ${e.message}")
            }
        }
    }

    fun enviarMensagem(texto: String) {
        if (texto.isBlank() || chatId.isBlank()) return

        val mensagem = Message(
            senderId = profissionalId,
            receiverId = pacienteId,
            text = texto,
            timestamp = null,
            read = false
        )

        viewModelScope.launch {
            chatRepository.sendMessage(chatId, mensagem)
        }
    }
}
