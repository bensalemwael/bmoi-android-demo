package com.bmoi.sifipdemo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmoi.sifipdemo.ui.theme.BmoiMuted
import com.bmoi.sifipdemo.ui.theme.BmoiPurple
import com.bmoi.sifipdemo.ui.theme.BmoiPurpleDark

enum class BmoiAction { Comptes, Beneficiaires, Center, Virements, Cartes }

/**
 * Barre d'actions bas BMOI Pocket : 4 items + un badge central proéminent
 * en forme de cercle violet sur fond blanc, légèrement surélevé.
 */
@Composable
fun BmoiBottomActionBar(
    selected: BmoiAction,
    onActionSelected: (BmoiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ActionItem(
                icon = Icons.Filled.AccountBalanceWallet,
                label = "COMPTES",
                isSelected = selected == BmoiAction.Comptes,
                onClick = { onActionSelected(BmoiAction.Comptes) },
            )
            ActionItem(
                icon = Icons.Filled.People,
                label = "BÉNÉFICIAIRES",
                isSelected = selected == BmoiAction.Beneficiaires,
                onClick = { onActionSelected(BmoiAction.Beneficiaires) },
            )
            CenterBadge(onClick = { onActionSelected(BmoiAction.Center) })
            ActionItem(
                icon = Icons.Filled.SwapHoriz,
                label = "VIREMENTS",
                isSelected = selected == BmoiAction.Virements,
                onClick = { onActionSelected(BmoiAction.Virements) },
            )
            ActionItem(
                icon = Icons.Filled.CreditCard,
                label = "CARTES",
                isSelected = selected == BmoiAction.Cartes,
                onClick = { onActionSelected(BmoiAction.Cartes) },
            )
        }
    }
}

@Composable
private fun ActionItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val tint = if (isSelected) BmoiPurple else BmoiMuted
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = tint,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CenterBadge(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .offset(y = (-12).dp)
            .size(54.dp)
            .background(BmoiPurple, shape = CircleShape)
            .border(width = 3.dp, color = Color.White, shape = CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "B",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
