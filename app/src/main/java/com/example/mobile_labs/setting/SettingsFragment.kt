package com.example.mobile_labs.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.mobile_labs.model.disney.DisneyCharacter
import com.example.mobile_labs.network.ktor.KtorDisneyApi
import com.example.mobile_labs.store.DisneyRepository
import com.example.mobile_labs.store.file.ExternalBackupManager
import com.example.mobile_labs.store.file.InternalBackupManager
import com.example.mobile_labs.ui.theme.Mobile_labsTheme
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json


class SettingsFragment : Fragment() {

    private var characters: List<DisneyCharacter> = emptyList()
    private lateinit var repository: DisneyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = DisneyRepository(
            context = requireContext(),
            api = KtorDisneyApi,
            internalBackup = InternalBackupManager(requireContext()),
            externalBackup = ExternalBackupManager(requireContext())
        )

        // Получаем JSON-строку из аргументов
        arguments?.getString("characters_json")?.let { json ->
            characters = try {
                Json.decodeFromString(ListSerializer(DisneyCharacter.serializer()), json)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Mobile_labsTheme {
                    SettingsScreen(
                        onBackClick = { findNavController().popBackStack() },
                        repository = repository,
                        characters = characters
                    )
                }
            }
        }
    }
}

