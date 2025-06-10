package com.example.safelife.ui.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.safelife.ui.auth.LoginScreen
import com.example.safelife.viewModel.AuthViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<AuthViewModel>(relaxed = true)

    @Test
    fun exibeCamposDeEntrada() {
        composeTestRule.setContent {
            LoginScreen(
                navigateToHome = {},
                navigateToSignup = {}
            )
        }

        composeTestRule.onNodeWithText("E-mail").assertIsDisplayed()
        composeTestRule.onNodeWithText("Senha").assertIsDisplayed()
    }

    @Test
    fun exibeBotaoDeAcesso() {
        composeTestRule.setContent {
            LoginScreen(
                navigateToHome = {},
                navigateToSignup = {}
            )
        }

        composeTestRule.onNodeWithText("Acessar").assertIsDisplayed()
    }

    @Test
    fun exibeLinkDeCadastro() {
        composeTestRule.setContent {
            LoginScreen(
                navigateToHome = {},
                navigateToSignup = {}
            )
        }

        composeTestRule.onNodeWithText("Cadastre-se", substring = true).assertIsDisplayed()
    }

    @Test
    fun cliqueNoBotaoCadastroDisparaNavegacao() {
        var cadastroChamado = false

        composeTestRule.setContent {
            LoginScreen(
                navigateToHome = {},
                navigateToSignup = { cadastroChamado = true }
            )
        }

        composeTestRule.onNodeWithText("Cadastre-se", substring = true).performClick()
        assert(cadastroChamado)
    }
}
