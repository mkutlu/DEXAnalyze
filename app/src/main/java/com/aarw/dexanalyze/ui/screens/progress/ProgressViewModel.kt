package com.aarw.dexanalyze.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aarw.dexanalyze.data.model.ScanResult
import com.aarw.dexanalyze.data.repository.ScanRepository
import com.aarw.dexanalyze.ui.components.ChartEntry
import com.aarw.dexanalyze.ui.components.StackedBarEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProgressUiState(
    val isLoading: Boolean = true,
    val scans: List<ScanResult> = emptyList(),
    val weightEntries: List<ChartEntry> = emptyList(),
    val fatPctEntries: List<ChartEntry> = emptyList(),
    val vatEntries: List<ChartEntry> = emptyList(),
    val stackedBarEntries: List<StackedBarEntry> = emptyList(),
    val error: String? = null
)

class ProgressViewModel(private val repository: ScanRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        loadScans()
    }

    private fun loadScans() {
        viewModelScope.launch {
            repository.getAllScans()
                .onSuccess { scans ->
                    val sorted = scans.sortedBy { it.scanDate }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            scans = sorted,
                            weightEntries = sorted.map { s ->
                                ChartEntry(s.scanDate.take(10).takeLast(5), (s.composition?.total?.totalMassKg ?: 0.0).toFloat())
                            },
                            fatPctEntries = sorted.map { s ->
                                ChartEntry(s.scanDate.take(10).takeLast(5), (s.composition?.total?.regionFatPct ?: 0.0).toFloat())
                            },
                            vatEntries = sorted.map { s ->
                                ChartEntry(s.scanDate.take(10).takeLast(5), (s.visceralFat?.vatMassKg ?: 0.0).toFloat())
                            },
                            stackedBarEntries = sorted.map { s ->
                                StackedBarEntry(
                                    s.scanDate.take(10).substring(5),
                                    (s.composition?.total?.fatMassKg ?: 0.0).toFloat(),
                                    (s.composition?.total?.leanMassKg ?: 0.0).toFloat()
                                )
                            }
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
            ProgressViewModel(repository) as T
    }
}
