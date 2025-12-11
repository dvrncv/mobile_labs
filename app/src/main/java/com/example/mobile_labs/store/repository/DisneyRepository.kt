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

    // Room database
    private val database: DisneyDatabase = DisneyDatabase.getDatabase(context)
    private val characterDao: DisneyCharacterDao = database.characterDao()

    /**
     * Получить Flow всех персонажей из базы данных (реактивное обновление)
     */
    fun getAllCharactersFlow(): Flow<List<DisneyCharacter>> {
        return characterDao.getAllCharacters().map { entities ->
            entities.map { it.toDisneyCharacter() }
        }
    }

    /**
     * Получить Flow персонажей до указанного номера включительно
     * Например, для userNumber = 8 и maxNumber = 8 вернет персонажей из диапазона 8 (351-400)
     * Для maxNumber = 9 вернет персонажей из диапазонов 8 и 9 (351-450)
     */
    fun getCharactersUpToNumberFlow(maxNumber: Int): Flow<List<DisneyCharacter>> {
        // Начало первого диапазона для userNumber: всегда 351
        // Для userNumber = 8: первый диапазон 351-400
        val startId = 351
        // Конец последнего диапазона: 350 + (maxNumber - userNumber) * 50 + 50
        // Для userNumber = 8, maxNumber = 8: 350 + 0 * 50 + 50 = 400
        // Для userNumber = 8, maxNumber = 9: 350 + 1 * 50 + 50 = 450
        val endId = 350 + (maxNumber - userNumber) * 50 + 50
        return characterDao.getCharactersInRange(startId, endId).map { entities ->
            entities.map { it.toDisneyCharacter() }
        }
    }

    /**
     * Холодный старт: всегда загружает первые 50 персонажей для текущего userNumber
     * Проверяет, есть ли персонажи из этого диапазона, и загружает их, если их нет
     */
    suspend fun coldStart(): Result<Unit> {
        return try {
            // Вычисляем диапазон ID для userNumber (первые 50 персонажей)
            // Для userNumber = 8: первый диапазон 351-400
            val startId = 351
            val endId = 400
            
            // Проверяем, сколько персонажей из этого диапазона уже есть в БД
            val countInRange = characterDao.getCharacterCountInRange(startId, endId)
            
            if (countInRange < 50) {
                // Персонажей из диапазона userNumber недостаточно - загружаем из API
                loadCharactersFromApi(userNumber)
            } else {
                // Персонажи уже загружены
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Обновить список персонажей: загружает из API и сохраняет в БД
     */
    suspend fun refreshCharacters(): Result<Unit> {
        return loadCharactersFromApi(userNumber)
    }

    /**
     * Загрузить персонажей по другому порядковому номеру
     */
    suspend fun loadCharactersByNumber(number: Int): Result<Unit> {
        return loadCharactersFromApi(number)
    }

    /**
     * Загрузить персонажей из API и сохранить в БД
     * Не перезаписывает существующие записи, чтобы сохранить локальные изменения
     */
    private suspend fun loadCharactersFromApi(number: Int): Result<Unit> {
        return try {
            // Вычисляем диапазон ID на основе порядкового номера
            // Формула: 350 + (number - userNumber) * 50 + 1
            // Для userNumber = 8, number = 8: 350 + 0 * 50 + 1 = 351..400
            // Для userNumber = 8, number = 9: 350 + 1 * 50 + 1 = 401..450
            val startId = 350 + (number - userNumber) * 50 + 1
            val endId = startId + 49
            val idRange = startId..endId

            val characters = KtorDisneyApi.getCharacters(idRange).getOrNull() ?: emptyList()

            if (characters.isNotEmpty()) {
                // Преобразуем в Entity
                val entities = characters.map { DisneyCharacterEntity.fromDisneyCharacter(it) }
                
                // Добавляем только тех персонажей, которых еще нет в БД
                // Это сохраняет локальные изменения пользователя
                entities.forEach { entity ->
                    val existing = characterDao.getCharacterById(entity.id)
                    if (existing == null) {
                        // Персонажа нет в БД - добавляем
                        characterDao.insertCharacter(entity)
                    }
                    // Если персонаж уже есть - не трогаем его (сохраняем локальные изменения)
                }
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Не удалось загрузить персонажей из API"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Получить количество персонажей в БД
     */
    suspend fun getCharacterCount(): Int {
        return characterDao.getCharacterCount()
    }

    // ========== CRUD OPERATIONS ==========

    // ========== CREATE (Вставка) ==========
    
    /**
     * Вставить одного персонажа в базу данных
     * @param character Персонаж для вставки
     * @return Result с успешным результатом или ошибкой
     */
    suspend fun insertCharacter(character: DisneyCharacter): Result<Unit> {
        return try {
            val entity = DisneyCharacterEntity.fromDisneyCharacter(character)
            characterDao.insertCharacter(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Вставить список персонажей в базу данных
     * @param characters Список персонажей для вставки
     * @return Result с успешным результатом или ошибкой
     */
    suspend fun insertCharacters(characters: List<DisneyCharacter>): Result<Unit> {
        return try {
            val entities = characters.map { DisneyCharacterEntity.fromDisneyCharacter(it) }
            characterDao.insertCharacters(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== READ (Выборка) ==========
    
    /**
     * Получить персонажа по ID
     * @param id ID персонажа
     * @return Result с персонажем или null, если не найден
     */
    suspend fun getCharacterById(id: Int): Result<DisneyCharacter?> {
        return try {
            val entity = characterDao.getCharacterById(id)
            val character = entity?.toDisneyCharacter()
            Result.success(character)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // ========== UPDATE (Обновление) ==========
    
    /**
     * Обновить персонажа в базе данных
     * @param character Персонаж с обновленными данными
     * @return Result с успешным результатом или ошибкой
     */
    suspend fun updateCharacter(character: DisneyCharacter): Result<Unit> {
        return try {
            val entity = DisneyCharacterEntity.fromDisneyCharacter(character)
            characterDao.updateCharacter(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== DELETE (Удаление) ==========
    
    /**
     * Удалить персонажа по ID
     * @param id ID персонажа для удаления
     * @return Result с успешным результатом или ошибкой
     */
    suspend fun deleteCharacterById(id: Int): Result<Unit> {
        return try {
            characterDao.deleteCharacterById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Удалить персонажа по объекту
     * @param character Персонаж для удаления
     * @return Result с успешным результатом или ошибкой
     */
    suspend fun deleteCharacter(character: DisneyCharacter): Result<Unit> {
        return try {
            val entity = DisneyCharacterEntity.fromDisneyCharacter(character)
            characterDao.deleteCharacter(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Удалить всех персонажей из базы данных
     * @return Result с успешным результатом или ошибкой
     */
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

