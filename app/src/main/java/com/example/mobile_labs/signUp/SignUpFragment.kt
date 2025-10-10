package com.example.mobile_labs.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.mobile_labs.MainActivity
import com.example.mobile_labs.ui.theme.Mobile_labsTheme

class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Mobile_labsTheme {
                    var email by rememberSaveable { mutableStateOf(arguments?.getString("email") ?: "") }
                    var password by rememberSaveable { mutableStateOf(arguments?.getString("password") ?: "") }

                    SignUpScreen(
                        email = email,
                        password = password,
                        onEmailChange = { email = it },
                        onPasswordChange = { password = it },
                        onBack = { requireActivity().onBackPressedDispatcher.onBackPressed() },
                        onSignIn = {
                            (activity as? MainActivity)?.navigateToSignIn(email = email, password = password)
                        },
                        onSignUp = {
                            (activity as? MainActivity)?.navigateToSignIn(email = email, password = password)
                        }
                    )
                }
            }
        }
    }
}
