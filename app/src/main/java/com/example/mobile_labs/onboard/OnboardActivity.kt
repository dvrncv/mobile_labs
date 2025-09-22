package com.example.mobile_labs.onboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mobile_labs.signIn.SignInActivity
import com.example.mobile_labs.signUp.SignUpActivity
import com.example.mobile_labs.ui.theme.Mobile_labsTheme

class OnboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mobile_labsTheme {
                OnboardScreen(
                    onSignUp = {
                        startActivity(Intent(this, SignUpActivity::class.java))
                    },
                    onSignIn = {
                        startActivity(Intent(this, SignInActivity::class.java))
                    },
                )
            }
        }
    }
}