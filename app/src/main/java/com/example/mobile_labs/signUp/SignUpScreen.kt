package com.example.mobile_labs.signUp

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp

@Composable
fun SignUpScreen(
    onBack: () -> Unit = {},
    onSignUp: (name: String, email: String, password: String) -> Unit = { _, _, _ -> },
    onSignIn: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var genderError by remember { mutableStateOf<String?>(null) }

    val emailRegex = remember {
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$".toRegex(RegexOption.IGNORE_CASE)
    }

    fun validate(): Boolean {
        nameError = if (name.isBlank()) "Имя обязательно" else null

        emailError = when {
            email.isBlank() -> "Email обязателен"
            !emailRegex.matches(email) -> "Введите корректный email"
            else -> null
        }

        passwordError = when {
            password.length < 8 -> "Минимум 8 символов"
            else -> null
        }

        confirmPasswordError = when {
            confirmPassword != password -> "Пароли не совпадают"
            else -> null
        }

        genderError = if (gender.isBlank()) "Выберите пол" else null

        return listOf(nameError, emailError, passwordError, confirmPasswordError, genderError)
            .all { it == null }
    }

    val canSubmit by remember(name, email, password, gender) {
        derivedStateOf { name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && gender.isNotBlank() }
    }

    fun handleSubmit() {
        focusManager.clearFocus()
        if (validate()) {
            onSignUp(name, email, password)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Fon(modifier = Modifier.fillMaxSize())

        Scaffold(
            topBar = { TopBar(title = "Регистрация", onBack = onBack) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NameField(
                    value = name,
                    onValueChange = { name = it; nameError = null },
                    error = nameError
                )

                EmailField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    error = emailError,
                )

                PasswordField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    showPassword = showPassword,
                    onToggleVisibility = { showPassword = !showPassword },
                    error = passwordError,
                    modifier = Modifier
                )

                ConfirmPasswordField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; confirmPasswordError = null },
                    showPassword = showPassword,
                    onToggleVisibility = { showPassword = !showPassword },
                    error = confirmPasswordError,
                )

                GenderSelector(
                    selectedGender = gender,
                    onGenderSelected = { gender = it; genderError = null }
                )

                ButtonsUp(
                    onSubmit = { handleSubmit() },
                    onSignIn = onSignIn,
                    canSubmit = canSubmit
                )
            }
        }
    }
}
