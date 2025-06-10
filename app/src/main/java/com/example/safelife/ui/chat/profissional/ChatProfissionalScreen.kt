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
import com.example.safelife.viewModel.chat.profissional.ChatProfissionalViewModel
import com.example.safelife.viewModel.chat.profissional.ChatProfissionalViewModelFactory
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.imePadding
import kotlinx.coroutines.launch
/**
 * Tela de chat utilizada por profissionais para conversar com um paciente específico.
 *
 * @param profissionalId ID do profissional logado.
 * @param pacienteId ID do paciente com quem o profissional está conversando.
 */
@Composable
fun ChatProfissionalScreen(
    profissionalId: String,
    pacienteId: String
) {
    // ViewModel com parâmetros injetados via Factory
    val viewModel: ChatProfissionalViewModel = viewModel(
        factory = ChatProfissionalViewModelFactory(profissionalId, pacienteId)
    )

    // Lista de mensagens observada em tempo real
    val mensagens by viewModel.mensagens.collectAsState()

    // Campo de texto controlado
    var textoMensagem by remember { mutableStateOf(TextFieldValue("")) }

    // Controle da rolagem automática
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Inicia ou recupera o ID do chat no Firestore
    LaunchedEffect(Unit) {
        viewModel.iniciarOuCarregarChat()
    }

    // Rola automaticamente para a última mensagem recebida
    LaunchedEffect(mensagens.size) {
        coroutineScope.launch {
            if (mensagens.isNotEmpty()) {
                listState.animateScrollToItem(0)
            }
        }
    }

    // Ordena mensagens por timestamp de forma decrescente
    val mensagensOrdenadas = mensagens
        .filter { it.timestamp != null }
        .sortedBy { it.timestamp }
        .reversed()

    Log.d("ChatUI", "Mensagens visíveis na tela: ${mensagensOrdenadas.size}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding() // Ajusta a altura ao abrir o teclado
    ) {
        // Título da tela
        Text(
            text = "Conversa com paciente",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        )

        // Lista de mensagens com rolagem reversa
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

        // Campo de entrada e botão de envio
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
                        textoMensagem = TextFieldValue("") // Limpa campo após envio
                    }
                }
            ) {
                Text("Enviar")
            }
        }
    }
}
