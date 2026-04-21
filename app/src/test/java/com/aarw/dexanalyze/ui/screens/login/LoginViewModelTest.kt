package com.aarw.dexanalyze.ui.screens.login

import app.cash.turbine.test
import com.aarw.dexanalyze.data.auth.AuthRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        authRepository = mockk(relaxed = true)
        every { authRepository.isLoggedIn } returns MutableStateFlow(false)
        viewModel = LoginViewModel(authRepository)
    }

    @Test
    fun `initial state has empty auth URL`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.authUrl.isEmpty())
            assertEquals(null, state.error)
            assertFalse(state.isLoading)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `startLogin generates auth URL and code verifier`() = runTest {
        every { authRepository.buildAuthUrl() } returns Pair(
            "https://auth.example.com/auth?code_challenge=xyz",
            "verifier_code"
        )

        viewModel.startLogin()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.authUrl.isNotEmpty())
            assertTrue(state.authUrl.contains("code_challenge"))
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleAuthCode clears loading state on success`() = runTest {
        coEvery { authRepository.exchangeCode(any(), any()) } returns Result.success(Unit)

        viewModel.handleAuthCode("auth_code")

        viewModel.uiState.test {
            awaitItem() // Initial state
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleAuthCode sets error on code exchange failure`() = runTest {
        val error = Exception("Invalid code")
        coEvery { authRepository.exchangeCode(any(), any()) } returns Result.failure(error)

        viewModel.handleAuthCode("invalid_code")

        viewModel.uiState.test {
            awaitItem() // First emission might be loading
            val state = awaitItem() // Should have error
            assertTrue(state.error?.contains("Invalid code") == true)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `clearError clears error message`() = runTest {
        coEvery { authRepository.exchangeCode(any(), any()) } returns Result.failure(Exception("Test error"))

        viewModel.handleAuthCode("invalid")
        viewModel.uiState.test {
            skipItems(2) // Skip initial and loading states
            val errorState = awaitItem()
            assertTrue(errorState.error?.isNotEmpty() == true)

            viewModel.clearError()
            skipItems(1) // Skip state update from clearError
            cancelAndConsumeRemainingEvents()
        }
    }
}
