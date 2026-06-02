package com.bmoi.sifipdemo.ui.dashboard

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmoi.sifipdemo.R
import com.bmoi.sifipdemo.data.model.Transaction
import com.bmoi.sifipdemo.ui.components.BmoiAccountCard
import com.bmoi.sifipdemo.ui.components.BmoiAction
import com.bmoi.sifipdemo.ui.components.BmoiBottomActionBar
import com.bmoi.sifipdemo.ui.components.BmoiPrimaryButton
import com.bmoi.sifipdemo.ui.theme.BmoiBorder
import com.bmoi.sifipdemo.ui.theme.BmoiMuted
import com.bmoi.sifipdemo.ui.theme.BmoiPurple
import com.bmoi.sifipdemo.ui.theme.BmoiPurpleLight
import com.bmoi.sifipdemo.ui.theme.BmoiPurpleTint
import com.bmoi.sifipdemo.ui.theme.BmoiText
import com.bmoi.sifipdemo.ui.theme.StatusError
import com.bmoi.sifipdemo.ui.theme.StatusOk
import androidx.compose.ui.res.stringResource
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onTransferClicked: () -> Unit,
    onLogout: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val account = state.account
    var selectedAction by remember { mutableStateOf(BmoiAction.Center) }

    Scaffold(
        bottomBar = {
            BmoiBottomActionBar(
                selected = selectedAction,
                onActionSelected = { action ->
                    selectedAction = action
                    if (action == BmoiAction.Virements) {
                        onTransferClicked()
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            // Top bar violet
            TopBar(holderName = account.holder, onLogout = onLogout)

            Spacer(modifier = Modifier.height(20.dp))

            // Carte centrale BMOI
            BmoiAccountCard(
                accountLabel = "Compte A Vue en MGA",
                accountId = account.accountNumberMasked.replace("•", "").trim().ifBlank { "010191" },
                balanceMga = account.balanceMga,
                previousDayBalanceMga = account.balanceMga - 42_300,
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Pagination dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Dot(active = false)
                Spacer(modifier = Modifier.width(6.dp))
                Dot(active = true)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CTA virement
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                BmoiPrimaryButton(
                    text = stringResource(R.string.dashboard_transfer),
                    onClick = onTransferClicked,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Transactions
            Text(
                text = stringResource(R.string.dashboard_recent).uppercase(),
                color = BmoiMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
            ) {
                Column {
                    account.transactions.forEachIndexed { i, tx ->
                        TransactionRow(tx)
                        if (i < account.transactions.lastIndex) {
                            HorizontalDivider(
                                color = BmoiBorder,
                                thickness = 1.dp,
                                modifier = Modifier.padding(start = 60.dp),
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TopBar(holderName: String, onLogout: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BmoiPurple),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = Color.White,
                )
            }
            Text(
                text = "ACCUEIL",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            ProfileIcon(onClick = onLogout)
        }
    }
}

@Composable
private fun ProfileIcon(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Filled.PersonOutline,
                contentDescription = "Se déconnecter",
                tint = BmoiPurple,
            )
        }
    }
}

@Composable
private fun Dot(active: Boolean) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(if (active) BmoiPurple else BmoiPurpleLight),
    )
}

@Composable
private fun TransactionRow(tx: Transaction) {
    val credit = tx.amountMga >= 0
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    if (credit) BmoiPurpleTint else StatusError.copy(alpha = 0.10f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (credit) Icons.Filled.SouthWest else Icons.Filled.NorthEast,
                contentDescription = null,
                tint = if (credit) StatusOk else StatusError,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tx.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = BmoiText,
            )
            Text(
                text = tx.date,
                style = MaterialTheme.typography.bodyMedium,
                color = BmoiMuted,
            )
        }
        Text(
            text = "${if (credit) "+" else "-"}${formatMga(kotlin.math.abs(tx.amountMga))} MGA",
            color = if (credit) StatusOk else StatusError,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun formatMga(amount: Long): String {
    val nf = NumberFormat.getInstance(Locale.FRANCE)
    return nf.format(amount)
}

internal fun formatMgaPublic(amount: Long): String = formatMga(amount)
