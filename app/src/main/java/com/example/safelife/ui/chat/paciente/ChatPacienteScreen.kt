package com.example.safelife.ui.chat.paciente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safelife.model.Message
import com.example.safelife.viewModel.chat.paciente.ChatPacienteViewModel
import com.example.safelife.viewModel.chat.paciente.ChatPacienteViewModelFactory
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.layout.imePadding
import kotlinx.coroutines.launch


@Composable
fun ChatScreen(
    currentUserId: String,  // ID do usuário atual (quem está logado)
    otherUserId: String,    // ID do outro usuário (destinatário da conversa)
    viewModel: ChatPacienteViewModel = viewModel(factory = ChatPacienteViewModelFactory(currentUserId, otherUserId))
) {
    val messages = viewModel.messages                     // Lista de mensagens observável
    var messageText by remember { mutableStateOf("") }    // Texto atual digitado pelo usuário

    val listState = rememberLazyListState()               // Estado para controlar rolagem da lista
    val coroutineScope = rememberCoroutineScope()

    /**
     * Quando novas mensagens forem recebidas, rola automaticamente para a última mensagem.
     */
    LaunchedEffect(messages) {
        snapshotFlow { messages.size }.collect {
            coroutineScope.launch {
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(0)
                }
            }
        }
    }

    // Ordena as mensagens por timestamp de forma decrescente (mensagem mais nova no topo)
    val mensagensOrdenadas = messages
        .filter { it.timestamp != null }
        .sortedBy { it.timestamp }
        .reversed()

    /**
     * Layout principal do chat, com rolagem e ajuste ao teclado virtual.
     */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding() // Ajusta o conteúdo para não ficar escondido atrás do teclado
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Lista de mensagens com rolagem reversa (de baixo pra cima)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                reverseLayout = true
            ) {
                items(mensagensOrdenadas) { message ->
                    MessageBubble(message, isCurrentUser = message.senderId == currentUserId)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Campo de texto e botão de enviar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Digite uma mensagem...") }
                )

                IconButton(
                    onClick = {
                        viewModel.sendMessage(messageText)
                        messageText = ""
                    },
                    enabled = messageText.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar")
                }
            }

            // Garante espaço abaixo do teclado quando ele estiver aberto
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.ime))
        }
    }
}

/**
 * Composable que desenha o "balão de mensagem" na tela, com cores diferentes para
 * remetente e destinatário.
 *
 * @param message Objeto da mensagem contendo texto, remetente, etc.
 * @param isCurrentUser Define se a mensagem foi enviada pelo usuário atual.
 */
@Composable
fun MessageBubble(message: Message, isCurrentUser: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrentUser) Color(0xFF1976D2) else Color(0xFFE0E0E0),
                contentColor = if (isCurrentUser) Color.White else Color.Black
            )
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
