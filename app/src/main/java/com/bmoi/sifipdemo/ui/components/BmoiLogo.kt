package com.bmoi.sifipdemo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bmoi.sifipdemo.R

/**
 * Logo BMOI Groupe BCP. Charge `res/drawable/bmoi_logo.png` — fichier
 * officiel fourni par la banque (ratio carré ~1:1). ContentScale.Fit
 * préserve les proportions quelles que soient les dimensions passées.
 */
@Composable
fun BmoiLogo(
    modifier: Modifier = Modifier,
    width: Dp = 120.dp,
    height: Dp = 120.dp,
) {
    Box(modifier = modifier.width(width).height(height)) {
        Image(
            painter = painterResource(id = R.drawable.bmoi_logo),
            contentDescription = "BMOI Groupe BCP",
            contentScale = ContentScale.Fit,
        )
    }
}
