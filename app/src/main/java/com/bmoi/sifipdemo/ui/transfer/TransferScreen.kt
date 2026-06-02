package com.bmoi.sifipdemo.ui.transfer

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmoi.sifipdemo.ui.components.BmoiPrimaryButton
import com.bmoi.sifipdemo.ui.components.BmoiSecondaryButton
import com.bmoi.sifipdemo.ui.components.BmoiTopBar
import com.bmoi.sifipdemo.ui.components.FraudGauge
import com.bmoi.sifipdemo.ui.components.FraudReasons
import com.bmoi.sifipdemo.ui.dashboard.formatMgaPublic
import com.bmoi.sifipdemo.ui.theme.BmoiBorder
import com.bmoi.sifipdemo.ui.theme.BmoiMuted
import com.bmoi.sifipdemo.ui.theme.BmoiPurple
import com.bmoi.sifipdemo.ui.theme.BmoiPurpleDeep
import com.bmoi.sifipdemo.ui.theme.BmoiText
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
        BmoiTopBar(
            title = "Effectuer un virement",
            onHomeClick = onBack,
            onPowerClick = onBack,
        )

        when (state.phase) {
            TransferPhase.Form -> TransferForm(
                state = state,
                onRecipientChanged = viewModel::onRecipientChanged,
                onIbanChanged = viewModel::onIbanChanged,
                onAmountChanged = viewModel::onAmountChanged,
                onMotifChanged = viewModel::onMotifChanged,
                onSubmit = viewModel::submit,
            )
            TransferPhase.Analyzing -> AnalyzingState()
            TransferPhase.Approved -> ResultState(
                title = "Virement autorisé",
                isSuccess = true,
                score = state.result?.score ?: 0,
                reasons = state.result?.reasons.orEmpty(),
                amount = state.amountText.toLongOrNull() ?: 0,
                onClose = onBack,
                onRetry = viewModel::reset,
            )
            TransferPhase.Rejected -> ResultState(
                title = "Virement bloqué",
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
    onMotifChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, start = 12.dp, end = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Sélecteurs Compte à débiter / Compte à créditer (style boutons gris)
        AccountSelector(
            label = "Compte à débiter",
            value = "Compte courant •••• 4218",
        )
        AccountSelector(
            label = "Compte à créditer",
            value = state.recipient.ifBlank { "—" },
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Carte formulaire
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BmoiBorder),
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FieldGroup(label = "Bénéficiaire") {
                    OutlinedTextField(
                        value = state.recipient,
                        onValueChange = onRecipientChanged,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BmoiPurple),
                    )
                }
                FieldGroup(label = "IBAN / RIB") {
                    OutlinedTextField(
                        value = state.iban,
                        onValueChange = onIbanChanged,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BmoiPurple),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    FieldGroup(label = "Montant :", modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = state.amountText,
                            onValueChange = onAmountChanged,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BmoiPurple),
                        )
                    }
                    FieldGroup(label = "Devise", modifier = Modifier.size(width = 96.dp, height = 70.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .border(1.dp, BmoiBorder, RoundedCornerShape(4.dp))
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(text = "MGA", color = BmoiText, fontSize = 14.sp)
                        }
                    }
                }
                FieldGroup(label = "Motif :") {
                    OutlinedTextField(
                        value = state.motif,
                        onValueChange = onMotifChanged,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BmoiPurple),
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                HorizontalDivider(color = BmoiBorder, thickness = 1.dp)

                RadioRow(
                    selected = true,
                    label = "Virement unique le",
                    trailing = {
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .padding(end = 0.dp)
                                .border(1.dp, BmoiBorder, RoundedCornerShape(4.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(text = "07/06/2026", color = BmoiText, fontSize = 13.sp)
                        }
                    },
                )
                RadioRow(
                    selected = false,
                    label = "Virement permanent",
                    trailing = {
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .border(1.dp, BmoiBorder, RoundedCornerShape(4.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Mensuel", color = BmoiMuted, fontSize = 13.sp)
                                Spacer(modifier = Modifier.size(4.dp))
                                Icon(
                                    imageVector = Icons.Filled.ExpandMore,
                                    contentDescription = null,
                                    tint = BmoiMuted,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bouton Valider centré
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 80.dp),
        ) {
            BmoiPrimaryButton(
                text = "Valider",
                onClick = onSubmit,
                enabled = state.amountText.isNotBlank() && state.iban.isNotBlank(),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AccountSelector(label: String, value: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        shape = RoundedCornerShape(4.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, BmoiBorder),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFAFAFB), Color(0xFFE8E6EE)),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = label, color = BmoiText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                if (value.isNotBlank()) {
                    Text(text = value, color = BmoiMuted, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun FieldGroup(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = BmoiMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        content()
    }
}

@Composable
private fun RadioRow(
    selected: Boolean,
    label: String,
    trailing: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = {},
            colors = RadioButtonDefaults.colors(selectedColor = BmoiPurple),
        )
        Text(
            text = label,
            color = BmoiText,
            fontSize = 13.sp,
            modifier = Modifier.padding(end = 8.dp),
        )
        Spacer(modifier = Modifier.weight(1f))
        trailing()
    }
}

@Composable
private fun AnalyzingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = BmoiPurple, strokeWidth = 3.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Analyse anti-fraude SIFIP en cours…",
            style = MaterialTheme.typography.titleMedium,
            color = BmoiPurpleDeep,
            textAlign = TextAlign.Center,
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
            .padding(12.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            color = if (isSuccess) StatusOk.copy(alpha = 0.08f) else StatusError.copy(alpha = 0.08f),
            border = BorderStroke(
                1.dp,
                if (isSuccess) StatusOk.copy(alpha = 0.3f) else StatusError.copy(alpha = 0.3f),
            ),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
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
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSuccess) StatusOk else StatusError,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Montant : ${formatMgaPublic(amount)} MGA",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BmoiPurpleDeep.copy(alpha = 0.8f),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BmoiBorder),
        ) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                FraudGauge(score = score)
                Text(
                    text = "MOTIFS ANALYSÉS PAR L'IA SIFIP",
                    color = BmoiMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
                HorizontalDivider(color = BmoiBorder, thickness = 1.dp)
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    FraudReasons(reasons = reasons)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 60.dp)) {
            BmoiPrimaryButton(text = "Retour au tableau de bord", onClick = onClose)
            Spacer(modifier = Modifier.height(8.dp))
            BmoiSecondaryButton(text = "Nouveau virement", onClick = onRetry)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}
