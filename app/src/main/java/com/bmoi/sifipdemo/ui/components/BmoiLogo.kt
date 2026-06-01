package com.bmoi.sifipdemo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bmoi.sifipdemo.R

/**
 * BMOI corporate wordmark. The drawable resource is a vector — replace
 * `res/drawable/bmoi_logo.xml` by the official PNG/SVG once received from
 * the bank's communication team without changing any composable.
 */
@Composable
fun BmoiLogo(
    modifier: Modifier = Modifier,
    width: Dp = 200.dp,
    height: Dp = 80.dp,
) {
    Box(modifier = modifier.width(width).height(height)) {
        Image(
            painter = painterResource(id = R.drawable.bmoi_logo),
            contentDescription = "BMOI",
        )
    }
}
