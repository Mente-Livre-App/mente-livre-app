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

@Composable
fun ChatProfissionalScreen(
    profissionalId: String,
    pacienteId: String
) {
    val viewModel: ChatProfissionalViewModel = viewModel(
        factory = ChatProfissionalViewModelFactory(profissionalId, pacienteId)
    )

    val mensagens by viewModel.mensagens.collectAsState()
    var textoMensagem by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        viewModel.iniciarOuCarregarChat()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text(
            text = "Conversa com paciente",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(mensagens) { mensagem ->
                val alinhamento = if (mensagem.senderId == profissionalId) Alignment.End else Alignment.Start
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = if (alinhamento == Alignment.End) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (alinhamento == Alignment.End) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
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

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
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
                        textoMensagem = TextFieldValue("") // Limpa campo
                    }
                }
            ) {
                Text("Enviar")
            }
        }
    }
}
