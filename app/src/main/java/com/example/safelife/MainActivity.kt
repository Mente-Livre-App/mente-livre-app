package com.example.safelife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.safelife.ui.auth.LoginScreen
import com.example.safelife.ui.auth.SignupScreen
import com.example.safelife.ui.home.HomeScreen
//import com.example.safelife.viewmodel.AuthViewModel
import com.example.safelife.ui.theme.SafeLifeTheme
import com.example.safelife.viewModel.AuthViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import com.example.safelife.ui.chat.paciente.ChatScreen
import com.example.safelife.ui.chat.paciente.ListaProfissionaisScreen
import com.example.safelife.ui.chat.profissional.ChatProfissionalScreen
import com.example.safelife.ui.chat.profissional.ListaPacientesScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeLifeTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val isUserLoggedIn by authViewModel.isUserLoggedIn.observeAsState(null)
    val isCheckingAuth by authViewModel.isCheckingAuth.observeAsState(true) // Novo estado

    // Se ainda está verificando o login, mostra uma tela de carregamento
    if (isCheckingAuth) {
        LoadingScreen()
        return
    }

    LaunchedEffect(isUserLoggedIn) {
        if (isUserLoggedIn == true) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        } else if (isUserLoggedIn == false) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    NavHost(navController, startDestination = if (isUserLoggedIn == true) "home" else "login") {
        composable("login") {
            LoginScreen(
                navigateToHome = {
                    if (isUserLoggedIn == true) { // Verifica se realmente está logado antes de ir para a Home
                        navController.navigate("home")
                    }
                },
                navigateToSignup = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            SignupScreen(
                navigateToHome = {  // Adicionando corretamente navigateToHome
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                navigateToListaProfissionais = { userId ->
                    navController.navigate("lista_profissionais/$userId")
                },
                navigateToListaPacientes = { userId ->
                    navController.navigate("lista_pacientes/$userId")
                },
                navigateToConsultas = { /* Adicionar navegação para Consultas */ },
                navigateToForum = { /* Adicionar navegação para Fórum */ },
                navigateToLogin = {
                    authViewModel.logout {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("lista_profissionais/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ListaProfissionaisScreen(
                currentUserId = userId,
                navigateToChat = { currentUserId, recipientId ->
                    navController.navigate("chat/$currentUserId/$recipientId")
                }
            )
        }

        composable("chat/{currentUserId}/{recipientId}") { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString("currentUserId") ?: ""
            val recipientId = backStackEntry.arguments?.getString("recipientId") ?: ""

            ChatScreen(
                currentUserId = currentUserId,
                otherUserId = recipientId
            )
        }

        composable("lista_pacientes/{profissionalId}") { backStackEntry ->
            val profissionalId = backStackEntry.arguments?.getString("profissionalId") ?: ""
            ListaPacientesScreen(
                navController = navController,
                profissionalId = profissionalId
            )
        }

        composable("chat_profissional/{profissionalId}/{pacienteId}") { backStackEntry ->
            val profissionalId = backStackEntry.arguments?.getString("profissionalId") ?: ""
            val pacienteId = backStackEntry.arguments?.getString("pacienteId") ?: ""

            ChatProfissionalScreen(
                profissionalId = profissionalId,
                pacienteId = pacienteId
            )
        }

    }
}
