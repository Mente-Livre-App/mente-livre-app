package com.example.safelife.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/**
 * Diálogo de consentimento da LGPD.
 * Exibido na primeira execução do app, antes de liberar o uso.
 */
@Composable
fun PolicyConsentDialog(
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onViewPolicy: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {}, // impede fechar com clique fora
        confirmButton = {
            Button(onClick = onAccept) {
                Text("Aceito")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDecline) {
                Text("Recusar")
            }
        },
        title = {
            Text("Política de Privacidade")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Este aplicativo coleta dados como nome, e-mail, mensagens e informações de agendamento " +
                            "para fins de funcionamento da plataforma. Ao continuar, você concorda com nossa Política " +
                            "de Privacidade, conforme a Lei Geral de Proteção de Dados (LGPD)."
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ver política completa",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.clickable { onViewPolicy() }
                )
            }
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}

