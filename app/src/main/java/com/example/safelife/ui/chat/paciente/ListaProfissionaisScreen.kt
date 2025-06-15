package com.example.safelife.ui.chat.paciente

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safelife.viewModel.ListaProfissionaisViewModel
import androidx.compose.foundation.layout.statusBarsPadding

@Composable
fun ListaProfissionaisScreen(
    navController: NavController? = null,
    currentUserId: String,
    navigateToChat: (String, String) -> Unit,
    viewModel: ListaProfissionaisViewModel = viewModel()
) {
    val profissionais by viewModel.profissionais.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.carregarProfissionaisPorTipo("profissional")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ✅ Conteúdo principal com padding no topo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 72.dp) // espaço para o botão
        ) {
            Text(
                text = "Escolha seu Profissional",
                color = Color(0xFF9C27B0),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Text(
                text = "Lista de Profissionais",
                color = Color(0xFF9C27B0),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .testTag("loading-indicator")
                        )
                    }
                }

                profissionais.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Nenhum profissional encontrado.",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .testTag("empty-message"),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(profissionais) { profissional ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navigateToChat(currentUserId, profissional.uid)
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                                elevation = CardDefaults.cardElevation(6.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = profissional.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF4A148C)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = profissional.email,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF6A1B9A)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ✅ Botão voltar no topo com padding e zIndex para sempre aparecer
        IconButton(
            onClick = { navController?.popBackStack() },
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 8.dp)
                .size(48.dp)
                .zIndex(2f) // garante visibilidade acima da coluna
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = Color(0xFF9C27B0),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
