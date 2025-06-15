package com.example.safelife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.safelife.ui.auth.LoginScreen
import com.example.safelife.ui.auth.SignupScreen
import com.example.safelife.ui.chat.paciente.ChatScreen
import com.example.safelife.ui.chat.paciente.ListaProfissionaisScreen
import com.example.safelife.ui.chat.profissional.ChatProfissionalScreen
import com.example.safelife.ui.chat.profissional.ListaPacientesScreen
import com.example.safelife.ui.feed.FeedScreen
import com.example.safelife.ui.feed.NovaPostagemScreen
import com.example.safelife.ui.feed.PostDetailScreen
import com.example.safelife.ui.home.HomeScreen
import com.example.safelife.ui.theme.SafeLifeTheme
import com.example.safelife.viewModel.AuthViewModel
import com.example.safelife.viewModel.FeedViewModel
import com.example.safelife.ui.agendamento.AgendamentoScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.Alignment
import androidx.navigation.compose.rememberNavController

import androidx.compose.ui.Modifier

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
    val feedViewModel: FeedViewModel = viewModel()

    val isUserLoggedIn by authViewModel.isUserLoggedIn.observeAsState(null)
    val isCheckingAuth by authViewModel.isCheckingAuth.observeAsState(true)

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
                    if (isUserLoggedIn == true) {
                        navController.navigate("home")
                    }
                },
                navigateToSignup = {
                    navController.navigate("signup")
                }
            )
        }
        composable("signup") {
            SignupScreen(
                navigateToHome = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
            HomeScreen(
                navigateToListaProfissionais = { userId -> navController.navigate("lista_profissionais/$userId") },
                navigateToListaPacientes = { userId -> navController.navigate("lista_pacientes/$userId") },
                navigateToConsultas = { userId -> navController.navigate("agendamento/$userId") },
                navigateToAgendaProfissional = {},
                navigateToForum = { navController.navigate("feed") },
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
                pacienteId = pacienteId,
                navController = TODO(),
                agendamentoId = TODO(),
                userType = TODO()
            )
        }
        composable("feed") {
            FeedScreen(
                viewModel = feedViewModel,
                navigateToNovaPostagem = { navController.navigate("nova_postagem") }
            )
        }
        composable("nova_postagem") {
            NovaPostagemScreen(
                viewModel = feedViewModel,
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "post_detail/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PostDetailScreen(postId = postId)
        }
        composable(
            route = "agendamento/{pacienteId}",
            arguments = listOf(navArgument("pacienteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pacienteId = backStackEntry.arguments?.getString("pacienteId") ?: ""
            AgendamentoScreen(
                pacienteId = pacienteId,
                navigateBack = { navController.popBackStack() }
            )
        }
        // Opcional: futura rota para a agenda do profissional
        composable(
            route = "agenda_profissional/{profissionalId}",
            arguments = listOf(navArgument("profissionalId") { type = NavType.StringType })
        ) {
            // Placeholder caso queira navegar para uma tela de agenda
        }
    }
}
