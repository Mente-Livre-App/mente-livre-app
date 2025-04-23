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


@Composable
fun ChatScreen(
    currentUserId: String,
    otherUserId: String,
    viewModel: ChatPacienteViewModel = viewModel(factory = ChatPacienteViewModelFactory(currentUserId, otherUserId))
) {
    val messages = viewModel.messages
    var messageText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Lista de mensagens
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { message ->
                MessageBubble(message, isCurrentUser = message.senderId == currentUserId)
            }
        }

        // Campo de mensagem e botão de envio
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
    }
}

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
