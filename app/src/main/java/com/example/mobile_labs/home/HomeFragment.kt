package com.example.mobile_labs.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.mobile_labs.R
import com.example.mobile_labs.store.dataStore.SettingsDataStore
import com.example.mobile_labs.ui.theme.Mobile_labsTheme


class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Mobile_labsTheme {

                    val context = LocalContext.current
                    val dataStore = remember(context) { SettingsDataStore(context) }
                    val fontSize by dataStore.currentFontSize.collectAsState(16f)

                    HomeScreen(
                        fontSize = fontSize,
                        onOpenSettings = {
                            findNavController().navigate(R.id.action_home_to_settings)
                        }
                    )
                }
            }
        }
    }
}
