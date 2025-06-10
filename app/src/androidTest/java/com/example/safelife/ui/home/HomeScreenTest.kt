package com.example.safelife.ui.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.safelife.MainActivity
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun exibeTodosOsBotoes() {
        composeTestRule.setContent {
            HomeScreen(
                navigateToListaProfissionais = {},
                navigateToListaPacientes = {},
                navigateToConsultas = {},
                navigateToAgendaProfissional = {}, // ✅ novo parâmetro
                navigateToForum = {},
                navigateToLogin = {}
            )
        }

        composeTestRule.onNodeWithText("Agendar Consulta").assertIsDisplayed()
        composeTestRule.onNodeWithText("Chat de Suporte").assertIsDisplayed()
        composeTestRule.onNodeWithText("Feed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sair").assertIsDisplayed()
    }

    @Test
    fun botaoFeed_disparaNavegacao() {
        var navegou = false

        composeTestRule.setContent {
            HomeScreen(
                navigateToListaProfissionais = {},
                navigateToListaPacientes = {},
                navigateToConsultas = {},
                navigateToAgendaProfissional = {}, // ✅ novo parâmetro

                navigateToForum = { navegou = true },
                navigateToLogin = {}
            )
        }

        composeTestRule.onNodeWithText("Feed").performClick()
        assert(navegou)
    }

    @Test
    fun botaoSair_disparaLogout() {
        var navegouLogin = false

        composeTestRule.setContent {
            HomeScreen(
                navigateToListaProfissionais = {},
                navigateToListaPacientes = {},
                navigateToConsultas = {},
                navigateToAgendaProfissional = {}, // ✅ novo parâmetro

                navigateToForum = {},
                navigateToLogin = { navegouLogin = true }
            )
        }

        composeTestRule.onNodeWithText("Sair").performClick()
        assert(navegouLogin)
    }
}
