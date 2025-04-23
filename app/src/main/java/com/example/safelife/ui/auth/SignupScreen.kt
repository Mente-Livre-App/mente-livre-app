package com.example.safelife.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safelife.viewModel.AuthViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import com.airbnb.lottie.compose.*
import com.example.safelife.R
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(
    navigateToHome: () -> Unit
) {
    val viewModel: AuthViewModel = viewModel()
    val context = LocalContext.current

    //campos do formulário
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

    if (isLoading) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation_aviao))
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.fillMaxSize()
        )
    } else {
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
                Text(text = "Cadastro", style = MaterialTheme.typography.headlineMedium)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Telefone *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha *") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Senha *") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
                )

                Text("Selecione o tipo de usuário:")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = userType == "Paciente", onClick = { userType = "Paciente" })
                    Text("Paciente")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = userType == "Profissional", onClick = { userType = "Profissional" })
                    Text("Profissional")
                }

                if (isProfessional) {
                    OutlinedTextField(
                        value = crp,
                        onValueChange = { crp = it },
                        label = { Text("CRP *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || email.isBlank() || phoneNumber.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                            Toast.makeText(context, "Todos os campos são obrigatórios.", Toast.LENGTH_SHORT).show()
                        } else if (password != confirmPassword) {
                            Toast.makeText(context, "As senhas não coincidem.", Toast.LENGTH_SHORT).show()
                        } else if (isProfessional && crp.isBlank()) {
                            Toast.makeText(context, "Profissionais precisam informar o CRP.", Toast.LENGTH_SHORT).show()
                        } else {

                            coroutineScope.launch {
                                isLoading = true
//                                delay(1500)
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
                                        navigateToHome() // Agora navega corretamente para a Home
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
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFED474A))
                ) {
                    Text("CONFIRMAR", color = Color.White)
                }
            }
        }
    }
}
