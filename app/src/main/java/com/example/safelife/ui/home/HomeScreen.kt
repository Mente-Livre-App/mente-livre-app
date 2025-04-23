package com.example.safelife.ui.home

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    navigateToListaProfissionais: (String) -> Unit,
    navigateToListaPacientes: (String) -> Unit,
    navigateToConsultas: () -> Unit,
    navigateToForum: () -> Unit,
    navigateToLogin: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center // ✅ Centraliza todo o conteúdo na tela
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp) // ✅ Aumentado o espaçamento entre os botões
        ) {
            Text(
                text = "Escolha uma das opções para continuar:",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            // Botão Agendar Consulta (Azul)
            Button(
                onClick = {
                    Log.d("HomeScreen", "Usuário acessou a tela de Agendamento de Consultas")
                    navigateToConsultas()
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2374AB)),
                border = BorderStroke(2.dp, Color(0xFF2374AB)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(55.dp) // ✅ Ajustado para manter proporção correta
            ) {
                Text(text = "Agendar Consulta", color = Color(0xFF616161))
            }

            // Botão Chat de Suporte (Amarelo)
            Button(
                onClick = {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button

                    // Aqui você decide com base no tipo de usuário
                    val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    firestore.collection("usuarios").document(userId)
                        .get()
                        .addOnSuccessListener { doc ->
                            val tipo = doc.getString("userType")?.lowercase()
                            Log.d("HomeScreen", "Usuário logado como: $tipo")

                            if (tipo == "profissional") {
                                navigateToListaPacientes(userId)
                            } else {
                                navigateToListaProfissionais(userId)
                            }
                        }
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFC43D)),
                border = BorderStroke(2.dp, Color(0xFFFFC43D)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(55.dp)
            ) {
                Text(text = "Chat de Suporte", color = Color(0xFF616161))
            }

            // Botão Feed (Verde)
            Button(
                onClick = {
                    Log.d("HomeScreen", "Usuário acessou a tela do Feed")
                    navigateToForum()
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF06D6A0)),
                border = BorderStroke(2.dp, Color(0xFF06D6A0)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(55.dp)
            ) {
                Text(text = "Feed", color = Color(0xFF616161))
            }

            // Botão Sair (Vermelho)
            Button(
                onClick = {
                    Log.d("HomeScreen", "Usuário fez logout")
                    auth.signOut()
                    navigateToLogin()
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFED474A)),
                border = BorderStroke(2.dp, Color(0xFFED474A)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(55.dp)
            ) {
                Text(text = "Sair", color = Color(0xFFED474A))
            }
        }
    }
}
