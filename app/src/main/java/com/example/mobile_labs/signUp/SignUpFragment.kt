package com.example.mobile_labs.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobile_labs.model.User
import com.example.mobile_labs.ui.theme.Mobile_labsTheme

class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                var email by rememberSaveable { mutableStateOf("") }
                var password by rememberSaveable { mutableStateOf("") }
                Mobile_labsTheme {
                    SignUpScreen(
                        email = email,
                        password = password,
                        onEmailChange = { email = it },
                        onPasswordChange = { password = it },
                        onSignIn = {
                             val user = User(name = "", email = email,  password = password)
                             findNavController().navigate(SignUpFragmentDirections.actionSignUpToSignIn(user))
                        },
                        onSignUp = {
                             val user = User(name = "", email = email,  password = password)
                             findNavController().navigate(SignUpFragmentDirections.actionSignUpToSignIn(user))
                        },
                        onBack = {
                             parentFragmentManager.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
