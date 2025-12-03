package com.example.mobile_labs.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.mobile_labs.R
import com.example.mobile_labs.model.disney.DisneyCharacter
import com.example.mobile_labs.network.ktor.KtorDisneyApi
import com.example.mobile_labs.store.dataStore.SettingsDataStore
import com.example.mobile_labs.store.file.InternalFileStorage
import com.example.mobile_labs.ui.theme.Mobile_labsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Mobile_labsTheme {
                    HomeScreenContainer(
                        onOpenSettings = {
                            findNavController().navigate(R.id.action_home_to_settings)
                        }
                    )
                }
            }
        }
    }
}
