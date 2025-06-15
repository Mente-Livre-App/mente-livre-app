package com.example.safelife.ui.chat.profissional

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safelife.viewModel.chat.profissional.ChatProfissionalViewModel
import com.example.safelife.viewModel.chat.profissional.ChatProfissionalViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatProfissionalScreen(
    navController: NavController,
    profissionalId: String,
    pacienteId: String,
    agendamentoId: String,
    userType: String
) {
    val viewModel: ChatProfissionalViewModel = viewModel(
        factory = ChatProfissionalViewModelFactory(profissionalId, pacienteId)
    )

    val mensagens by viewModel.mensagens.collectAsState()
    var textoMensagem by remember { mutableStateOf(TextFieldValue("")) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.iniciarOuCarregarChat()
    }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Bem-vindo ao",
                            color = Color(0xFF9C27B0),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Suporte à Saúde Mental",
                            color = Color(0xFF9C27B0),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
                .padding(12.dp)
        ) {
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
                        val isProfissional = mensagem.senderId == profissionalId
                        val background = if (isProfissional) Color(0xFF3F51B5) else Color(0xFF8E24AA)
                        val alignment = if (isProfissional) Arrangement.End else Arrangement.Start

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
                                    text = mensagem.text ?: "",
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
                            viewModel.enviarMensagem(textoMensagem.text)
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
    }
}
