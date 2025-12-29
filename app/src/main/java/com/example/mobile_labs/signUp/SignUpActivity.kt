package com.example.mobile_labs.signUp

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mobile_labs.BaseActivity
import com.example.mobile_labs.model.User
import com.example.mobile_labs.signIn.SignInActivity
import com.example.mobile_labs.ui.theme.Mobile_labsTheme

class SignUpActivity : BaseActivity() {
    companion object {
        const val USER_OBJECT = "user_object"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mobile_labsTheme {
                SignUpScreen(
                    onBack = { finish() },
                    onSignIn = { startActivity(Intent(this, SignInActivity::class.java)) },
                    onSignUp = { name, email, password ->
                        val resultIntent = Intent().apply {
                            putExtra(USER_OBJECT, User(name, email, password))
                        }
                        setResult(RESULT_OK, resultIntent)

                        val signInIntent = Intent(this, SignInActivity::class.java).apply {
                            putExtra(USER_OBJECT, User(name, email, password))
                        }
                        startActivity(signInIntent)
                        finish()
                    }
                )
            }
        }
    }
}