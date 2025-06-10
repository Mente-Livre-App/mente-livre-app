package com.example.safelife.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safelife.viewModel.AuthViewModel
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle

@Composable
fun LoginScreen(navigateToHome: () -> Unit, navigateToSignup: () -> Unit) {
    // ViewModel responsável pela lógica de autenticação
    val viewModel: AuthViewModel = viewModel()
    val context = LocalContext.current

    // Estados para capturar o que o usuário digita nos campos de login
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Layout principal com centralização e padding
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Permite rolagem
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Título principal
            Text(
                text = "Seja Bem-Vindo",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFFFC43D)
            )

            // Descrição curta da proposta da plataforma
            Text(
                text = "O Mente livre conecta você a profissionais de saúde mental e grupos de apoio.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtítulo
            Text(
                text = "Faça seu Login",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            // Campo de e-mail
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            // Campo de senha com ocultação de caracteres
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            // Link de "Esqueceu a senha?"
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Esqueceu a senha?",
                    color = Color(0xFF726C6C),
                    style = MaterialTheme.typography.bodyMedium
                )

                // Botão que chama a função de redefinição de senha
                TextButton(onClick = {
                    if (email.isNotEmpty()) {
                        viewModel.resetPassword(email) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Digite seu e-mail antes de redefinir a senha",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text(
                        text = "Clique aqui",
                        color = Color(0xFFED474A),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão de login
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(
                            context,
                            "Por favor, preencha todos os campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Realiza login via ViewModel
                        viewModel.login(
                            email,
                            password,
                            onSuccess = {
                                navigateToHome()
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF536DFE))
            ) {
                Text(text = "Acessar", color = Color.White)
            }

            // Link para a tela de cadastro
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = navigateToSignup) {
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color(0xFF726C6C))) {
                            append("Ainda não tem conta? ")
                        }
                        withStyle(style = SpanStyle(color = Color(0xFF00BF9A), fontWeight = FontWeight.Bold)) {
                            append("Cadastre-se")
                        }
                    })
                }
            }
        }
    }
}
