package com.cuervolu.potato.ui.screens.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuervolu.potato.data.local.entity.CharacterEntity
import com.cuervolu.potato.data.repository.CharacterRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

class CharactersViewModel(private val characterRepository: CharacterRepository) : ViewModel() {
    private val _characters = MutableStateFlow<List<CharacterEntity>>(emptyList())
    val characters: StateFlow<List<CharacterEntity>> = _characters

    init {
        loadCharacters()
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            characterRepository.getAllCharacters()
                .catch { e ->
                    // Handle error
                    logger.error(e) { "Error loading characters: $e" }
                }
                .collect { charactersList ->
                    _characters.value = charactersList
                }
        }
    }

    fun archiveCharacter(character: CharacterEntity) {
        viewModelScope.launch {
            characterRepository.updateCharacterArchiveStatus(character.id, true)
        }
    }
}