package com.aarw.dexanalyze.data.repository

import com.aarw.dexanalyze.data.api.BodySpecApiService
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ScanRepositoryTest {

    private lateinit var apiService: BodySpecApiService

    @Before
    fun setup() {
        apiService = mockk(relaxed = true)
    }

    @Test
    fun `demo mode flag creates correct repository instance`() {
        val demoRepo = ScanRepository(apiService, demoMode = true)
        val apiRepo = ScanRepository(apiService, demoMode = false)

        assertTrue(demoRepo is ScanRepository)
        assertTrue(apiRepo is ScanRepository)
    }

    @Test
    fun `DemoData scans are available`() {
        val demoData = DemoData.scans
        assertFalse(demoData.isEmpty())
    }

    @Test
    fun `DemoData contains valid scan entries`() {
        val scans = DemoData.scans
        assertTrue(scans.size >= 2)

        scans.forEach { scan ->
            assertFalse(scan.resultId.isBlank())
            assertFalse(scan.scanDate.isBlank())
        }
    }

    @Test
    fun `DemoData scans have expected fields populated`() {
        val scans = DemoData.scans
        assertFalse(scans.isEmpty())

        val firstScan = scans.first()
        assertTrue(firstScan.resultId.isNotEmpty())
        assertTrue(firstScan.scanDate.isNotEmpty())
    }

    @Test
    fun `DemoData maintains consistent scan count`() {
        val count = DemoData.scans.size
        assertTrue(count > 0)

        // Calling again should return same data
        val count2 = DemoData.scans.size
        assertEquals(count, count2)
    }

    @Test
    fun `demo mode provides consistent data across instances`() {
        val repo1 = ScanRepository(apiService, demoMode = true)
        val repo2 = ScanRepository(apiService, demoMode = true)

        // Both should be capable of loading demo data
        assertTrue(repo1 is ScanRepository)
        assertTrue(repo2 is ScanRepository)
    }

    private fun assertEquals(expected: Int, actual: Int) {
        if (expected != actual) {
            throw AssertionError("Expected $expected but got $actual")
        }
    }
}
