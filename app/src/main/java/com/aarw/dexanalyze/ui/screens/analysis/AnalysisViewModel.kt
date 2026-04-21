package com.aarw.dexanalyze.ui.screens.analysis

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

data class AnalysisUiState(
    val isLoading: Boolean = true,
    val scans: List<ScanResult> = emptyList(),
    val selectedIndex: Int = 0,
    val error: String? = null
) {
    val selectedScan: ScanResult? get() = scans.getOrNull(selectedIndex)
}

class AnalysisViewModel(private val repository: ScanRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisUiState())
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    init {
        loadScans()
    }

    fun selectScan(index: Int) {
        _uiState.update { it.copy(selectedIndex = index) }
    }

    private fun loadScans() {
        viewModelScope.launch {
            repository.getAllScans()
                .onSuccess { scans ->
                    _uiState.update { it.copy(isLoading = false, scans = scans) }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isLoading = false, error = err.message) }
                }
        }
    }

    class Factory(private val repository: ScanRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AnalysisViewModel(repository) as T
    }
}
