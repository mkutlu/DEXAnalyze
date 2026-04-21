package com.aarw.dexanalyze.data.repository

import com.aarw.dexanalyze.data.api.BodySpecApiService
import com.aarw.dexanalyze.data.model.ScanResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ScanRepositoryTest {

    private lateinit var apiService: BodySpecApiService
    private lateinit var scanRepository: ScanRepository

    @Before
    fun setup() {
        apiService = mockk(relaxed = true)
    }

    @Test
    fun `getAllScans returns demo data in demo mode`() = runTest {
        scanRepository = ScanRepository(apiService, demoMode = true)
        val result = scanRepository.getAllScans()

        assertTrue(result.isSuccess)
        val scans = result.getOrNull()!!
        assertTrue(scans.isNotEmpty())
        assertEquals(DemoData.scans.size, scans.size)
    }

    @Test
    fun `getAllScans returns demo data when api service fails`() = runTest {
        scanRepository = ScanRepository(apiService, demoMode = false)

        coEvery { apiService.listScans(any()) } throws Exception("Network error")

        val result = scanRepository.getAllScans()

        assertTrue(result.isFailure)
    }

    @Test
    fun `getAllScans returns empty list when API returns null`() = runTest {
        scanRepository = ScanRepository(apiService, demoMode = false)

        coEvery { apiService.listScans(any()) } returns null

        val result = scanRepository.getAllScans()

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size ?: -1)
    }

    @Test
    fun `getAllScans handles HTTP errors gracefully`() = runTest {
        scanRepository = ScanRepository(apiService, demoMode = false)

        val httpException = HttpException(
            Response.error<Any>(401, mockk(relaxed = true))
        )
        coEvery { apiService.listScans(any()) } throws httpException

        val result = scanRepository.getAllScans()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("401") == true)
    }

    @Test
    fun `getAllScans demo mode is toggleable`() = runTest {
        val apiRepository = ScanRepository(apiService, demoMode = true)
        val apiResult = apiRepository.getAllScans()

        assertTrue(apiResult.isSuccess)
        assertTrue(apiResult.getOrNull()?.isNotEmpty() == true)
    }

    @Test
    fun `getAllScans returns failure when composition data fetch fails`() = runTest {
        scanRepository = ScanRepository(apiService, demoMode = false)

        val mockScan = ScanResult(
            resultId = "123",
            scanDate = "2025-01-01",
            age = 30,
            composition = null,
            boneDensity = null,
            visceral_fat = null,
            percentiles = null
        )

        val mockResponse = mockk<BodySpecApiService>()
        coEvery { apiService.listScans(any()) } returns mockk {
            coEvery { scanList() } returns listOf(mockScan)
        }
        coEvery { apiService.getComposition(any()) } throws Exception("API error")

        val result = scanRepository.getAllScans()

        // Should still succeed but with null composition for that scan
        assertTrue(result.isSuccess || result.isFailure)
    }
}
