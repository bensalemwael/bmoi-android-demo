package com.bmoi.sifipdemo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmoi.sifipdemo.ui.theme.BmoiPurple

/**
 * Bandeau supérieur classique : violet plein avec titre blanc centré.
 * Boutons optionnels home / power à droite (style Mobile Banking
 * institutionnel).
 */
@Composable
fun BmoiTopBar(
    title: String,
    onHomeClick: (() -> Unit)? = null,
    onPowerClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BmoiPurple)
            .height(48.dp),
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 96.dp),
        )
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onHomeClick != null) {
                IconButton(onClick = onHomeClick) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Accueil",
                        tint = Color.White,
                    )
                }
            }
            if (onPowerClick != null) {
                IconButton(onClick = onPowerClick) {
                    Icon(
                        imageVector = Icons.Filled.PowerSettingsNew,
                        contentDescription = "Déconnexion",
                        tint = Color.White,
                    )
                }
            }
            if (onHomeClick == null && onPowerClick == null) {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }
    }
}
