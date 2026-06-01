package com.bmoi.sifipdemo.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmoi.sifipdemo.data.mock.SifipApi
import com.bmoi.sifipdemo.data.model.FraudDecision
import com.bmoi.sifipdemo.data.model.FraudScoreResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TransferPhase { Form, Analyzing, Approved, Rejected }

data class TransferUiState(
    val recipient: String = "Mialy Rakotomalala",
    val iban: String = "MG46 0000 5012 3456 7890 12",
    val amountText: String = "250000",
    val phase: TransferPhase = TransferPhase.Form,
    val result: FraudScoreResponse? = null,
)

class TransferViewModel(
    private val sifipApi: SifipApi,
    private val userMsisdn: String,
) : ViewModel() {

    private val _state = MutableStateFlow(TransferUiState())
    val state: StateFlow<TransferUiState> = _state.asStateFlow()

    fun onRecipientChanged(v: String) = _state.update { it.copy(recipient = v) }
    fun onIbanChanged(v: String) = _state.update { it.copy(iban = v) }
    fun onAmountChanged(v: String) = _state.update {
        // Allow only digits to keep the demo bullet-proof.
        it.copy(amountText = v.filter(Char::isDigit))
    }

    fun reset() {
        _state.update { it.copy(phase = TransferPhase.Form, result = null) }
    }

    fun submit() {
        if (_state.value.phase == TransferPhase.Analyzing) return
        val amount = _state.value.amountText.toLongOrNull() ?: 0L
        viewModelScope.launch {
            _state.update { it.copy(phase = TransferPhase.Analyzing, result = null) }
            val res = sifipApi.scoreFraud(
                msisdn = userMsisdn,
                recipientIban = _state.value.iban,
                amountMga = amount,
            )
            val phase = when (res.decision) {
                FraudDecision.APPROVE, FraudDecision.REVIEW -> TransferPhase.Approved
                FraudDecision.CHALLENGE, FraudDecision.REJECT -> TransferPhase.Rejected
            }
            _state.update { it.copy(phase = phase, result = res) }
        }
    }
}
