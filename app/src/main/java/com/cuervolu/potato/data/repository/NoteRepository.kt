package com.cuervolu.potato.data.repository

import android.net.Uri
import com.cuervolu.potato.data.local.dao.NoteDao
import com.cuervolu.potato.data.local.entity.NoteEntity
import com.cuervolu.potato.utils.ImageStorageService
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao,
    private val imageStorage: ImageStorageService
) {

    fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()

    suspend fun getNoteById(id: Int): NoteEntity? = noteDao.getNoteById(id)

    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)

    suspend fun updateNotePinStatus(id: Int, isPinned: Boolean) =
        noteDao.updateNotePinStatus(id, isPinned)

    suspend fun updateNoteArchiveStatus(id: Int, isArchived: Boolean) =
        noteDao.updateNoteArchiveStatus(id, isArchived)

    fun searchNotes(query: String): Flow<List<NoteEntity>> = noteDao.searchNotes(query)


    suspend fun saveNoteWithImage(note: NoteEntity, imageUri: Uri): Result<Long> {
        return try {
            val imageResult = imageStorage.saveImage(imageUri)

            imageResult.fold(
                onSuccess = { imagePath ->
                    val noteWithImage = note.copy(coverImage = imagePath)
                    insertNote(noteWithImage)
                },
                onFailure = { e ->
                    Result.failure(Exception("Error al guardar la imagen", e))
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertNote(note: NoteEntity): Result<Long> = try {
        Result.success(noteDao.insertNote(note))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateNote(note: NoteEntity): Result<Unit> = try {
        Result.success(noteDao.updateNote(note))
    } catch (e: Exception) {
        Result.failure(e)
    }
}