package com.bmoi.sifipdemo.data.mock

import com.bmoi.sifipdemo.data.model.DeviceSwapResponse
import com.bmoi.sifipdemo.data.model.FraudScoreResponse
import com.bmoi.sifipdemo.data.model.NumberVerifyResponse
import com.bmoi.sifipdemo.data.model.SimSwapResponse

/**
 * Contract for the SIFIP backend. Both the local mock used in this demo
 * (`SifipMockService`) and a future Retrofit-based real client implement
 * the same interface — swapping them out is a single line in
 * [com.bmoi.sifipdemo.BmoiApplication].
 *
 * Endpoint mapping (see sifip-platform/docs/COLLEAGUE-GUIDE.md §5):
 * - [verifyNumber]  → POST /number-verification/vwip/verify
 * - [checkSimSwap]  → POST /sim-swap/vwip/check
 * - [checkDeviceSwap] → POST /device-swap/vwip/check
 * - [scoreFraud]    → POST /fraud-engine/score
 */
interface SifipApi {
    suspend fun verifyNumber(msisdn: String): NumberVerifyResponse
    suspend fun checkSimSwap(msisdn: String): SimSwapResponse
    suspend fun checkDeviceSwap(msisdn: String, deviceId: String): DeviceSwapResponse
    suspend fun scoreFraud(
        msisdn: String,
        recipientIban: String,
        amountMga: Long,
    ): FraudScoreResponse
}
