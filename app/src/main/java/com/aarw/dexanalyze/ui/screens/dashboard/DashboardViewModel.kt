package com.aarw.dexanalyze.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aarw.dexanalyze.data.model.ScanResult
import com.aarw.dexanalyze.data.repository.ScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = true,
    val scan: ScanResult? = null,
    val previousScan: ScanResult? = null,
    val history: List<ScanResult> = emptyList(),
    val error: String? = null
)

class DashboardViewModel(private val repository: ScanRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadScans()
    }

    private fun loadScans() {
        viewModelScope.launch {
            repository.getAllScans()
                .onSuccess { scans ->
                    val allScans = scans.orEmpty()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            scan = allScans.firstOrNull(),
                            previousScan = allScans.getOrNull(1),
                            history = allScans
                        )
                    }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isLoading = false, error = err.message) }
                }
        }
    }

    class Factory(private val repository: ScanRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DashboardViewModel(repository) as T
    }
}
