package com.cuervolu.potato.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(
    tableName = "characters",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["summary"])
    ]
)
@TypeConverters(CharacterConverters::class)
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val image: String?, // URL or path to the image
    val relationships: List<Relationship> = emptyList(),
    val likes: List<String> = emptyList(),
    val dislikes: List<String> = emptyList(),
    val summary: String,
    val history: String,
    val personality: String,
    val motivations: String,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)

data class Relationship(
    val characterId: Int,
    val relationshipType: String
)

class CharacterConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromRelationshipList(value: List<Relationship>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toRelationshipList(value: String): List<Relationship> {
        val listType = object : TypeToken<List<Relationship>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
}