package com.bmoi.sifipdemo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmoi.sifipdemo.ui.theme.BmoiBorder
import com.bmoi.sifipdemo.ui.theme.BmoiMuted
import com.bmoi.sifipdemo.ui.theme.BmoiPurple
import com.bmoi.sifipdemo.ui.theme.BmoiPurpleLight
import com.bmoi.sifipdemo.ui.theme.BmoiPurpleTint
import com.bmoi.sifipdemo.ui.theme.BmoiText

/**
 * Carte centrale du dashboard BMOI Pocket : badge circulaire "Ar" en haut
 * (devise Ariary), label compte, solde de la veille + solde disponible.
 */
@Composable
fun BmoiAccountCard(
    accountLabel: String,
    accountId: String,
    balanceMga: Long,
    previousDayBalanceMga: Long,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BmoiBorder),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ArBadge()
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = accountLabel.uppercase(),
                color = BmoiText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Text(
                text = accountId,
                color = BmoiPurple,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = BmoiBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))

            BalanceLine(
                label = "SOLDE DE LA VEILLE",
                amount = previousDayBalanceMga,
            )
            Spacer(modifier = Modifier.height(10.dp))
            BalanceLine(
                label = "SOLDE DISPONIBLE",
                amount = balanceMga,
                emphasize = true,
            )
        }
    }
}

@Composable
private fun ArBadge() {
    Box(
        modifier = Modifier
            .size(88.dp)
            .background(BmoiPurpleTint, shape = CircleShape)
            .border(width = 2.dp, color = BmoiPurpleLight, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Ar",
            color = BmoiPurple,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun BalanceLine(label: String, amount: Long, emphasize: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            color = BmoiMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
        )
        Text(
            text = "${formatMga(amount)} MGA",
            color = if (emphasize) BmoiPurple else BmoiText,
            fontSize = if (emphasize) 18.sp else 15.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun formatMga(amount: Long): String {
    val nf = java.text.NumberFormat.getInstance(java.util.Locale.FRANCE)
    return nf.format(amount)
}
