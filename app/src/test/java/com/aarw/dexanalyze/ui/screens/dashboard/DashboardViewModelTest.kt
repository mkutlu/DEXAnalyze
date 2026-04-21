package com.aarw.dexanalyze.ui.screens.dashboard

import com.aarw.dexanalyze.data.model.ScanResult
import com.aarw.dexanalyze.data.repository.ScanRepository
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DashboardViewModelTest {

    private lateinit var scanRepository: ScanRepository

    @Before
    fun setup() {
        scanRepository = mockk(relaxed = true)
    }

    @Test
    fun `DashboardUiState is initialized with correct defaults`() {
        val state = DashboardUiState()

        assertTrue(state.isLoading)
        assertNull(state.scan)
        assertNull(state.previousScan)
        assertEquals(0, state.history.size)
        assertNull(state.error)
    }

    @Test
    fun `DashboardUiState can be updated with scans`() {
        val scans = listOf(
            ScanResult(resultId = "1", scanDate = "2025-01-01"),
            ScanResult(resultId = "2", scanDate = "2024-12-01")
        )

        val state = DashboardUiState(
            isLoading = false,
            scan = scans.firstOrNull(),
            previousScan = scans.getOrNull(1),
            history = scans,
            error = null
        )

        assertFalse(state.isLoading)
        assertEquals("1", state.scan?.resultId)
        assertEquals("2", state.previousScan?.resultId)
        assertEquals(2, state.history.size)
        assertNull(state.error)
    }

    @Test
    fun `DashboardUiState can set error`() {
        val errorMsg = "Network error"
        val state = DashboardUiState(
            isLoading = false,
            error = errorMsg
        )

        assertEquals(errorMsg, state.error)
        assertTrue(state.history.isEmpty())
    }

    @Test
    fun `DashboardUiState with single scan`() {
        val scan = ScanResult(resultId = "1", scanDate = "2025-01-01")
        val state = DashboardUiState(
            isLoading = false,
            scan = scan,
            previousScan = null,
            history = listOf(scan)
        )

        assertEquals("1", state.scan?.resultId)
        assertNull(state.previousScan)
        assertEquals(1, state.history.size)
    }

    @Test
    fun `DashboardUiState copy preserves existing values`() {
        val originalScans = listOf(
            ScanResult(resultId = "1", scanDate = "2025-01-01")
        )
        val original = DashboardUiState(
            isLoading = true,
            scan = originalScans.first(),
            history = originalScans
        )

        val updated = original.copy(isLoading = false)

        assertFalse(updated.isLoading)
        assertEquals(original.scan, updated.scan)
        assertEquals(original.history.size, updated.history.size)
    }

    @Test
    fun `ScanResult can be created with minimal data`() {
        val scan = ScanResult(
            resultId = "test-id",
            scanDate = "2025-01-01"
        )

        assertEquals("test-id", scan.resultId)
        assertEquals("2025-01-01", scan.scanDate)
        assertNull(scan.composition)
        assertNull(scan.boneDensity)
    }

    @Test
    fun `DashboardUiState handles empty history`() {
        val state = DashboardUiState(
            isLoading = false,
            history = emptyList()
        )

        assertTrue(state.history.isEmpty())
        assertNull(state.scan)
        assertNull(state.previousScan)
    }
}
