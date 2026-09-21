package com.sobuj.cheperakha.reader.model

/**
 * A chapter in the book with navigation info.
 */
data class Chapter(
    val id: String,
    val title: String,
    val href: String,
    val index: Int,
    val isCurrent: Boolean = false
)

/**
 * A bookmark saved by the user.
 */
data class Bookmark(
    val chapterId: String,
    val chapterTitle: String,
    val positionPercent: Float,
    val timestamp: Long
)

/**
 * User reading preferences persisted locally.
 */
data class ReadingSettings(
    val theme: Theme = Theme.LIGHT,
    val fontFamily: FontFamily = FontFamily.SANS_SERIF,
    val fontSize: Int = 17, // sp
    val lineSpacing: Float = 1.7f,
    val paragraphSpacing: Float = 1.0f,
    val textAlign: TextAlign = TextAlign.JUSTIFY
) {
    enum class Theme { LIGHT, SEPIA, DARK }
    enum class FontFamily { SERIF, SANS_SERIF }
    enum class TextAlign { LEFT, JUSTIFY }
}

/**
 * Book metadata extracted from EPUB.
 */
data class BookMetadata(
    val title: String,
    val author: String,
    val language: String,
    val coverBitmapDescriptor: String? = null,
    val description: String? = null
)

/**
 * Current reading state for persistence.
 */
data class ReadingState(
    val currentChapterIndex: Int = 0,
    val scrollOffset: Int = 0,
    val progressPercent: Float = 0f,
    val lastOpenedTimestamp: Long = System.currentTimeMillis()
)
