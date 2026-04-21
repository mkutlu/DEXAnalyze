package com.aarw.dexanalyze.data.repository

import com.aarw.dexanalyze.data.api.BodySpecApiService
import com.aarw.dexanalyze.data.model.ScanResult
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async
import retrofit2.HttpException

class ScanRepository(
    private val apiService: BodySpecApiService,
    private val demoMode: Boolean = true
) {
    suspend fun getAllScans(): Result<List<ScanResult>> {
        if (demoMode) return Result.success(DemoData.scans)
        return try {
            val scanList = apiService.listScans(pageSize = 100)?.scanList() ?: emptyList()
            val enriched = coroutineScope {
                scanList.map { scan ->
                    async {
                        scan.copy(
                            composition = try { apiService.getComposition(scan.resultId) } catch (e: Exception) { null },
                            boneDensity = try { apiService.getBoneDensity(scan.resultId) } catch (e: Exception) { null },
                            percentiles = try { apiService.getPercentiles(scan.resultId) } catch (e: Exception) { null },
                            visceralFat = try { apiService.getVisceralFat(scan.resultId) } catch (e: Exception) { null }
                        )
                    }
                }.map { it.await() }
            }
            Result.success(enriched)
        } catch (e: HttpException) {
            val body = e.response()?.errorBody()?.string() ?: ""
            Result.failure(Exception("HTTP ${e.code()}: $body"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
