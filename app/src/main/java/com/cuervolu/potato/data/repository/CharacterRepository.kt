package com.cuervolu.potato.data.repository

import com.cuervolu.potato.data.local.dao.CharacterDao
import com.cuervolu.potato.data.local.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

class CharacterRepository(
    private val characterDao: CharacterDao
) {
    fun getAllCharacters(): Flow<List<CharacterEntity>> = characterDao.getAllCharacters()

    suspend fun getCharacterById(id: Int): CharacterEntity? = characterDao.getCharacterById(id)

    suspend fun insertCharacter(character: CharacterEntity): Long = characterDao.insertCharacter(character)

    suspend fun updateCharacter(character: CharacterEntity) = characterDao.updateCharacter(character)

    suspend fun deleteCharacter(character: CharacterEntity) = characterDao.deleteCharacter(character)

    suspend fun updateCharacterArchiveStatus(id: Int, isArchived: Boolean) =
        characterDao.updateCharacterArchiveStatus(id, isArchived)

    fun searchCharacters(query: String): Flow<List<CharacterEntity>> = characterDao.searchCharacters(query)
}