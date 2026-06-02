package com.bmoi.sifipdemo.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmoi.sifipdemo.ui.components.BmoiLogo
import com.bmoi.sifipdemo.ui.theme.BmoiPurple
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val logoAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        logoAlpha.animateTo(1f, tween(600))
        delay(1100)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BmoiPurple),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            BmoiLogo(
                modifier = Modifier.alpha(logoAlpha.value),
                width = 240.dp,
                height = 96.dp,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "MADAGASCAR",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp,
            )
            Spacer(modifier = Modifier.height(48.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(26.dp),
                color = Color.White,
                strokeWidth = 2.dp,
            )
        }

        Text(
            text = "Sécurisé par SIFIP",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
        )
    }
}
