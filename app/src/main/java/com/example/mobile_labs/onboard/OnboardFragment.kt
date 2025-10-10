package com.example.mobile_labs.onboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.mobile_labs.MainActivity
import com.example.mobile_labs.ui.theme.Mobile_labsTheme

class OnboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Mobile_labsTheme {
                    OnboardScreen(
                        onSignUp = {
                            (activity as? MainActivity)?.navigateToSignUp()
                        },
                        onSignIn = {
                            (activity as? MainActivity)?.navigateToSignIn()
                        },
                    )
                }
            }
        }
    }
}