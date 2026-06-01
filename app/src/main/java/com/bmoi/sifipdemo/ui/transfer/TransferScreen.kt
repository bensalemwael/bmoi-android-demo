package com.bmoi.sifipdemo.ui.transfer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bmoi.sifipdemo.R
import com.bmoi.sifipdemo.ui.components.BmoiPrimaryButton
import com.bmoi.sifipdemo.ui.components.BmoiSecondaryButton
import com.bmoi.sifipdemo.ui.components.FraudGauge
import com.bmoi.sifipdemo.ui.components.FraudReasons
import com.bmoi.sifipdemo.ui.dashboard.formatMgaPublic
import com.bmoi.sifipdemo.ui.theme.BmoiNavy
import com.bmoi.sifipdemo.ui.theme.BmoiNavyDark
import com.bmoi.sifipdemo.ui.theme.StatusError
import com.bmoi.sifipdemo.ui.theme.StatusOk
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    viewModel: TransferViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(BmoiNavy, BmoiNavyDark))),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color.White,
                    )
                }
                Text(
                    text = stringResource(R.string.transfer_title),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        when (state.phase) {
            TransferPhase.Form -> TransferForm(
                state = state,
                onRecipientChanged = viewModel::onRecipientChanged,
                onIbanChanged = viewModel::onIbanChanged,
                onAmountChanged = viewModel::onAmountChanged,
                onSubmit = viewModel::submit,
            )
            TransferPhase.Analyzing -> AnalyzingState()
            TransferPhase.Approved -> ResultState(
                title = stringResource(R.string.transfer_approved),
                isSuccess = true,
                score = state.result?.score ?: 0,
                reasons = state.result?.reasons.orEmpty(),
                amount = state.amountText.toLongOrNull() ?: 0,
                onClose = onBack,
                onRetry = viewModel::reset,
            )
            TransferPhase.Rejected -> ResultState(
                title = stringResource(R.string.transfer_blocked),
                isSuccess = false,
                score = state.result?.score ?: 0,
                reasons = state.result?.reasons.orEmpty(),
                amount = state.amountText.toLongOrNull() ?: 0,
                onClose = onBack,
                onRetry = viewModel::reset,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransferForm(
    state: TransferUiState,
    onRecipientChanged: (String) -> Unit,
    onIbanChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            OutlinedTextField(
                value = state.recipient,
                onValueChange = onRecipientChanged,
                label = { Text(stringResource(R.string.transfer_recipient)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BmoiNavy),
            )
            OutlinedTextField(
                value = state.iban,
                onValueChange = onIbanChanged,
                label = { Text(stringResource(R.string.transfer_iban)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BmoiNavy),
            )
            OutlinedTextField(
                value = state.amountText,
                onValueChange = onAmountChanged,
                label = { Text(stringResource(R.string.transfer_amount)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BmoiNavy),
            )
            Spacer(modifier = Modifier.height(4.dp))
            BmoiPrimaryButton(
                text = stringResource(R.string.transfer_send),
                onClick = onSubmit,
                enabled = state.amountText.isNotBlank() && state.iban.isNotBlank(),
            )
        }
    }
}

@Composable
private fun AnalyzingState() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                color = BmoiNavy,
                strokeWidth = 3.dp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.transfer_analyzing),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun ResultState(
    title: String,
    isSuccess: Boolean,
    score: Int,
    reasons: List<String>,
    amount: Long,
    onClose: () -> Unit,
    onRetry: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Filled.CheckCircle else Icons.Filled.Block,
                contentDescription = null,
                tint = if (isSuccess) StatusOk else StatusError,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .height(56.dp),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = if (isSuccess) StatusOk else StatusError,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Montant : ${formatMgaPublic(amount)} MGA",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            )
            Spacer(modifier = Modifier.height(16.dp))
            FraudGauge(score = score)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Motifs analysés par l'IA SIFIP",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 6.dp),
            )
            FraudReasons(reasons = reasons)
            Spacer(modifier = Modifier.height(20.dp))
            BmoiPrimaryButton(
                text = stringResource(R.string.transfer_close),
                onClick = onClose,
            )
            Spacer(modifier = Modifier.height(8.dp))
            BmoiSecondaryButton(
                text = "Nouveau virement",
                onClick = onRetry,
            )
        }
    }
}
