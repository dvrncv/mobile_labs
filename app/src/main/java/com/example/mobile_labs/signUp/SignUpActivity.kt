package com.example.mobile_labs.signUp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mobile_labs.home.HomeActivity
import com.example.mobile_labs.signIn.SignInActivity
import com.example.mobile_labs.ui.theme.Mobile_labsTheme

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mobile_labsTheme {
                SignUpScreen(
                    onBack = { finish() },
                    onSignIn = { startActivity(Intent(this, SignInActivity::class.java)) },
                    onSignUp = { startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}