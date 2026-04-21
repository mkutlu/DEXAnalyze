package com.aarw.dexanalyze.ui.screens.dashboard

import app.cash.turbine.test
import com.aarw.dexanalyze.data.model.ScanResult
import com.aarw.dexanalyze.data.repository.ScanRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DashboardViewModelTest {

    private lateinit var scanRepository: ScanRepository
    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setup() {
        scanRepository = mockk(relaxed = true)
        viewModel = DashboardViewModel(scanRepository)
    }

    @Test
    fun `initial state shows loading`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isLoading)
            assertTrue(state.scans.isEmpty())
            assertEquals(null, state.error)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `loadScans updates state with scans on success`() = runTest {
        val mockScans = listOf(
            ScanResult(resultId = "1", scanDate = "2025-01-01"),
            ScanResult(resultId = "2", scanDate = "2024-12-01")
        )
        coEvery { scanRepository.getAllScans() } returns Result.success(mockScans)

        viewModel.loadScans()

        viewModel.uiState.test {
            skipItems(1) // Skip initial loading state
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(2, state.scans.size)
            assertEquals("1", state.scans[0].resultId)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `loadScans sets error on failure`() = runTest {
        val error = Exception("Network error")
        coEvery { scanRepository.getAllScans() } returns Result.failure(error)

        viewModel.loadScans()

        viewModel.uiState.test {
            skipItems(1) // Skip initial loading state
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.error?.contains("Network error") == true)
            assertTrue(state.scans.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `selectScan updates selected scan`() {
        val scan = ScanResult(resultId = "123", scanDate = "2025-01-01")
        viewModel.selectScan(scan)

        viewModel.uiState.test {
            skipItems(1) // Skip loading state
            val state = awaitItem()
            assertNotNull(state.selectedScan)
            assertEquals("123", state.selectedScan?.resultId)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `sortScans by date descending`() = runTest {
        val mockScans = listOf(
            ScanResult(resultId = "1", scanDate = "2024-12-01"),
            ScanResult(resultId = "2", scanDate = "2025-01-01"),
            ScanResult(resultId = "3", scanDate = "2024-11-01")
        )
        coEvery { scanRepository.getAllScans() } returns Result.success(mockScans)

        viewModel.loadScans()

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()
            // Scans should be sorted by date (assuming descending order)
            assertEquals("2", state.scans[0].resultId) // 2025-01-01
            cancelAndConsumeRemainingEvents()
        }
    }
}
