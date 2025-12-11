package com.example.mobile_labs.store.repository

import android.content.Context
import com.example.mobile_labs.database.DisneyCharacterDao
import com.example.mobile_labs.database.DisneyCharacterEntity
import com.example.mobile_labs.database.DisneyDatabase
import com.example.mobile_labs.model.disney.DisneyCharacter
import com.example.mobile_labs.network.ktor.KtorDisneyApi
import com.example.mobile_labs.store.file.FormatBackup
import com.example.mobile_labs.store.file.FileInfo
import com.example.mobile_labs.store.file.ExternalFileStorage
import com.example.mobile_labs.store.file.InternalFileStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DisneyRepository(
    private val context: Context,
    private val userNumber: Int
) {
    private val backupFileName = "${userNumber}_disney_backup.txt"

    private val internalBackupStorage = InternalFileStorage(
        context = context,
        fileName = backupFileName
    )

    private val externalStorage = ExternalFileStorage(
        context = context,
        fileName = backupFileName
    )

    private val database: DisneyDatabase = DisneyDatabase.getDatabase(context)
    private val characterDao: DisneyCharacterDao = database.characterDao()

    fun getAllCharactersFlow(): Flow<List<DisneyCharacter>> {
        return characterDao.getAllCharacters().map { entities ->
            entities.map { it.toDisneyCharacter() }
        }
    }

    fun getCharactersUpToNumberFlow(maxNumber: Int): Flow<List<DisneyCharacter>> {
        val startId = 351
        val endId = 350 + (maxNumber - userNumber) * 50 + 50
        return characterDao.getCharactersInRange(startId, endId).map { entities ->
            entities.map { it.toDisneyCharacter() }
        }
    }

    suspend fun coldStart(): Result<Unit> {
        return try {
            val startId = 351
            val endId = 400
            
            val countInRange = characterDao.getCharacterCountInRange(startId, endId)
            
            if (countInRange < 50) {
                loadCharactersFromApi(userNumber)
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshCharacters(): Result<Unit> {
        return loadCharactersFromApi(userNumber)
    }

    suspend fun loadCharactersByNumber(number: Int): Result<Unit> {
        return loadCharactersFromApi(number)
    }

    private suspend fun loadCharactersFromApi(number: Int): Result<Unit> {
        return try {
            val startId = 350 + (number - userNumber) * 50 + 1
            val endId = startId + 49
            val idRange = startId..endId

            val characters = KtorDisneyApi.getCharacters(idRange).getOrNull() ?: emptyList()

            if (characters.isNotEmpty()) {
                val entities = characters.map { DisneyCharacterEntity.fromDisneyCharacter(it) }
                
                entities.forEach { entity ->
                    val existing = characterDao.getCharacterById(entity.id)
                    if (existing == null) {
                        characterDao.insertCharacter(entity)
                    }
                }
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Не удалось загрузить персонажей из API"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCharacterCount(): Int {
        return characterDao.getCharacterCount()
    }

    suspend fun insertCharacter(character: DisneyCharacter): Result<Unit> {
        return try {
            val entity = DisneyCharacterEntity.fromDisneyCharacter(character)
            characterDao.insertCharacter(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertCharacters(characters: List<DisneyCharacter>): Result<Unit> {
        return try {
            val entities = characters.map { DisneyCharacterEntity.fromDisneyCharacter(it) }
            characterDao.insertCharacters(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCharacterById(id: Int): Result<DisneyCharacter?> {
        return try {
            val entity = characterDao.getCharacterById(id)
            val character = entity?.toDisneyCharacter()
            Result.success(character)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun updateCharacter(character: DisneyCharacter): Result<Unit> {
        return try {
            val entity = DisneyCharacterEntity.fromDisneyCharacter(character)
            characterDao.updateCharacter(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCharacterById(id: Int): Result<Unit> {
        return try {
            characterDao.deleteCharacterById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCharacter(character: DisneyCharacter): Result<Unit> {
        return try {
            val entity = DisneyCharacterEntity.fromDisneyCharacter(character)
            characterDao.deleteCharacter(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllCharacters(): Result<Unit> {
        return try {
            characterDao.deleteAllCharacters()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun hasInternalBackup(): Boolean = internalBackupStorage.fileExists()

    fun getExternalFileInfo(): FileInfo? {
        return externalStorage.getFileInfo()
    }

    suspend fun createBackup(): BackupResult {
        return try {
            val characters = KtorDisneyApi.getCharacters(351..400).getOrNull() ?: emptyList()

            if (characters.isEmpty()) {
                return BackupResult.Error("Нет данных для создания резервной копии")
            }

            val formattedText = FormatBackup.format(characters, userNumber)
            val externalSaved = externalStorage.writeText(formattedText)
            if (!externalSaved) {
                return BackupResult.Error("Не удалось сохранить во внешнее хранилище")
            }

            delay(100)

            BackupResult.Success(
                externalFileInfo = externalStorage.getFileInfo(),
                hasInternalBackup = internalBackupStorage.fileExists()
            )
        } catch (e: Exception) {
            BackupResult.Error("Ошибка при создании резервной копии: ${e.message}")
        }
    }

    suspend fun restoreBackup(): BackupResult {
        return try {
            val backupText = internalBackupStorage.readText()
            if (backupText.isNullOrBlank()) {
                return BackupResult.Error("Резервная копия не найдена во внутреннем хранилище")
            }

            val externalSaved = externalStorage.writeText(backupText)
            if (!externalSaved) {
                return BackupResult.Error("Не удалось восстановить во внешнее хранилище")
            }

            internalBackupStorage.deleteFile()

            delay(100)

            BackupResult.Success(
                externalFileInfo = externalStorage.getFileInfo(),
                hasInternalBackup = internalBackupStorage.fileExists()
            )
        } catch (e: Exception) {
            BackupResult.Error("Ошибка при восстановлении резервной копии: ${e.message}")
        }
    }

    fun deleteExternalBackup(): BackupResult {
        return try {
            val backupText = externalStorage.readText()
            if (backupText.isNullOrBlank()) {
                return BackupResult.Error("Резервная копия не найдена во внешнем хранилище")
            }

            val internalSaved = internalBackupStorage.writeText(backupText)
            if (!internalSaved) {
                return BackupResult.Error("Не удалось сохранить во внутреннее хранилище")
            }

            val deleted = externalStorage.deleteFile()
            if (!deleted) {
                return BackupResult.Error("Не удалось удалить внешний файл")
            }

            BackupResult.Success(
                externalFileInfo = null,
                hasInternalBackup = internalBackupStorage.fileExists()
            )
        } catch (e: Exception) {
            BackupResult.Error("Ошибка при удалении резервной копии: ${e.message}")
        }
    }
}

sealed class BackupResult {
    data class Success(
        val externalFileInfo: FileInfo?,
        val hasInternalBackup: Boolean
    ) : BackupResult()

    data class Error(val message: String) : BackupResult()
}

