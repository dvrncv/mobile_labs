package com.example.mobile_labs.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DisneyCharacterDao {
    @Query("SELECT * FROM characters ORDER BY id ASC")
    fun getAllCharacters(): Flow<List<DisneyCharacterEntity>>

    @Query("SELECT * FROM characters WHERE id >= :startId AND id <= :endId ORDER BY id ASC")
    fun getCharactersInRange(startId: Int, endId: Int): Flow<List<DisneyCharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterById(id: Int): DisneyCharacterEntity?

    @Query("SELECT COUNT(*) FROM characters")
    suspend fun getCharacterCount(): Int

    @Query("SELECT COUNT(*) FROM characters WHERE id >= :startId AND id <= :endId")
    suspend fun getCharacterCountInRange(startId: Int, endId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: DisneyCharacterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<DisneyCharacterEntity>)

    @Update
    suspend fun updateCharacter(character: DisneyCharacterEntity)

    @Delete
    suspend fun deleteCharacter(character: DisneyCharacterEntity)

    @Query("DELETE FROM characters")
    suspend fun deleteAllCharacters()

    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun deleteCharacterById(id: Int)
}


