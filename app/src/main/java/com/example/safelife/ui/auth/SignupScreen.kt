package com.example.safelife.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.*
import com.example.safelife.R
import com.example.safelife.viewModel.AuthViewModel
import kotlinx.coroutines.launch


@Composable
fun SignupScreen(
    navigateToHome: () -> Unit,
    authViewModel: AuthViewModel = viewModel() // ViewModel pode ser injetado para facilitar testes
) {
    // Encapsula a lógica em um composable reutilizável e testável
    SignupScreenTestable(
        navigateToHome = navigateToHome,
        viewModel = authViewModel
    )
}

@Composable
fun SignupScreenTestable(
    navigateToHome: () -> Unit,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current

    // Estados para capturar as entradas do formulário
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("Paciente") }
    var crp by remember { mutableStateOf("") }
    val isProfessional = userType == "Profissional"
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Animação de carregamento enquanto a requisição é processada
    if (isLoading) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation_aviao))
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        // Formulário de cadastro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Título da tela
                Text(text = "Cadastro", style = MaterialTheme.typography.headlineMedium)

                // Campo: Nome
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome *") },
                    modifier = Modifier.fillMaxWidth().testTag("campoNome"),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                // Campo: E-mail
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail *") },
                    modifier = Modifier.fillMaxWidth().testTag("campoEmail"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )

                // Campo: Telefone
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Telefone *") },
                    modifier = Modifier.fillMaxWidth().testTag("campoTelefone"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
                )

                // Campo: Senha
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha *") },
                    modifier = Modifier.fillMaxWidth().testTag("campoSenha"),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
                )

                // Campo: Confirmar Senha
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Senha *") },
                    modifier = Modifier.fillMaxWidth().testTag("campoConfirmarSenha"),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
                )

                // Seletor de tipo de usuário (Paciente ou Profissional)
                Text("Selecione o tipo de usuário:")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = userType == "Paciente",
                        onClick = { userType = "Paciente" },
                        modifier = Modifier.testTag("radioPaciente")
                    )
                    Text("Paciente")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = userType == "Profissional",
                        onClick = { userType = "Profissional" },
                        modifier = Modifier.testTag("radioProfissional")
                    )
                    Text("Profissional")
                }

                // Campo CRP (aparece apenas se for profissional)
                if (isProfessional) {
                    OutlinedTextField(
                        value = crp,
                        onValueChange = { crp = it },
                        label = { Text("CRP *") },
                        modifier = Modifier.fillMaxWidth().testTag("campoCRP"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botão de confirmação do cadastro
                Button(
                    onClick = {
                        // Validação dos campos antes de prosseguir
                        if (name.isBlank() || email.isBlank() || phoneNumber.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                            Toast.makeText(context, "Todos os campos são obrigatórios.", Toast.LENGTH_SHORT).show()
                        } else if (password != confirmPassword) {
                            Toast.makeText(context, "As senhas não coincidem.", Toast.LENGTH_SHORT).show()
                        } else if (isProfessional && crp.isBlank()) {
                            Toast.makeText(context, "Profissionais precisam informar o CRP.", Toast.LENGTH_SHORT).show()
                        } else {
                            // Executa o cadastro no ViewModel
                            coroutineScope.launch {
                                isLoading = true
                                viewModel.signup(
                                    name = name,
                                    email = email,
                                    phone = phoneNumber,
                                    userType = userType,
                                    crp = crp,
                                    password = password,
                                    confirmPassword = confirmPassword,
                                    onSuccess = {
                                        isLoading = false
                                        navigateToHome()
                                    },
                                    onError = { error ->
                                        isLoading = false
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("botaoConfirmar"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFED474A))
                ) {
                    Text("CONFIRMAR", color = Color.White)
                }
            }
        }
    }
}
