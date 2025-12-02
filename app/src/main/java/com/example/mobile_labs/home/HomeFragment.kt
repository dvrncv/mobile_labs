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
import com.example.mobile_labs.store.DisneyRepository
import com.example.mobile_labs.store.dataStore.SettingsDataStore
import com.example.mobile_labs.store.file.ExternalBackupManager
import com.example.mobile_labs.store.file.InternalBackupManager
import com.example.mobile_labs.ui.theme.Mobile_labsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


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
                    val characters = remember { mutableStateListOf<DisneyCharacter>() }
                    var isLoading by remember { mutableStateOf(true) }
                    var errorMessage by remember { mutableStateOf<String?>(null) }

                    val repository = remember {
                        DisneyRepository(
                            context = context,
                            api = KtorDisneyApi,
                            internalBackup = InternalBackupManager(context),
                            externalBackup = ExternalBackupManager(context)
                        )
                    }

                    LaunchedEffect(Unit) {
                        CoroutineScope(Dispatchers.IO).launch {
                            runCatching {
                                repository.getCharacters(351..400)
                            }.onSuccess { list ->
                                characters.clear()
                                characters.addAll(list)
                            }.onFailure { ex ->
                                errorMessage = ex.message
                            }
                            isLoading = false
                        }
                    }

                    HomeScreen(
                        fontSize = 16f,
                        onOpenSettings = {
                            val bundle = Bundle().apply {
                                val json = Json.encodeToString(characters.toList())
                                putString("characters_json", json)
                            }
                            findNavController().navigate(R.id.action_home_to_settings, bundle)
                        },
                        characters = characters.toList(),
                        isLoading = isLoading,
                        errorMessage = errorMessage
                    )
                }
            }
        }
    }
}
