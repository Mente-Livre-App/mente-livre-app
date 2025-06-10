package com.example.safelife.ui.chat.profissional

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safelife.model.Message
import com.example.safelife.viewModel.chat.profissional.ChatProfissionalViewModel
import com.example.safelife.viewModel.chat.profissional.ChatProfissionalViewModelFactory
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.layout.imePadding
import kotlinx.coroutines.launch



@Composable
fun ChatProfissionalScreen(
    profissionalId: String,
    pacienteId: String,
    agendamentoId: String
) {
    val viewModel: ChatProfissionalViewModel = viewModel(
        factory = ChatProfissionalViewModelFactory(profissionalId, pacienteId, agendamentoId)
    )

    val mensagens by viewModel.mensagens.collectAsState()
    var textoMensagem by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        viewModel.iniciarOuCarregarChat()
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(mensagens) {
        snapshotFlow { mensagens.size }.collect {
            coroutineScope.launch {
                if (mensagens.isNotEmpty()) {
                    listState.animateScrollToItem(0)
                }
            }
        }
    }

//    val mensagensOrdenadas = mensagens.sortedBy { it.timestamp }.reversed()
    val mensagensOrdenadas = mensagens
        .filter { it.timestamp != null }
        .sortedBy { it.timestamp }
        .reversed()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding())
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Text(
                text = "Conversa com paciente",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            )



            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                reverseLayout = true
            ) {
                items(mensagensOrdenadas) { mensagem ->
                    val alinhamento =
                        if (mensagem.senderId == profissionalId) Alignment.End else Alignment.Start
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = if (alinhamento == Alignment.End) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (alinhamento == Alignment.End)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(
                                text = mensagem.text ?: "",
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier.weight(1f),
                    value = textoMensagem,
                    onValueChange = { textoMensagem = it },
                    placeholder = { Text("Digite uma mensagem...") }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (textoMensagem.text.isNotBlank()) {
                            Log.d("ChatProfissionalScreen", "Enviando: ${textoMensagem.text}")
                            viewModel.enviarMensagem(textoMensagem.text)
                            textoMensagem = TextFieldValue("")
                        }
                    }
                ) {
                    Text("Enviar")
                }
            }

            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.ime))
        }
    }
}

