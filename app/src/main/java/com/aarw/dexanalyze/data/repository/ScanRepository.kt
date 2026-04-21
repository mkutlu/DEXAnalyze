package com.aarw.dexanalyze.data.repository

import com.aarw.dexanalyze.data.api.BodySpecApiService
import com.aarw.dexanalyze.data.model.ScanResult
import com.aarw.dexanalyze.util.Logger
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async
import retrofit2.HttpException

class ScanRepository(
    private val apiService: BodySpecApiService,
    private val demoMode: Boolean = true
) {
    private val TAG = "ScanRepository"

    suspend fun getAllScans(): Result<List<ScanResult>> {
        if (demoMode) {
            Logger.d(TAG, "Loading demo scan data")
            return Result.success(DemoData.scans)
        }

        return try {
            Logger.d(TAG, "Fetching scans from API")
            val scanList = apiService.listScans(pageSize = 100)?.scanList() ?: emptyList()
            Logger.d(TAG, "Fetched ${scanList.size} scans, enriching with additional data")

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
            Logger.i(TAG, "Successfully loaded ${enriched.size} enriched scans")
            Result.success(enriched)
        } catch (e: HttpException) {
            Logger.w(TAG, "HTTP error while fetching scans: ${e.code()}")
            val body = e.response()?.errorBody()?.string() ?: ""
            Result.failure(Exception("HTTP ${e.code()}: $body"))
        } catch (e: Exception) {
            Logger.e(TAG, "Failed to fetch scans", e)
            Result.failure(e)
        }
    }
}
