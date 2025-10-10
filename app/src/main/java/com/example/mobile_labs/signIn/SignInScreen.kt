package com.example.mobile_labs.signIn

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onBack: () -> Unit = {},
    onSignUp: () -> Unit = {},
    onSignIn: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    var showPassword by rememberSaveable { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val emailRegex = remember {
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$".toRegex(RegexOption.IGNORE_CASE)
    }

    fun validate(): Boolean {
        emailError = when {
            email.isBlank() -> "Введите почту"
            !emailRegex.matches(email) -> "Некорректная почта"
            else -> null
        }

        passwordError = when {
            password.length < 8 -> "Минимум 8 символов"
            else -> null
        }

        return emailError == null && passwordError == null
    }

    val canSubmit by remember(email, password) {
        derivedStateOf { email.isNotBlank() && password.isNotBlank() }
    }

    fun handleSubmit() {
        focusManager.clearFocus()
        if (validate()) {
            onSignIn()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FonIn(modifier = Modifier.fillMaxSize())

        Scaffold(
            topBar = { TopBar(title = "Вход", onBack = onBack) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                FonIn(modifier = Modifier.fillMaxSize())
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(25.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    EmailField(
                        value = email,
                        onValueChange = {
                            onEmailChange(it)
                            emailError = null
                        },
                        error = emailError,
                        onClear = { onEmailChange("") }
                    )

                    PasswordField(
                        value = password,
                        onValueChange = {
                            onPasswordChange(it)
                            passwordError = null
                        },
                        showPassword = showPassword,
                        onToggleVisibility = { showPassword = !showPassword },
                        error = passwordError,
                        modifier = Modifier
                    )

                    ButtonsIn(
                        onSubmit = { handleSubmit() },
                        onSignUp = onSignUp,
                        canSubmit = canSubmit
                    )
                }
            }
        }
    }
}