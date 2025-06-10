package com.example.safelife.ui.info

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliticaPrivacidadeScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Política de Privacidade") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "O aplicativo SafeLife valoriza a sua privacidade. Esta política explica como coletamos, usamos e protegemos os seus dados, em conformidade com a Lei Geral de Proteção de Dados (LGPD).",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("1. Coleta de Dados", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
            Text("Coletamos dados fornecidos por você, como nome, e-mail, mensagens de chat e agendamentos. Esses dados são utilizados exclusivamente para funcionamento do aplicativo e melhoria dos serviços.")
            Spacer(modifier = Modifier.height(12.dp))

            Text("2. Uso dos Dados", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
            Text("Seus dados são usados para:\n- Identificação e autenticação no app\n- Agendamento de consultas\n- Troca de mensagens com profissionais\n- Exibição de conteúdo personalizado")
            Spacer(modifier = Modifier.height(12.dp))

            Text("3. Compartilhamento", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
            Text("Não compartilhamos seus dados com terceiros, exceto quando exigido por lei.")
            Spacer(modifier = Modifier.height(12.dp))

            Text("4. Armazenamento e Segurança", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
            Text("Seus dados são armazenados em servidores seguros do Firebase, com criptografia e autenticação.")
            Spacer(modifier = Modifier.height(12.dp))

            Text("5. Direitos do Usuário", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
            Text("Você pode:\n- Solicitar exclusão de sua conta e dados\n- Revogar o consentimento a qualquer momento\n- Acessar ou corrigir seus dados")
            Spacer(modifier = Modifier.height(12.dp))

            Text("6. Exclusão", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
            Text("Ao solicitar a exclusão da conta, todos os dados relacionados ao seu perfil são permanentemente removidos do sistema.")
            Spacer(modifier = Modifier.height(12.dp))

            Text("7. Contato", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
            Text("Em caso de dúvidas, entre em contato conosco pelo e-mail suporte@safelife.app.")
            Spacer(modifier = Modifier.height(16.dp))

            Text("Versão: 1.0 | Última atualização: 09/06/2025", fontSize = 14.sp)
        }
    }
}
