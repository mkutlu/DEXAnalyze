package com.aarw.dexanalyze.ui.screens.login

import com.aarw.dexanalyze.data.auth.AuthRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        authRepository = mockk(relaxed = true)
        viewModel = LoginViewModel(authRepository)
    }

    @Test
    fun `viewModel is created successfully`() {
        assertTrue(viewModel is LoginViewModel)
    }

    @Test
    fun `uiState is initialized to Idle`() {
        val currentState = viewModel.uiState.value
        assertEquals(LoginUiState.Idle, currentState)
    }

    @Test
    fun `startAuth generates auth URL and transitions to ShowWebView`() {
        every { authRepository.buildAuthUrl() } returns Pair(
            "https://auth.example.com/auth?code_challenge=xyz",
            "verifier_code"
        )

        viewModel.startAuth()

        val state = viewModel.uiState.value
        assertTrue(state is LoginUiState.ShowWebView)
        assertEquals("https://auth.example.com/auth?code_challenge=xyz", (state as LoginUiState.ShowWebView).authUrl)
        assertEquals("verifier_code", state.codeVerifier)
    }

    @Test
    fun `cancelAuth sets state to Idle`() {
        every { authRepository.buildAuthUrl() } returns Pair("https://url", "verifier")
        viewModel.startAuth()

        viewModel.cancelAuth()

        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `startAuth generates different verifiers each time`() {
        every { authRepository.buildAuthUrl() }
            .returnsMany(
                Pair("https://url1", "verifier1"),
                Pair("https://url2", "verifier2")
            )

        viewModel.startAuth()
        val state1 = viewModel.uiState.value as LoginUiState.ShowWebView

        viewModel.startAuth()
        val state2 = viewModel.uiState.value as LoginUiState.ShowWebView

        assertTrue(state1.codeVerifier.isNotEmpty())
        assertTrue(state2.codeVerifier.isNotEmpty())
    }

    @Test
    fun `LoginUiState has correct subclasses`() {
        val idle = LoginUiState.Idle
        val loading = LoginUiState.Loading
        val error = LoginUiState.Error("test error")
        val showWeb = LoginUiState.ShowWebView("https://url", "verifier")

        assertTrue(idle is LoginUiState)
        assertTrue(loading is LoginUiState)
        assertTrue(error is LoginUiState)
        assertTrue(showWeb is LoginUiState)
    }

    @Test
    fun `Error state contains error message`() {
        val errorMsg = "Authentication failed"
        val errorState = LoginUiState.Error(errorMsg)

        assertEquals(errorMsg, errorState.message)
    }

    @Test
    fun `ShowWebView state contains auth URL and verifier`() {
        val url = "https://auth.example.com"
        val verifier = "test_verifier"
        val state = LoginUiState.ShowWebView(url, verifier)

        assertEquals(url, state.authUrl)
        assertEquals(verifier, state.codeVerifier)
    }
}
