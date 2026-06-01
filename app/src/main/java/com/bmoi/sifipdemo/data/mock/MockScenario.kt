package com.bmoi.sifipdemo.data.mock

/**
 * Scenario drives the deterministic responses of the SIFIP mock service.
 *
 * Switchable at runtime from the login screen (long-press the BMOI logo
 * to reveal the scenario picker) or at build time via:
 * `./gradlew assembleDebug -PsifipScenario=FAIL_SIM_SWAP`
 */
enum class MockScenario(val label: String) {
    /** All login checks succeed, fraud score low → demo "happy path". */
    ALL_OK("Tout OK (score bas)"),

    /** Number-verify fails (e.g. user typed the wrong MSISDN). */
    FAIL_NUMBER_VERIFY("Échec Number Verify"),

    /** Recent SIM swap detected → block login. */
    FAIL_SIM_SWAP("Échec SIM Swap (SIM récemment changée)"),

    /** Unknown device → block login. */
    FAIL_DEVICE_SWAP("Échec Device Swap (appareil inconnu)"),

    /** All checks OK but fraud score high → block transfer. */
    FAIL_FRAUD("OK + score fraude élevé (transaction bloquée)"),

    /**
     * Login checks pass. Fraud decision depends on the transfer amount:
     *  - ≤ 1 000 000 MGA → score bas, virement autorisé
     *  - >  1 000 000 MGA → score élevé, virement bloqué
     *
     * Permet de démontrer dans un seul scénario les deux issues côté banque.
     */
    AMOUNT_BASED("Score fraude selon le montant (seuil 1 000 000 MGA)"),
}
