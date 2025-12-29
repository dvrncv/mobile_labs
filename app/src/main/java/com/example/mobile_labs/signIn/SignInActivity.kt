package com.example.mobile_labs.signIn

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.mobile_labs.BaseActivity
import com.example.mobile_labs.home.HomeActivity
import com.example.mobile_labs.model.User
import com.example.mobile_labs.signUp.SignUpActivity
import com.example.mobile_labs.ui.theme.Mobile_labsTheme
import kotlinx.coroutines.flow.MutableStateFlow

class SignInActivity : BaseActivity() {
    
    private val userFlow = MutableStateFlow<User?>(null)

    private val launcher = registerForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            @Suppress("DEPRECATION")
            val user = result.data?.getParcelableExtra<User>(SignUpActivity.USER_OBJECT)
            userFlow.value = user
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        @Suppress("DEPRECATION")
        val userFromIntent = intent?.getParcelableExtra<User>(SignUpActivity.USER_OBJECT)
        if (userFromIntent != null) {
            userFlow.value = userFromIntent
        }
        
        setContent {
            val user by userFlow.collectAsState()
            
            Mobile_labsTheme {
                SignInScreen(
                    onBack = { finish() },
                    onSignUp = {
                        launcher.launch(Intent(this@SignInActivity, SignUpActivity::class.java))
                    },
                    onSignIn = {
                        startActivity(Intent(this@SignInActivity, HomeActivity::class.java))
                        finish()
                    },
                    receivedEmail = user?.email,
                    receivedPassword = user?.password
                )
            }
        }
    }
}