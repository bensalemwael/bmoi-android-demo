package com.bmoi.sifipdemo.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmoi.sifipdemo.data.mock.MockScenario
import com.bmoi.sifipdemo.data.mock.SifipApi
import com.bmoi.sifipdemo.data.mock.SifipMockService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Status of a single SIFIP check shown in the login flow. */
enum class CheckStatus { Idle, Running, Ok, Failed }

data class CheckState(
    val name: String,
    val description: String,
    val status: CheckStatus = CheckStatus.Idle,
    val resultMessage: String? = null,
)

enum class LoginPhase { Idle, Running, Success, Failure }

data class LoginUiState(
    val phoneNumber: String = "+261 32 12 345 67",
    val phase: LoginPhase = LoginPhase.Idle,
    val numberVerify: CheckState = CheckState(
        name = "Number Verify",
        description = "Vérification du numéro",
    ),
    val simSwap: CheckState = CheckState(
        name = "SIM Swap Check",
        description = "Détection changement SIM récent",
    ),
    val deviceSwap: CheckState = CheckState(
        name = "Device Swap",
        description = "Vérification appareil habituel",
    ),
    val scenario: MockScenario = MockScenario.ALL_OK,
)

class LoginViewModel(
    private val sifipApi: SifipApi,
    /** Optional reference to the mock service: lets the UI flip scenarios live. */
    private val mockService: SifipMockService? = null,
) : ViewModel() {

    private val _state = MutableStateFlow(
        LoginUiState(scenario = mockService?.scenario?.value ?: MockScenario.ALL_OK)
    )
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onPhoneChanged(phone: String) {
        _state.update { it.copy(phoneNumber = phone) }
    }

    fun setScenario(scenario: MockScenario) {
        mockService?.setScenario(scenario)
        _state.update { it.copy(scenario = scenario) }
    }

    /** Resets to idle, useful when the user comes back from the dashboard. */
    fun reset() {
        _state.update {
            LoginUiState(
                phoneNumber = it.phoneNumber,
                scenario = it.scenario,
            )
        }
    }

    fun login(onSuccess: () -> Unit) {
        if (_state.value.phase == LoginPhase.Running) return

        viewModelScope.launch {
            // Reset all checks and mark running.
            _state.update {
                it.copy(
                    phase = LoginPhase.Running,
                    numberVerify = it.numberVerify.copy(
                        status = CheckStatus.Running,
                        resultMessage = null,
                    ),
                    simSwap = it.simSwap.copy(
                        status = CheckStatus.Idle,
                        resultMessage = null,
                    ),
                    deviceSwap = it.deviceSwap.copy(
                        status = CheckStatus.Idle,
                        resultMessage = null,
                    ),
                )
            }

            // 1) Number Verify
            val phone = _state.value.phoneNumber
            val nv = sifipApi.verifyNumber(phone)
            _state.update {
                it.copy(
                    numberVerify = it.numberVerify.copy(
                        status = if (nv.verified) CheckStatus.Ok else CheckStatus.Failed,
                        resultMessage = nv.message,
                    ),
                    // Queue the next one as Running only if the previous passed.
                    simSwap = if (nv.verified) {
                        it.simSwap.copy(status = CheckStatus.Running)
                    } else {
                        it.simSwap
                    },
                )
            }
            if (!nv.verified) {
                _state.update { it.copy(phase = LoginPhase.Failure) }
                return@launch
            }

            // 2) SIM Swap
            val ss = sifipApi.checkSimSwap(phone)
            _state.update {
                it.copy(
                    simSwap = it.simSwap.copy(
                        status = if (!ss.swapped) CheckStatus.Ok else CheckStatus.Failed,
                        resultMessage = ss.message,
                    ),
                    deviceSwap = if (!ss.swapped) {
                        it.deviceSwap.copy(status = CheckStatus.Running)
                    } else {
                        it.deviceSwap
                    },
                )
            }
            if (ss.swapped) {
                _state.update { it.copy(phase = LoginPhase.Failure) }
                return@launch
            }

            // 3) Device Swap
            val ds = sifipApi.checkDeviceSwap(phone, deviceId = DEMO_DEVICE_ID)
            _state.update {
                it.copy(
                    deviceSwap = it.deviceSwap.copy(
                        status = if (ds.knownDevice) CheckStatus.Ok else CheckStatus.Failed,
                        resultMessage = ds.message,
                    ),
                )
            }
            if (!ds.knownDevice) {
                _state.update { it.copy(phase = LoginPhase.Failure) }
                return@launch
            }

            _state.update { it.copy(phase = LoginPhase.Success) }
            onSuccess()
        }
    }

    private companion object {
        // Stable per-install identifier would normally come from SafetyNet/Play Integrity.
        // For the demo we hard-code a fingerprint so the mock can recognise the device.
        const val DEMO_DEVICE_ID = "samsung-galaxy-s23-DEMO-A1B2C3"
    }
}
