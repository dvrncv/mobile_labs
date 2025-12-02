package com.example.mobile_labs.store

import android.content.Context
import com.example.mobile_labs.model.disney.DisneyCharacter
import com.example.mobile_labs.network.ktor.KtorDisneyApi
import com.example.mobile_labs.store.file.ExternalBackupManager
import com.example.mobile_labs.store.file.InternalBackupManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class DisneyRepository(
    private val context: Context,
    private val api: KtorDisneyApi,
    private val internalBackup: InternalBackupManager,
    private val externalBackup: ExternalBackupManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getCharacters(range: IntRange): List<DisneyCharacter> = withContext(dispatcher) {
        val characters = api.getCharacters(range).getOrDefault(emptyList())

        if (characters.isNotEmpty()) {
            val internalFile = File(context.filesDir, "disney_backup.txt")
            val jsonString = Json.encodeToString(characters)
            internalFile.writeText(jsonString)

            internalBackup.saveInternalBackup(internalFile)
        }

        characters
    }

    fun saveExternalBackup(characters: List<DisneyCharacter>, fileName: String): Boolean {
        return externalBackup.saveToExternal(characters, fileName)
    }

    fun getExternalFileInfo(fileName: String) = externalBackup.getFileInfo(fileName)

    fun internalBackupExists() = internalBackup.backupExists()

    suspend fun restoreInternalBackup(): Boolean = withContext(dispatcher) {
        internalBackup.restoreExternal()
    }

    suspend fun saveInternalBackupFromFile(file: File) = withContext(dispatcher) {
        internalBackup.saveInternalBackup(file)
    }
}
