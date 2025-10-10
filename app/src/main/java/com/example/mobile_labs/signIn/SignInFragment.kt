package com.example.mobile_labs.signIn

import android.os.Bundle
import android.util.Log
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
import com.example.mobile_labs.model.User
import com.example.mobile_labs.ui.theme.Mobile_labsTheme

class SignInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Mobile_labsTheme {
                    val user = arguments?.getParcelable<User>("user")
                    Log.d("USER", "email = ${user?.email}; password = ${user?.password}")

                    var email by rememberSaveable { mutableStateOf(arguments?.getString("email") ?: "") }
                    var password by rememberSaveable { mutableStateOf(arguments?.getString("password") ?: "") }

                    SignInScreen(
                        email = email,
                        password = password,
                        onEmailChange = { email = it },
                        onPasswordChange = { password = it },
                        onBack = { requireActivity().onBackPressedDispatcher.onBackPressed() },
                        onSignUp = {
                            (activity as? MainActivity)?.navigateToSignUp()
                        },
                        onSignIn = {
                            (activity as? MainActivity)?.navigateToHome()
                        }
                    )
                }
            }
        }
    }

    companion object {
        fun newInstance(user: User? = null, email: String? = null, password: String? = null): SignInFragment {
            return SignInFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("user", user)
                    putString("email", email)
                    putString("password", password)
                }
            }
        }
    }
}