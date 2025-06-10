package com.example.safelife.ui.home

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.safelife.viewModel.AuthViewModel
import com.example.safelife.viewModel.home.HomeViewModel

@Composable
fun HomeScreen(
    navigateToListaProfissionais: (String) -> Unit,
    navigateToListaPacientes: (String) -> Unit,
    navigateToConsultas: (String) -> Unit,
    navigateToAgendaProfissional: () -> Unit,
    navigateToForum: () -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val userId by viewModel.userId.collectAsState()
    val userType by viewModel.userType.collectAsState()

    val context = LocalContext.current
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Escolha uma das opções para continuar:",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            // ✅ Botão de agendamento separado por tipo de usuário
            if (userType == "profissional") {
                Button(
                    onClick = { navigateToAgendaProfissional() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2374AB)),
                    border = BorderStroke(2.dp, Color(0xFF2374AB)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(55.dp)
                ) {
                    Text("Gerenciar Agenda", color = Color(0xFF616161))
                }
            } else {
                Button(
                    onClick = {
                        if (userId.isNotBlank()) {
                            navigateToConsultas(userId)
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2374AB)),
                    border = BorderStroke(2.dp, Color(0xFF2374AB)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(55.dp)
                ) {
                    Text("Agendar Consulta", color = Color(0xFF616161))
                }
            }

            // Chat de Suporte (paciente → lista profissionais / profissional → lista pacientes)
            Button(
                onClick = {
                    if (userType == "profissional") {
                        navigateToListaPacientes(userId)
                    } else {
                        navigateToListaProfissionais(userId)
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFC43D)),
                border = BorderStroke(2.dp, Color(0xFFFFC43D)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(55.dp)
            ) {
                Text("Chat de Suporte", color = Color(0xFF616161))
            }

            // Feed
            Button(
                onClick = navigateToForum,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF06D6A0)),
                border = BorderStroke(2.dp, Color(0xFF06D6A0)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(55.dp)
            ) {
                Text("Feed", color = Color(0xFF616161))
            }

            // Sair
            Button(
                onClick = {
                    viewModel.logout()
                    navigateToLogin()
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFED474A)),
                border = BorderStroke(2.dp, Color(0xFFED474A)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(55.dp)
            ) {
                Text("Sair", color = Color(0xFFED474A))
            }

            // Excluir conta
            Button(
                onClick = {
                    authViewModel.excluirConta(
                        onSuccess = {
                            Toast.makeText(context, "Conta excluída com sucesso", Toast.LENGTH_LONG).show()
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        onError = { erro ->
                            Toast.makeText(context, erro, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(55.dp)
            ) {
                Text("Excluir minha conta e dados")
            }
        }
    }
}
