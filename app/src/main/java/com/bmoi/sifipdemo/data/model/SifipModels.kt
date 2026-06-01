package com.bmoi.sifipdemo.data.model

/**
 * SIFIP API response envelope — kept intentionally close to the real schemas
 * documented in sifip-platform/docs/COLLEAGUE-GUIDE.md so the mock can be
 * swapped for the real HTTP client without changing UI code.
 */
sealed interface SifipCheckResult {
    val checkName: String

    data class Ok(
        override val checkName: String,
        val message: String,
    ) : SifipCheckResult

    data class Failed(
        override val checkName: String,
        val reason: String,
    ) : SifipCheckResult
}

data class NumberVerifyResponse(
    val verified: Boolean,
    val msisdn: String,
    val message: String,
)

data class SimSwapResponse(
    val swapped: Boolean,
    val lastSwapDate: String?,
    val message: String,
)

data class DeviceSwapResponse(
    val knownDevice: Boolean,
    val deviceFingerprint: String,
    val message: String,
)

/**
 * Fraud Engine result. Decision mirrors the real fraud-engine output
 * (APPROVE / REVIEW / CHALLENGE / REJECT).
 */
data class FraudScoreResponse(
    val score: Int, // 0..100
    val decision: FraudDecision,
    val reasons: List<String>,
)

enum class FraudDecision {
    APPROVE,
    REVIEW,
    CHALLENGE,
    REJECT,
}
