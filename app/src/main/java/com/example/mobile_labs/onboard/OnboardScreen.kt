package com.example.mobile_labs.onboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnboardScreen(
    onSignUp: () -> Unit = {},
    onSignIn: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CardHeader(modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 35.dp, vertical = 60.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WelcomeText()
            Spacer(modifier = Modifier.height(8.dp))
            Description()
            Spacer(modifier = Modifier.height(24.dp))
            ActionButtons(onSignIn = onSignIn, onSignUp = onSignUp)
        }
    }
}