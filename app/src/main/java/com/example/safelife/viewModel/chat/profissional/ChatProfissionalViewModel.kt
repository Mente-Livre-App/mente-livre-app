package com.example.safelife.viewModel.chat.profissional

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelife.model.Message
import com.example.safelife.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pelo chat entre profissional e paciente.
 *
 * @param profissionalId ID do profissional autenticado.
 * @param pacienteId ID do paciente com quem o profissional está conversando.
 * @param chatRepository Repositório injetável para facilitar testes.
 */
class ChatProfissionalViewModel(
    private val profissionalId: String,
    private val pacienteId: String,
    private val chatRepository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _mensagens = MutableStateFlow<List<Message>>(emptyList())
    val mensagens: StateFlow<List<Message>> = _mensagens

    private var chatId: String = ""

    init {
        iniciarOuCarregarChat()
    }

    fun iniciarOuCarregarChat() {
        viewModelScope.launch {
            try {
                chatId = chatRepository.getOrCreateChatId(profissionalId, pacienteId)

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
