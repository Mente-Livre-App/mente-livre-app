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
import com.example.safelife.preferences.LGPDPreferences
import com.example.safelife.repository.ChatRepository
import com.example.safelife.ui.agendamento.paciente.AgendamentoPacienteScreen
import com.example.safelife.ui.agendamento.profissional.AgendaProfissionalScreen
import com.example.safelife.ui.agendamento.profissional.AgendamentosConfirmadosScreen
import com.example.safelife.ui.auth.LoginScreen
import com.example.safelife.ui.auth.SignupScreen
import com.example.safelife.ui.chat.paciente.ChatScreen
import com.example.safelife.ui.chat.paciente.ListaProfissionaisScreen
import com.example.safelife.ui.chat.profissional.ChatProfissionalScreen
import com.example.safelife.ui.chat.profissional.ListaPacientesScreen
import com.example.safelife.ui.components.PolicyConsentDialog
import com.example.safelife.ui.feed.FeedScreen
import com.example.safelife.ui.feed.NovaPostagemScreen
import com.example.safelife.ui.feed.PostDetailScreen
import com.example.safelife.ui.home.HomeScreen
import com.example.safelife.ui.info.PoliticaPrivacidadeScreen
import com.example.safelife.ui.theme.SafeLifeTheme
import com.example.safelife.viewModel.AuthViewModel
import com.example.safelife.viewModel.FeedViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeLifeTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val prefs = remember { LGPDPreferences(context) }

    var consentido by remember { mutableStateOf(false) }
    var carregando by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        consentido = prefs.consentimentoFoiDado()
        carregando = false
    }

    if (carregando) {
        LoadingScreen()
    } else {
        val startDestination = when {
            !consentido -> "politica"
            FirebaseAuth.getInstance().currentUser != null -> "home"
            else -> "login"
        }

        NavHost(navController = navController, startDestination = startDestination) {
            composable("politica") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PolicyConsentDialog(
                        onAccept = {
                            coroutineScope.launch {
                                prefs.salvarConsentimento(true)
                                consentido = true
                                navController.navigate("login") {
                                    popUpTo("politica") { inclusive = true }
                                }
                            }
                        },
                        onDecline = {
                            (context as? ComponentActivity)?.finish()
                        },
                        onViewPolicy = {
                            navController.navigate("ver_politica")
                        }
                    )
                }
            }

            composable("ver_politica") {
                PoliticaPrivacidadeScreen(onBack = { navController.popBackStack() })
            }


            composable("login") {
                LoginScreen(
                    navigateToHome = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    navigateToSignup = { navController.navigate("signup") }
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
                val authViewModel: AuthViewModel = viewModel()
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

                HomeScreen(
                    navigateToListaProfissionais = { userId ->
                        navController.navigate("lista_profissionais/$userId")
                    },
                    navigateToListaPacientes = { userId ->
                        navController.navigate("lista_pacientes/$userId")
                    },
                    navigateToConsultas = { userId ->
                        navController.navigate("agendamento/$userId")
                    },
                    navigateToAgendaProfissional = {
                        navController.navigate("agendaProfissional")
                    },
                    navigateToForum = {
                        navController.navigate("feed")
                    },
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
                ChatScreen(currentUserId = currentUserId, otherUserId = recipientId)
            }

            composable("lista_pacientes/{profissionalId}") { backStackEntry ->
                val profissionalId = backStackEntry.arguments?.getString("profissionalId") ?: ""
                ListaPacientesScreen(navController = navController, profissionalId = profissionalId)
            }

            composable("feed") {
                val feedViewModel: FeedViewModel = viewModel()
                FeedScreen(viewModel = feedViewModel, navigateToNovaPostagem = {
                    navController.navigate("nova_postagem")
                })
            }

            composable("nova_postagem") {
                val feedViewModel: FeedViewModel = viewModel()
                NovaPostagemScreen(viewModel = feedViewModel, navigateBack = {
                    navController.popBackStack()
                })
            }

            composable("post_detail/{postId}", arguments = listOf(
                navArgument("postId") { type = NavType.StringType }
            )) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                PostDetailScreen(postId = postId)
            }

            // ✅ Substituído para usar AgendamentoPacienteScreen
            composable("agendamento/{pacienteId}", arguments = listOf(
                navArgument("pacienteId") { type = NavType.StringType }
            )) {
                AgendamentoPacienteScreen(navController = navController)
            }

            composable("agendaProfissional") {
                val profissionalUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                AgendaProfissionalScreen(navController = navController, profissionalId = profissionalUid)
            }

            composable("agendamentosConfirmados") {
                val coroutineScope = rememberCoroutineScope()
                val profissionalUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                AgendamentosConfirmadosScreen(onAbrirChat = { pacienteId, agendamentoId, userType ->
                    coroutineScope.launch {
                        try {
                            val chatRepository = ChatRepository()
                            chatRepository.getOrCreateChatId(
                                user1 = profissionalUid,
                                user2 = pacienteId,
                                userType = userType,
                                agendamentoId = agendamentoId
                            )
                            navController.navigate(
                                "chat_profissional/$profissionalUid/$pacienteId/$agendamentoId/$userType"
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
            }

            composable("agendamentoPaciente") {
                AgendamentoPacienteScreen(navController = navController)
            }

            composable(
                route = "chat_profissional/{profissionalId}/{pacienteId}",
                arguments = listOf(
                    navArgument("profissionalId") { type = NavType.StringType },
                    navArgument("pacienteId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val profissionalId = backStackEntry.arguments?.getString("profissionalId") ?: ""
                val pacienteId = backStackEntry.arguments?.getString("pacienteId") ?: ""
                ChatProfissionalScreen(profissionalId = profissionalId, pacienteId = pacienteId)
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
