package com.cuervolu.potato.ui.screens.notes

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuervolu.potato.data.local.entity.NoteEntity
import com.cuervolu.potato.data.repository.NoteRepository
import com.cuervolu.potato.utils.error.ErrorHandler
import com.cuervolu.potato.utils.state.AppError
import com.cuervolu.potato.utils.state.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteEditorViewModel(
    private val noteRepository: NoteRepository,
    private val errorHandler: ErrorHandler
) : ViewModel() {
    private val _uiState = MutableStateFlow<UIState<NoteEntity?>>(UIState.Success(null))
    val uiState: StateFlow<UIState<NoteEntity?>> = _uiState

    private var currentNoteId: Int? = null
    private var lastSavedContent: String? = null
    private var lastSavedTitle: String? = null

    fun loadNote(id: Int) {
        viewModelScope.launch {
            _uiState.value = UIState.Loading
            try {
                val note = noteRepository.getNoteById(id)
                currentNoteId = note?.id
                lastSavedContent = note?.content
                lastSavedTitle = note?.title
                _uiState.value = UIState.Success(note)
            } catch (e: Exception) {
                _uiState.value = UIState.Error(AppError.DatabaseError(
                    message = "Error al cargar la nota",
                    cause = e
                ))
            }
        }
    }

    fun saveNote(title: String, content: String, newImageUri: Uri? = null) {
        if (title == lastSavedTitle && content == lastSavedContent && newImageUri == null) {
            return
        }

        viewModelScope.launch {
            try {
                val currentTime = System.currentTimeMillis()
                val currentNote = (_uiState.value as? UIState.Success)?.data

                val note = currentNote?.copy(
                    title = title.ifEmpty { "Nota sin título" },
                    content = content,
                    updatedAt = currentTime
                ) ?: NoteEntity(
                    id = currentNoteId ?: 0,
                    title = title.ifEmpty { "Nota sin título" },
                    content = content,
                    createdAt = currentTime,
                    updatedAt = currentTime
                )

                val result = if (newImageUri != null) {
                    noteRepository.saveNoteWithImage(note, newImageUri)
                } else {
                    if (currentNoteId == null) {
                        val insertResult = noteRepository.insertNote(note)
                        insertResult.also {
                            if (it.isSuccess) {
                                currentNoteId = it.getOrNull()?.toInt()
                            }
                        }
                    } else {
                        noteRepository.updateNote(note).map { note.id.toLong() }
                    }
                }

                result.fold(
                    onSuccess = { insertedId ->
                        lastSavedContent = content
                        lastSavedTitle = title
                        _uiState.value = UIState.Success(
                            note.copy(id = insertedId.toInt())
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = UIState.Error(
                            AppError.UnexpectedError(
                                message = "Error al guardar la nota",
                                cause = exception
                            )
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = UIState.Error(
                    AppError.UnexpectedError(
                        message = "Error inesperado",
                        cause = e
                    )
                )
            }
        }
    }

    fun onImageSelected(uri: Uri) {
        viewModelScope.launch {
            val currentNote = (_uiState.value as? UIState.Success)?.data
            if (currentNote != null) {
                _uiState.value = UIState.Loading

                val result = noteRepository.saveNoteWithImage(
                    currentNote.copy(
                        title = currentNote.title,
                        content = currentNote.content,
                        updatedAt = System.currentTimeMillis()
                    ),
                    uri
                )

                result.fold(
                    onSuccess = { noteId ->
                        val updatedNote = noteRepository.getNoteById(noteId.toInt())
                        _uiState.value = UIState.Success(updatedNote)
                    },
                    onFailure = { exception ->
                        _uiState.value = UIState.Error(
                            AppError.UnexpectedError(
                                message = "Error al guardar la imagen",
                                cause = exception
                            )
                        )
                    }
                )
            }
        }
    }
}