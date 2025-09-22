package com.example.mobile_labs.signIn

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mobile_labs.home.HomeActivity
import com.example.mobile_labs.signUp.SignUpActivity
import com.example.mobile_labs.ui.theme.Mobile_labsTheme

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mobile_labsTheme {
                SignInScreen(
                    onBack = { finish() },
                    onSignUp = { startActivity(Intent(this, SignUpActivity::class.java)) },
                    onSignIn = { startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}