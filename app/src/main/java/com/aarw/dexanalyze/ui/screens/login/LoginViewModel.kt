package com.aarw.dexanalyze.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aarw.dexanalyze.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    data class ShowWebView(val authUrl: String, val codeVerifier: String) : LoginUiState()
    object Loading : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun startAuth() {
        val (url, verifier) = authRepository.buildAuthUrl()
        _uiState.value = LoginUiState.ShowWebView(url, verifier)
    }

    fun handleCode(code: String, codeVerifier: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            authRepository.exchangeCode(code, codeVerifier)
                .onSuccess { _uiState.value = LoginUiState.Idle }
                .onFailure { _uiState.value = LoginUiState.Error(it.message ?: "Authentication failed") }
        }
    }

    fun cancelAuth() {
        _uiState.value = LoginUiState.Idle
    }

    class Factory(private val repo: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = LoginViewModel(repo) as T
    }
}
