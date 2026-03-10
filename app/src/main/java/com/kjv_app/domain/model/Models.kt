package com.kjv_app.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "books")

data class Book(
    @PrimaryKey val id: Int,
    val name: String,
    val testament: String // "Old" or "New"
)

@Entity(
    tableName = "verses",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["bookId", "chapter"], name = "index_verses_bookId_chapter")
    ]
)
data class Verse(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Int,
    val chapter: Int,
    val verseNumber: Int,
    val text: String
)

@Entity(
    tableName = "commentaries",
    foreignKeys = [
        ForeignKey(
            entity = Verse::class,
            parentColumns = ["id"],
            childColumns = ["verseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["verseId"], name = "index_commentaries_verseId")
    ]
)
data class Commentary(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val verseId: Long,
    val author: String,
    val text: String
)