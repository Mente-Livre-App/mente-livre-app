package com.example.safelife.ui.chat.paciente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safelife.model.Message
import com.example.safelife.viewModel.chat.paciente.ChatPacienteViewModel
import com.example.safelife.viewModel.chat.paciente.ChatPacienteViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.statusBarsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController? = null,
    currentUserId: String,
    otherUserId: String,
    viewModel: ChatPacienteViewModel = viewModel(factory = ChatPacienteViewModelFactory(currentUserId, otherUserId))
) {
    val mensagens = viewModel.messages
    var textoMensagem by remember { mutableStateOf(TextFieldValue("")) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(mensagens.size) {
        coroutineScope.launch {
            if (mensagens.isNotEmpty()) {
                listState.animateScrollToItem(0)
            }
        }
    }

    val mensagensOrdenadas = mensagens
        .filter { it.timestamp != null }
        .sortedBy { it.timestamp }
        .reversed()

    Box(modifier = Modifier.fillMaxSize()) {

        // ✅ Conteúdo principal com padding no topo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .padding(top = 72.dp) // espaço reservado para o botão
        ) {
            Text(
                text = "Bem-vindo ao",
                color = Color(0xFF9C27B0),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Suporte à Saúde Mental",
                color = Color(0xFF9C27B0),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    reverseLayout = true
                ) {
                    items(mensagensOrdenadas) { mensagem ->
                        val isPaciente = mensagem.senderId == currentUserId
                        val background = if (isPaciente) Color(0xFF3F51B5) else Color(0xFF8E24AA)
                        val alignment = if (isPaciente) Arrangement.End else Arrangement.Start

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = alignment
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = background
                            ) {
                                Text(
                                    text = mensagem.text,
                                    color = Color.White,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = textoMensagem,
                    onValueChange = { textoMensagem = it },
                    placeholder = { Text("Digite sua mensagem...") },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFF0F0F0),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (textoMensagem.text.isNotBlank()) {
                            viewModel.sendMessage(textoMensagem.text)
                            textoMensagem = TextFieldValue("")
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
                ) {
                    Text("Enviar", color = Color.White)
                }
            }
        }

        // ✅ Botão voltar visível no topo esquerdo
        IconButton(
            onClick = { navController?.popBackStack() },
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 8.dp)
                .size(48.dp)
                .zIndex(2f)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = Color(0xFF9C27B0),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
