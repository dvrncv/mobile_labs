package com.example.mobile_labs.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mobile_labs.ui.theme.Mobile_labsTheme

class SignInFragment : Fragment() {

     private val args: SignInFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val user = args.user
                var email by remember { mutableStateOf("") } // user?.email ?: ""
                var password by remember { mutableStateOf("") } // user?.password ?: ""

                Mobile_labsTheme {
                    SignInScreen(
                        email = email,
                        password = password,
                        onEmailChange = { email = it },
                        onPasswordChange = { password = it },
                        onSignIn = {
                             findNavController().navigate(
                                 SignInFragmentDirections.actionSignInToHome(email)
                             )
                        },
                        onSignUp = {
                             findNavController().navigate(
                                 SignInFragmentDirections.actionSignInToSignUp()
                             )
                        },
                        onBack = {
                             findNavController().popBackStack()
                        }
                    )
                }
            }
        }
    }
}
