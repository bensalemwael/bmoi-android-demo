package com.bmoi.sifipdemo.ui.transfer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmoi.sifipdemo.R
import com.bmoi.sifipdemo.ui.components.BmoiPrimaryButton
import com.bmoi.sifipdemo.ui.components.BmoiSecondaryButton
import com.bmoi.sifipdemo.ui.components.FraudGauge
import com.bmoi.sifipdemo.ui.components.FraudReasons
import com.bmoi.sifipdemo.ui.dashboard.formatMgaPublic
import com.bmoi.sifipdemo.ui.theme.BmoiBorder
import com.bmoi.sifipdemo.ui.theme.BmoiMuted
import com.bmoi.sifipdemo.ui.theme.BmoiPurple
import com.bmoi.sifipdemo.ui.theme.BmoiPurpleDeep
import com.bmoi.sifipdemo.ui.theme.StatusError
import com.bmoi.sifipdemo.ui.theme.StatusOk

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
        // Top bar violet
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BmoiPurple),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 12.dp),
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
                    text = stringResource(R.string.transfer_title).uppercase(),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.size(48.dp))
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Saisissez le virement",
            style = MaterialTheme.typography.headlineMedium,
            color = BmoiPurpleDeep,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            textAlign = TextAlign.Center,
        )

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
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BmoiPurple),
            )
            OutlinedTextField(
                value = state.iban,
                onValueChange = onIbanChanged,
                label = { Text(stringResource(R.string.transfer_iban)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BmoiPurple),
            )
            OutlinedTextField(
                value = state.amountText,
                onValueChange = onAmountChanged,
                label = { Text(stringResource(R.string.transfer_amount)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BmoiPurple),
            )

            Spacer(modifier = Modifier.height(8.dp))
            BmoiPrimaryButton(
                text = "Valider",
                onClick = onSubmit,
                enabled = state.amountText.isNotBlank() && state.iban.isNotBlank(),
            )
        }
    }
}

@Composable
private fun AnalyzingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = BmoiPurple, strokeWidth = 3.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.transfer_analyzing),
            style = MaterialTheme.typography.titleMedium,
            color = BmoiPurpleDeep,
        )
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isSuccess) StatusOk.copy(alpha = 0.08f) else StatusError.copy(alpha = 0.08f),
                )
                .border(
                    width = 1.dp,
                    color = if (isSuccess) StatusOk.copy(alpha = 0.3f) else StatusError.copy(alpha = 0.3f),
                )
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Filled.CheckCircle else Icons.Filled.Block,
                contentDescription = null,
                tint = if (isSuccess) StatusOk else StatusError,
                modifier = Modifier.size(28.dp),
            )
            Spacer(modifier = Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSuccess) StatusOk else StatusError,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = "Montant : ${formatMgaPublic(amount)} MGA",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BmoiPurpleDeep.copy(alpha = 0.8f),
                )
            }
        }

        FraudGauge(score = score)

        Text(
            text = "MOTIFS ANALYSÉS PAR L'IA SIFIP",
            color = BmoiMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        )
        HorizontalDivider(color = BmoiBorder, thickness = 1.dp)
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            FraudReasons(reasons = reasons)
        }
        HorizontalDivider(color = BmoiBorder, thickness = 1.dp)

        Column(modifier = Modifier.padding(20.dp)) {
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
