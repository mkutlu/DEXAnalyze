package com.aarw.dexanalyze.data.api

import com.aarw.dexanalyze.data.model.BoneDensity
import com.aarw.dexanalyze.data.model.Composition
import com.aarw.dexanalyze.data.model.Percentiles
import com.aarw.dexanalyze.data.model.Rmr
import com.aarw.dexanalyze.data.model.ScanInfo
import com.aarw.dexanalyze.data.model.ScanListResponse
import com.aarw.dexanalyze.data.model.ScanResult
import com.aarw.dexanalyze.data.model.VisceralFat
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BodySpecApiService {

    @GET("users/me/results/")
    suspend fun listScans(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 100,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): ScanListResponse?

    @GET("users/me/results/{result_id}")
    suspend fun getResult(@Path("result_id") resultId: String): ScanResult?

    @GET("users/me/results/{result_id}/dexa/scan-info")
    suspend fun getScanInfo(@Path("result_id") resultId: String): ScanInfo?

    @GET("users/me/results/{result_id}/dexa/composition")
    suspend fun getComposition(@Path("result_id") resultId: String): Composition?

    @GET("users/me/results/{result_id}/dexa/bone-density")
    suspend fun getBoneDensity(@Path("result_id") resultId: String): BoneDensity?

    @GET("users/me/results/{result_id}/dexa/percentiles")
    suspend fun getPercentiles(@Path("result_id") resultId: String): Percentiles?

    @GET("users/me/results/{result_id}/dexa/visceral-fat")
    suspend fun getVisceralFat(@Path("result_id") resultId: String): VisceralFat?

    @GET("users/me/results/{result_id}/dexa/rmr")
    suspend fun getRmr(@Path("result_id") resultId: String): Rmr?
}
