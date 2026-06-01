package com.bmoi.sifipdemo.data.mock

import com.bmoi.sifipdemo.data.model.DeviceSwapResponse
import com.bmoi.sifipdemo.data.model.FraudDecision
import com.bmoi.sifipdemo.data.model.FraudScoreResponse
import com.bmoi.sifipdemo.data.model.NumberVerifyResponse
import com.bmoi.sifipdemo.data.model.SimSwapResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * In-memory implementation of [SifipApi]. Reproduces the latency profile
 * of the real APIs (200–700 ms) so the UI animations feel realistic in
 * front of the bank.
 *
 * No network is required; perfect for offline demos on a Samsung device.
 *
 * The current scenario is observable as a [StateFlow] so the UI can update
 * the hidden "scenario picker" reactively.
 */
class SifipMockService(initial: MockScenario = MockScenario.ALL_OK) : SifipApi {

    private val _scenario = MutableStateFlow(initial)
    val scenario: StateFlow<MockScenario> = _scenario

    fun setScenario(next: MockScenario) {
        _scenario.value = next
    }

    override suspend fun verifyNumber(msisdn: String): NumberVerifyResponse {
        delay(NUMBER_VERIFY_LATENCY_MS)
        val ok = _scenario.value != MockScenario.FAIL_NUMBER_VERIFY
        return NumberVerifyResponse(
            verified = ok,
            msisdn = msisdn,
            message = if (ok) {
                "Numéro vérifié auprès de l'opérateur"
            } else {
                "Le numéro fourni ne correspond pas au compte"
            },
        )
    }

    override suspend fun checkSimSwap(msisdn: String): SimSwapResponse {
        delay(SIM_SWAP_LATENCY_MS)
        val swapped = _scenario.value == MockScenario.FAIL_SIM_SWAP
        return SimSwapResponse(
            swapped = swapped,
            lastSwapDate = if (swapped) "2026-05-30" else null,
            message = if (swapped) {
                "SIM changée il y a moins de 72 h — accès bloqué"
            } else {
                "Aucun changement de SIM récent"
            },
        )
    }

    override suspend fun checkDeviceSwap(
        msisdn: String,
        deviceId: String,
    ): DeviceSwapResponse {
        delay(DEVICE_SWAP_LATENCY_MS)
        val known = _scenario.value != MockScenario.FAIL_DEVICE_SWAP
        return DeviceSwapResponse(
            knownDevice = known,
            deviceFingerprint = deviceId,
            message = if (known) {
                "Appareil reconnu"
            } else {
                "Appareil inconnu — second facteur requis"
            },
        )
    }

    override suspend fun scoreFraud(
        msisdn: String,
        recipientIban: String,
        amountMga: Long,
    ): FraudScoreResponse {
        delay(FRAUD_SCORE_LATENCY_MS)
        return when (_scenario.value) {
            MockScenario.FAIL_FRAUD -> FraudScoreResponse(
                score = 87,
                decision = FraudDecision.REJECT,
                reasons = listOf(
                    "Authentification comportementale : incohérente",
                    "Vérification historique : opération atypique",
                    "Bénéficiaire jamais utilisé",
                    "Montant > 10× la moyenne mensuelle",
                    "Activité inhabituelle détectée par le modèle IA",
                ),
            )
            MockScenario.AMOUNT_BASED -> if (amountMga > AMOUNT_FRAUD_THRESHOLD_MGA) {
                FraudScoreResponse(
                    score = 76,
                    decision = FraudDecision.REJECT,
                    reasons = listOf(
                        "Authentification comportementale : OK",
                        "Vérification historique : montant inhabituel pour ce profil",
                        "Bénéficiaire connu",
                        "Montant > seuil critique (1 000 000 MGA)",
                    ),
                )
            } else {
                FraudScoreResponse(
                    score = 18,
                    decision = FraudDecision.APPROVE,
                    reasons = listOf(
                        "Authentification comportementale : OK",
                        "Vérification historique : conforme",
                        "Bénéficiaire connu",
                        "Montant cohérent avec l'historique",
                    ),
                )
            }
            else -> FraudScoreResponse(
                score = 12,
                decision = FraudDecision.APPROVE,
                reasons = listOf(
                    "Authentification comportementale : OK",
                    "Vérification historique : conforme",
                    "Bénéficiaire connu",
                    "Montant cohérent avec l'historique",
                ),
            )
        }
    }

    private companion object {
        const val NUMBER_VERIFY_LATENCY_MS = 650L
        const val SIM_SWAP_LATENCY_MS = 550L
        const val DEVICE_SWAP_LATENCY_MS = 450L
        const val FRAUD_SCORE_LATENCY_MS = 900L

        /** Threshold for the AMOUNT_BASED scenario (transfer ≤ → OK, > → blocked). */
        const val AMOUNT_FRAUD_THRESHOLD_MGA = 1_000_000L
    }
}
