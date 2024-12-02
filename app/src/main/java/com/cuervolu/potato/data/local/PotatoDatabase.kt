package com.cuervolu.potato.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cuervolu.potato.data.local.dao.CharacterDao
import com.cuervolu.potato.data.local.dao.NoteDao
import com.cuervolu.potato.data.local.entity.CharacterConverters
import com.cuervolu.potato.data.local.entity.CharacterEntity
import com.cuervolu.potato.data.local.entity.NoteEntity

@Database(
    entities = [NoteEntity::class, CharacterEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(CharacterConverters::class)
abstract class PotatoDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun characterDao(): CharacterDao

    companion object {
        const val DATABASE_NAME = "potato_database"
    }
}