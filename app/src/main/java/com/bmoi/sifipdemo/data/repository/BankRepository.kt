package com.bmoi.sifipdemo.data.repository

import com.bmoi.sifipdemo.data.model.BankAccount
import com.bmoi.sifipdemo.data.model.Transaction

/**
 * Read-only demo data for the dashboard. Numbers are in MGA (Ariary)
 * since BMOI is the Bank of Africa Madagascar.
 */
class BankRepository {

    fun loadAccount(): BankAccount = BankAccount(
        holder = "Rakoto Andrianasolo",
        accountNumberMasked = "•••• 4218",
        balanceMga = 8_452_300,
        transactions = listOf(
            Transaction("t1", "Salaire — Société TANA SA", "31/05/2026", 4_200_000),
            Transaction("t2", "Carte BMOI — Score Shop", "30/05/2026", -148_500),
            Transaction("t3", "Mobile Money — Orange Money", "28/05/2026", -50_000),
            Transaction("t4", "Virement reçu — Mialy R.", "27/05/2026", 320_000),
            Transaction("t5", "Prélèvement JIRAMA", "25/05/2026", -86_400),
        ),
    )
}
