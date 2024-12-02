package com.cuervolu.potato.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuervolu.potato.data.local.entity.NoteEntity
import com.cuervolu.potato.data.repository.NoteRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

class HomeViewModel(private val noteRepository: NoteRepository) : ViewModel() {
    private val _notes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val notes: StateFlow<List<NoteEntity>> = _notes

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            noteRepository.getAllNotes()
                .catch { e ->
                    // Handle error
                    logger.error(e) { "Error loading notes: $e" }
                }
                .collect { notesList ->
                    _notes.value = notesList
                }
        }
    }

    fun toggleNotePinStatus(note: NoteEntity) {
        viewModelScope.launch {
            noteRepository.updateNotePinStatus(note.id, !note.isPinned)
        }
    }

    fun archiveNote(note: NoteEntity) {
        viewModelScope.launch {
            noteRepository.updateNoteArchiveStatus(note.id, true)
        }
    }
}